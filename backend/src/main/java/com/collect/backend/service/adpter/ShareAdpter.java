package com.collect.backend.service.adpter;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.collect.backend.common.Constants;
import com.collect.backend.common.exception.BusinessException;
import com.collect.backend.dao.*;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.utils.ManageUserInfo;
import com.collect.backend.utils.json.JsonUtils;
import com.collect.backend.utils.redis.RedisCommonFunc;
import com.collect.backend.utils.redis.RedisUtils;
import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@Slf4j
public class ShareAdpter {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserShareBehaviorDao shareBehaviorDao;
    @Autowired
    private ReadCountDao readCountDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserAgreeShareDao userAgreeShareDao;
    @Autowired
    private UserShareBehaviorDao userShareBehaviorDao;
    @Autowired
    private RedissonClient redissonClient;

    public ReadCountDao getReadCountDao() {
        return readCountDao;
    }

    /**
     * 设置文章相关用户信息
     * @param share
     */
    public ShareVo getShareVoUserInfo(Share share){
        ShareVo shareVo = new ShareVo();
        BeanUtil.copyProperties(share,shareVo);
        //查询用户信息
        User user = userDao.getBaseMapper().selectById(shareVo.getUserId());
        shareVo.setPhoto(user.getPhoto());
        shareVo.setUsername(user.getUsername());
        return shareVo;
    }

    /**
     * 是否需要从db中查询总数到redis中，false：不需要，true 需要
     * @param articleCount
     * @return
     */
    public  Boolean isFindCountDb(Map<Object, Object> articleCount){
        Boolean isFind = true;
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_READING_CNT))){
            isFind = false;
        }
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_LIKE_CNT))){
            isFind = false;
        }
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_COMMENT_CNT))){
            isFind = false;
        }
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_COLLECT_CNT))){
            isFind = false;
        }
        return isFind;
    }

    /**
     * 将需要从db中查询总数的数据查询并返回
     * @param articleCount
     * @return
     */
    public  void selectFromDbToReturn(Map<Object, Object> articleCount,Long shareId){
        String articleCountHashKey = Constants.getArticleCountHashKey(shareId);
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_READING_CNT))){
            String readCnt = readCountDao.getReadCnt(shareId).toString();
            articleCount.put(Constants.ARTICLE_COUNT_HASH_READING_CNT,readCnt);
            RedisUtils.hset(articleCountHashKey,Constants.ARTICLE_COUNT_HASH_READING_CNT,readCnt);
        }
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_LIKE_CNT))){
            String likeCnt = userAgreeShareDao.getReadCnt(shareId).toString();
            articleCount.put(Constants.ARTICLE_COUNT_HASH_LIKE_CNT, likeCnt);
            RedisUtils.hset(articleCountHashKey,Constants.ARTICLE_COUNT_HASH_LIKE_CNT,likeCnt);
        }
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_COMMENT_CNT))){
            String commentCnt = commentDao.queryCommentCntShare(shareId).toString();
            articleCount.put(Constants.ARTICLE_COUNT_HASH_COMMENT_CNT,commentCnt);
            RedisUtils.hset(articleCountHashKey,Constants.ARTICLE_COUNT_HASH_COMMENT_CNT,commentCnt);
        }
        if(Objects.isNull(articleCount.get(Constants.ARTICLE_COUNT_HASH_COLLECT_CNT))){
            String collectCnt = userShareBehaviorDao.queryCollectCntShare(shareId).toString();
            articleCount.put(Constants.ARTICLE_COUNT_HASH_COLLECT_CNT,collectCnt);
            RedisUtils.hset(articleCountHashKey,Constants.ARTICLE_COUNT_HASH_COLLECT_CNT,collectCnt);
        }
        RedisUtils.expire(articleCountHashKey,5, TimeUnit.MINUTES);
    }
    public Map<Object, Object> addLockHashCount(Long shareId){
        RLock lock = redissonClient.getLock(Constants.getArticleCountHashLockKey(shareId));
        try {
            lock.lock();
        } catch (Throwable e) {
            log.error("获取分布式锁出问题",e);
            throw new BusinessException("获取数据失败");
        }
        try {
            Map<Object, Object> articleCount = RedisUtils.hmget(Constants.getArticleCountHashKey(shareId));
            if(isFindCountDb(articleCount)){
                return articleCount;
            }
            selectFromDbToReturn(articleCount,shareId);
            return articleCount;
        }finally {
            // 只能释放自己的锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 将计数信息设置到返回的对象中
     * @param shareVo
     * @param articleCount
     */
    public void setCountToShareVo(ShareVo shareVo,Map<Object, Object> articleCount){
        shareVo.setReadingCnt(Integer.parseInt((String) articleCount.get(Constants.ARTICLE_COUNT_HASH_READING_CNT)));
        shareVo.setLikeCnt(Long.parseLong((String) articleCount.get(Constants.ARTICLE_COUNT_HASH_LIKE_CNT)));
        shareVo.setCommentCnt(Integer.parseInt((String) articleCount.get(Constants.ARTICLE_COUNT_HASH_COMMENT_CNT)));
        shareVo.setCollectCnt(Integer.parseInt((String) articleCount.get(Constants.ARTICLE_COUNT_HASH_COLLECT_CNT)));
    }
    /**
     * 设置文章与当前用户的信息、缓存：计数
     * @param shareVo
     * @param userId
     * @return
     */
    public ShareVo getShareVo(ShareVo shareVo, Long userId) {
        Long shareId = shareVo.getId();
        //查询当前用户收否点赞收藏
        shareVo.setIsLike(shareBehaviorDao.queryIsLikeShare(userId,shareVo.getId()));
        shareVo.setIsCollect(shareBehaviorDao.queryIsCollectShare(userId,shareVo.getId()));
        //查询该文章对应的总数：添加缓存方式
        Map<Object, Object> articleCount = RedisUtils.hmget(Constants.getArticleCountHashKey(shareId));
        if(!isFindCountDb(articleCount)){  //需要查询DB，分布式锁保证只有一个线程查询DB
            articleCount = addLockHashCount(shareId);
        }
        setCountToShareVo(shareVo,articleCount);
        return shareVo;
    }

    public List<ShareVo> getShareVoList(List<Share> records){
        List<ShareVo> shareVoList = new ArrayList<>();
        records.forEach(value->{
            shareVoList.add(getShareVoUserInfo(value));
        });
        return shareVoList;
    }
}
