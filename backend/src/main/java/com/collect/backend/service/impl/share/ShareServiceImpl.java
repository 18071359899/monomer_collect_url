package com.collect.backend.service.impl.share;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.Constants;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.MessageNotifyTypeEnum;
import com.collect.backend.common.enums.UserShareBehaviorISAddType;
import com.collect.backend.common.enums.UserShareBehaviorType;
import com.collect.backend.common.event.DeleteShareEvent;
import com.collect.backend.common.event.MessageNotifyEvent;
import com.collect.backend.common.exception.BusinessException;
import com.collect.backend.common.exception.CommonErrorEnum;
import com.collect.backend.dao.ShareDao;
import com.collect.backend.dao.UserFollowDao;
import com.collect.backend.dao.UserShareBehaviorDao;
import com.collect.backend.domain.entity.*;
import com.collect.backend.domain.entity.mq_event.LikeQueueEntity;
import com.collect.backend.domain.vo.req.ShareUserBehaviorReq;
import com.collect.backend.domain.vo.req.common.CursorPageBaseReq;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.FollowShareListVo;
import com.collect.backend.domain.vo.resp.MessageNotifyVo;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.domain.vo.resp.common.CursorPageBaseResp;
import com.collect.backend.service.adpter.ShareAdpter;
import com.collect.backend.service.share.ShareService;
import com.collect.backend.utils.*;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.redis.RedisCommonFunc;
import com.collect.backend.utils.redis.RedisUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.collect.backend.common.Constants.*;
import static com.collect.backend.common.ErrorCode.OPERATION_ERROR;

@Service
@Slf4j
@Getter
public class ShareServiceImpl  implements ShareService {
    public static final int DETAILS_SHARE_TIME = 10;  //最大过期时间
    public static final int ARTICLE_NULL_TIME = 2;
    public static final int SHARE_LIST_TIME = 6;
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private UserFollowDao userFollowDao;
    @Autowired
    private UserShareBehaviorDao shareBehaviorDao;
    @Autowired
    private ShareAdpter shareAdpter;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public BaseResponse<Long> addShare(Share share) {
        Date date = new Date();
        Share insertShare = new Share(null, share.getTitle(), ManageUserInfo.getUser().getId(), share.getContent(), date, date);
        int insert = shareDao.getBaseMapper().insert(insertShare);
        if(insert > 0){
            Long userId = ManageUserInfo.getUser().getId();
            //查询该用户的所有粉丝
            List<UserFollow> userFollows = userFollowDao.queryUserAllFans(userId);
            Long newShareId = insertShare.getId();
            //推送当前内容给所有粉丝
            for (UserFollow userFollow : userFollows) {
                //获取粉丝id
                Long fanUserId = userFollow.getUserId();
                //推送给粉丝
                String key = FEED_KEY + fanUserId;
                RedisUtils.zAdd(key, newShareId,System.currentTimeMillis());
            }
            RedisUtils.del(ARTICLE_LIST_KEY);
            //布隆过滤器新增数据
            RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(ARTICLE_DETAILS_BL_KEY);
            bloomFilter.add(newShareId);
            return ResultUtils.success(newShareId);
        }
        return ResultUtils.error(OPERATION_ERROR);
    }

