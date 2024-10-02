package com.collect.backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.MessageNotifyTypeEnum;
import com.collect.backend.common.enums.UserShareBehaviorISAddType;
import com.collect.backend.common.enums.UserShareBehaviorType;
import com.collect.backend.common.event.MessageNotifyEvent;
import com.collect.backend.common.exception.CommonErrorEnum;
import com.collect.backend.dao.CommentDao;
import com.collect.backend.dao.ShareDao;
import com.collect.backend.dao.UserDao;
import com.collect.backend.dao.UserShareBehaviorDao;
import com.collect.backend.domain.entity.*;
import com.collect.backend.domain.vo.req.ShareUserBehaviorReq;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.service.UserShareBehaviorService;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.collect.backend.common.Constants.USER_BEHAVIOR_TOTAL_VALUE;

@Service
public class UserShareBehaviorServiceImpl implements UserShareBehaviorService {
    @Autowired
    private UserShareBehaviorDao userShareBehaviorDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public BaseResponse<Page<ShareVo>> listCollect(HomePageReq homePageReq) {
        Long userId = homePageReq.getUserId();
        QueryWrapper<UserShareBehavior> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("type",1);
        //1.先查询到用户收藏的所有帖子
        Page<UserShareBehavior> userShareBehaviorPage = userShareBehaviorDao.getBaseMapper().selectPage(new Page<>(homePageReq.getPage(), homePageReq.getSize()), queryWrapper);
        List<UserShareBehavior> records = userShareBehaviorPage.getRecords();
        Page<ShareVo> shareVoPage = new Page<>(userShareBehaviorPage.getCurrent(),userShareBehaviorPage.getSize(),userShareBehaviorPage.getTotal());
        //2.根据得到的信息查询帖子列表
        shareVoPage.setRecords(getShareVoList(records));
        return ResultUtils.success(shareVoPage);
    }

    @Override
    public BaseResponse<Integer> shareBehaviorComment(ShareUserBehaviorReq shareUserBehaviorReq) {
        if(Objects.isNull(shareUserBehaviorReq)){
            return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getErrorCode(),"参数为空");
        }
        Comment comment = commentDao.getBaseMapper().selectById(shareUserBehaviorReq.getId());
        Long userId = ManageUserInfo.getUser().getId();
        Date date = new Date();
        UserShareBehavior userShareBehavior = new UserShareBehavior(null,userId,
                UserShareBehaviorType.COMMENT_AGREE.getType(), date,shareUserBehaviorReq.getId()
        );
        MessageNotify messageNotify = new MessageNotify(null,comment.getUserId(),userId, MessageNotifyTypeEnum.USER_AGREE_COMMENT.getType(),comment.getId(),false,date);
        if(shareUserBehaviorReq.getIsAdd() == UserShareBehaviorISAddType.ADD_TYPE.getType()){
            comment.setAgree(comment.getAgree() + USER_BEHAVIOR_TOTAL_VALUE);
        }else if(shareUserBehaviorReq.getIsAdd() == UserShareBehaviorISAddType.DELETE_TYPE.getType()){
            comment.setAgree(comment.getAgree() - USER_BEHAVIOR_TOTAL_VALUE);
        }
        int updateById = commentDao.getBaseMapper().updateById(comment);
        if(updateById > 0){  //总量更新成功后再更新用户行为表
            if(shareUserBehaviorReq.getIsAdd() == UserShareBehaviorISAddType.ADD_TYPE.getType()){
                userShareBehaviorDao.getBaseMapper().insert(userShareBehavior);
                applicationEventPublisher.publishEvent(new MessageNotifyEvent(this,messageNotify));
            }else if(shareUserBehaviorReq.getIsAdd() == UserShareBehaviorISAddType.DELETE_TYPE.getType()){
                userShareBehaviorDao.getBaseMapper().deleteById(
                        userShareBehaviorDao.queryByUserIdAndTypeAndCommentId(userId,
                                UserShareBehaviorType.COMMENT_AGREE.getType(),comment.getId()));
            }
        }
        Integer resltCount = comment.getAgree();
        return  ResultUtils.success(resltCount);
    }

    private List<ShareVo> getShareVoList(List<UserShareBehavior> records) {
        List<ShareVo> shareVoList = new ArrayList<>();
        Long userId = ManageUserInfo.getUser().getId();
        records.forEach(value->{
            ShareVo shareVo = new ShareVo();
            Share share = shareDao.getBaseMapper().selectById(value.getRelationId());
            BeanUtil.copyProperties(share,shareVo);
            //查询用户信息
            User user = userDao.getBaseMapper().selectById(shareVo.getUserId());
            if(Objects.nonNull(user)){
                shareVo.setPhoto(user.getPhoto());
                shareVo.setUsername(user.getUsername());
            }
            //查询当前用户收否点赞收藏
            shareVo.setIsLike(userShareBehaviorDao.queryIsLikeShare(userId,shareVo.getId()));
            shareVo.setIsCollect(userShareBehaviorDao.queryIsCollectShare(userId,shareVo.getId()));
            shareVoList.add(shareVo);
        });
        return shareVoList;
    }
}