    @Override
    @Transactional
    public BaseResponse<Boolean> updateShare(Share share) {
        Long id = share.getId();
        if(Objects.isNull(id)){
            return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getErrorCode(),"id不能为空");
        }
        Date date = new Date();
        Share updateShare = new Share(
                id, share.getTitle(), ManageUserInfo.getUser().getId(), share.getContent(), null, date
        );
        int update = shareDao.getBaseMapper().updateById(updateShare);
        if(update > 0){
            RedisUtils.del(getShareDetailsKey(id));
            RedisUtils.del(ARTICLE_LIST_KEY);
            return ResultUtils.success(true);
        }
        return ResultUtils.error(OPERATION_ERROR);
    }
    public CursorPageBaseResp<ShareVo> getCursorPageBaseResp(CursorPageBaseReq cursorPageBaseReq){
        CursorPageBaseResp<Share> cursorPageBaseResp =  shareDao.getSharePage(cursorPageBaseReq);
        if (CollectionUtils.isEmpty(cursorPageBaseResp.getList())) {
            return CursorPageBaseResp.empty();
        }
        List<Share> records = cursorPageBaseResp.getList();
        CursorPageBaseResp<ShareVo> init = CursorPageBaseResp.init(cursorPageBaseResp,
                shareAdpter.getShareVoList(records));
        List<ShareVo> list = init.getList();
        for (ShareVo shareVo : list) {
            RedisUtils.zAddToStr(ARTICLE_LIST_KEY,shareVo,shareVo.getId());
        }
        return init;
    }
    @Override
    public CursorPageBaseResp<ShareVo> listShare(CursorPageBaseReq cursorPageBaseReq) {
        String cursor = cursorPageBaseReq.getCursor();
        CursorPageBaseResp<ShareVo> cursorPageBaseResp = RedisCommonFunc.addCacheCommonFun(redissonClient, new Supplier<CursorPageBaseResp>() {
                    @Override
                    public CursorPageBaseResp get() {
                        return RedisCommonFunc.getCursorDataByRedis(
                                cursorPageBaseReq, ARTICLE_LIST_KEY, ShareVo.class, ShareVo::getId
                        );
                    }
                },
                getShareListLockKey(cursor), new Supplier<CursorPageBaseResp>() {
                    @Override
                    public CursorPageBaseResp<ShareVo> get() {
                        CursorPageBaseResp<ShareVo> cursorPageBaseResp = getCursorPageBaseResp(cursorPageBaseReq);
                        if (CollectionUtils.isEmpty(cursorPageBaseResp.getList())) {
                            return CursorPageBaseResp.empty();
                        }
                        return cursorPageBaseResp;
                    }
                });
        List<ShareVo> respList = cursorPageBaseResp.getList();
        Long userId = ManageUserInfo.getUser().getId();
        for (ShareVo shareVo : respList) {
            shareAdpter.getShareVo(shareVo,userId);
        }
        return cursorPageBaseResp;
    }

    @Override
    public BaseResponse<Share> getShare(Long id){
        if(Objects.isNull(id)){
            return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getErrorCode(),"id不能为空");
        }
        Share share = shareDao.getById(id);
        AssertUtil.isNotEmpty(share,"分享页面不存在");
        return ResultUtils.success(share);
    }

    @Override
    public BaseResponse<String> userBehavior(ShareUserBehaviorReq shareUserBehaviorReq) {
        AssertUtil.isNotEmpty(shareUserBehaviorReq,"参数为空");
        Long userId = ManageUserInfo.getUser().getId();
        Long shareId = shareUserBehaviorReq.getId();
        UserShareBehavior userShareBehavior = new UserShareBehavior(null,userId,
                UserShareBehaviorType.SHARE_AGREE.getType(),new Date(), shareId
                );
        Integer type = shareUserBehaviorReq.getType();
        UserShareBehaviorType byType = UserShareBehaviorType.getByType(type);
        MessageNotify messageNotify  = null;
        Integer isAdd = shareUserBehaviorReq.getIsAdd();
        switch (byType) {
            case SHARE_AGREE:
                LikeQueueEntity likeQueueEntity = new LikeQueueEntity();
                BeanUtil.copyProperties(userShareBehavior,likeQueueEntity);
                likeQueueEntity.setIsAdd(isAdd);
                rabbitTemplate.convertAndSend("collect.like.direct","lcwyyds",likeQueueEntity);
                break;
                //收藏不走消息队列
            case SHARE_COLLECT:
                Share share = shareDao.getBaseMapper().selectById(shareId);
                if(Objects.isNull(share)) throw new BusinessException("文章内容不存在");
                messageNotify = new MessageNotify(null,
                        share.getUserId(),userId,null,share.getId(), false,new Date());

                if(isAdd == UserShareBehaviorISAddType.ADD_TYPE.getType()){
                    userShareBehavior.setType(UserShareBehaviorType.SHARE_COLLECT.getType());
                    shareBehaviorDao.getBaseMapper().insert(userShareBehavior);
                }else if(isAdd == UserShareBehaviorISAddType.DELETE_TYPE.getType()){
                    shareBehaviorDao.getBaseMapper().deleteById(
                            shareBehaviorDao.queryByUserIdAndTypeAndShareId(userId, shareUserBehaviorReq.getType(),share.getId()));
                }
                RedisUtils.hdel(Constants.getArticleCountHashKey(shareId), ARTICLE_COUNT_HASH_COLLECT_CNT);
                messageNotify.setType(MessageNotifyTypeEnum.USER_COLLECT_ARTICLE.getType());
                break;
        }

        if(isAdd == UserShareBehaviorISAddType.ADD_TYPE.getType()
        && shareUserBehaviorReq.getType() == UserShareBehaviorType.SHARE_COLLECT.getType()){  //用户收藏帖子走内置event事件
            applicationEventPublisher.publishEvent(new MessageNotifyEvent(this,messageNotify));
        }
        return  ResultUtils.success("ok");
    }

    @Override
    public BaseResponse<Page<ShareVo>> getInfoShare(HomePageReq homePageReq) {
        Page<Share> pagination = new Page<>(homePageReq.getPage(),homePageReq.getSize());
        QueryWrapper<Share> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",homePageReq.getUserId());
        queryWrapper.orderByDesc("id");
        Page<Share> sharePage = shareDao.getBaseMapper().selectPage(pagination, queryWrapper);
        List<Share> records = sharePage.getRecords();
        Page<ShareVo> shareVoPage = new Page<>(sharePage.getCurrent(),sharePage.getSize(),sharePage.getTotal());
        shareVoPage.setRecords(shareAdpter.getShareVoList(records));
        return ResultUtils.success(shareVoPage);
    }

    @Override
    public BaseResponse<String> deleteShare(Long id) {
        AssertUtil.isNotEmpty(id,"编号不能为空");
        int deleteById = shareDao.getBaseMapper().deleteById(id);
        if(deleteById > 0){
            // 将跟这个帖子有关的消息通知删掉
            MessageNotify messageNotify = new MessageNotifyVo();
            messageNotify.setBusinessId(id);
            Share share = new Share();
            share.setId(id);
            applicationEventPublisher.publishEvent(new MessageNotifyEvent(this,messageNotify));
            applicationEventPublisher.publishEvent(new DeleteShareEvent(this,share));
            RedisUtils.del(getShareDetailsKey(id));
            RedisUtils.del(ARTICLE_LIST_KEY);
            return ResultUtils.success("ok");
        }
        return ResultUtils.error(OPERATION_ERROR);
    }
    public String getShareDetailsKey(Long id){
        return ARTICLE_DETAILS_KEY + id;
    }

    public String getShareListKey(String cursor){
        StringBuilder key = new StringBuilder(ARTICLE_LIST_KEY);
        if(StringUtils.isNotBlank(cursor)){
            key.append(cursor);
        }
        return key.toString();
    }
    public String getShareListLockKey(String cursor){
        StringBuilder key = new StringBuilder(ARTICLE_LIST_LOCK_KEY);
        if(StringUtils.isNotBlank(cursor)){
            key.append(cursor);
        }
        return key.toString();
    }
    @Autowired
    private RedissonClient redissonClient;
    public String getDetailsLockKey(Long id){
        return ARTICLE_DETAILS_LOCK_KEY + id;
    }
    public long getRandomTimeMin(long time){  //设置随机时间来解决缓存雪崩问题
       return RandomUtil.randomLong(0, time - 4) + time - 5; //在time - 5 到 time 这个区间内
    }
    @Override
    public ShareVo getDetailShare(Long shareId) {
        AssertUtil.isNotEmpty(shareId,"编号不能为空");
        Long userId = ManageUserInfo.getUser().getId();
        //更新阅读量
        String readingKey = RedisUtils.getStr(shareReadingKey + shareId + ":userId:" + userId);
        if(StringUtils.isBlank(readingKey)){
            shareAdpter.getReadCountDao().updateReadCount(shareId);
            //同步缓存
            RedisUtils.hdel(Constants.getArticleCountHashKey(shareId),Constants.ARTICLE_COUNT_HASH_READING_CNT);
            RedisUtils.set(shareReadingKey + shareId + ":userId:" + userId,1,READING_EXPIRIION, TimeUnit.DAYS);
        }
        ShareVo shareVo = RedisCommonFunc.addCacheCommonFunForBloom
                (redissonClient, getShareDetailsKey(shareId), getDetailsLockKey(shareId), ShareVo.class
                        , new Supplier<ShareVo>() {
                            @Override
                            public ShareVo get() {
                                //走mysql
                                Share share = shareDao.getById(shareId);
                                if (Objects.isNull(share)) {
                                    //缓存空对象解决
//                            stringRedisTemplate.opsForValue().set(getShareDetailsKey(id),"", ARTICLE_NULL_TIME,TimeUnit.MINUTES);
                                    throw new BusinessException("分享页面不存在");
                                }
                                ShareVo shareVoUserInfo = shareAdpter.getShareVoUserInfo(share);
                                RedisUtils.set(getShareDetailsKey(shareId), share, getRandomTimeMin(DETAILS_SHARE_TIME), TimeUnit.MINUTES);  //设置缓存
                                return shareVoUserInfo;
                            }
                        }, shareId);
        return shareAdpter.getShareVo(shareVo, userId);
    }

    @Override
    public BaseResponse<FollowShareListVo> followListShare(Long max, Integer offset,Integer count) {
        Long userId = ManageUserInfo.getUser().getId();
        //查询收件箱
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                RedisUtils.zReverseRangeByScoreWithScores(key, 0, max, offset, count);
        if(typedTuples == null || typedTuples.isEmpty()){  //非空判断
            return ResultUtils.success(null);
        }
        //解析收件箱，拿到分享内容id
        List<Long> ids = new ArrayList<>();
        long lastTime = 0;  //计算下一次查询的最大时间
        int  lastOffset = 1;  //跳过重复的数据参数
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {  //5 4 4 2 2
            ids.add(Long.valueOf(typedTuple.getValue()));
            long time = typedTuple.getScore().longValue();
            if(time == lastTime){  //时间重复
                lastOffset++;
            }else{   // 未重复，重新赋值
                lastTime = time;
                lastOffset = 1;
            }
        }
        //查询分享内容数据
        List<Share> shares = shareDao.selectShareListByIds(ids);
        List<ShareVo> shareVoList = shareAdpter.getShareVoList(shares);
        //封装返回数据
        return ResultUtils.success(new FollowShareListVo(shareVoList,
                lastTime,lastOffset));
    }
}
