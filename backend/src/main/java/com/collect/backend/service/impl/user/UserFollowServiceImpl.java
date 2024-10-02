package com.collect.backend.service.impl.user;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ErrorCode;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.MessageNotifyTypeEnum;
import com.collect.backend.common.event.MessageNotifyEvent;
import com.collect.backend.dao.UserDao;
import com.collect.backend.dao.UserFollowDao;
import com.collect.backend.domain.entity.MessageNotify;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.entity.UserFollow;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.UserFollowVo;
import com.collect.backend.service.user.UserFollowService;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author Administrator
* @description 针对表【user_follow(用户关注的信息表)】的数据库操作Service实现
* @createDate 2024-04-10 23:37:46
*/
@Service
public class UserFollowServiceImpl implements UserFollowService{
    @Autowired
    private UserFollowDao userFollowDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public BaseResponse<String> add(UserFollow userFollow) {
        userFollow.setId(null);
        Date date = new Date();
        userFollow.setCreateTime(date);
        int insert = userFollowDao.getBaseMapper().insert(userFollow);
        if (insert > 0) {
            //更新用户表对应用户的关注数量和粉丝数量
            User user = userDao.getBaseMapper().selectById(userFollow.getUserId());
            user.setFollow(user.getFollow()+1);
            userDao.getBaseMapper().updateById(user);
            User followUser = userDao.getBaseMapper().selectById(userFollow.getFollowUserId());
            followUser.setFans(followUser.getFans()+1);
            userDao.getBaseMapper().updateById(followUser);
            //对关注用户进行消息通知
            applicationEventPublisher.publishEvent(new MessageNotifyEvent(this,new MessageNotify(
                    null,userFollow.getFollowUserId(),userFollow.getUserId(), MessageNotifyTypeEnum.USER_FOLLOW.getType(),null,false,date
            )));
            return ResultUtils.success("ok");
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR);
    }

    @Override
    public BaseResponse<String> delete(UserFollow userFollow) {
        int deleteById = userFollowDao.getBaseMapper().deleteById(userFollowDao.queryByUserIdAndFollowUserId
                (userFollow.getUserId(), userFollow.getFollowUserId()));
        if(deleteById > 0){
            //更新用户表对应用户的关注数量和粉丝数量
            User user = userDao.getBaseMapper().selectById(userFollow.getUserId());
            user.setFollow(user.getFollow()-1);
            userDao.getBaseMapper().updateById(user);
            User followUser = userDao.getBaseMapper().selectById(userFollow.getFollowUserId());
            followUser.setFans(followUser.getFans()-1);
            userDao.getBaseMapper().updateById(followUser);
            return ResultUtils.success("ok");
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR);
    }
    public List<UserFollowVo> getUserFollowVoList(List<UserFollow> records,String attributes){
        List<UserFollowVo> userFollowVoList = new ArrayList<>();
        Long loginUserId = ManageUserInfo.getUser().getId();
        records.forEach(value->{
            UserFollowVo userFollowVo = new UserFollowVo();
            BeanUtil.copyProperties(value,userFollowVo);
            //根据属性得到当前的用户id，如果查询的是关注，得到的是被关注用户的id，如果查询的是粉丝，得到的是关注用户的id
            Long followUserId = "user_id".equals(attributes) ? userFollowVo.getFollowUserId()
                    : userFollowVo.getUserId();
            //查询关注或粉丝用户信息
            User user = userDao.getBaseMapper().selectById(followUserId);
            userFollowVo.setPhoto(user.getPhoto());
            userFollowVo.setUsername(user.getUsername());
            userFollowVo.setIntroduction(StringUtils.isNotBlank(user.getIntroduction()) ? user.getIntroduction() : "暂无个人简介");
            //查询当前登录用户和关注或粉丝之间的关系
            UserFollow userFollow = userFollowDao.queryByUserIdAndFollowUserId(loginUserId, followUserId);
            //存在，可能是已关注或互粉
            if(Objects.nonNull(userFollow)){
                UserFollow followUser = userFollowDao.queryByUserIdAndFollowUserId(followUserId, loginUserId);
                if(Objects.nonNull(followUser)){  //互粉
                    userFollowVo.setLinkStatus(2);
                }else{   //已关注
                    userFollowVo.setLinkStatus(1);
                }
            }else{   //未关注
                if(!loginUserId.equals(followUserId))  //某个用户的关注和粉丝是否是当前登录用户,是：为null，否：为0
                userFollowVo.setLinkStatus(0);
            }
            userFollowVoList.add(userFollowVo);
        });
        return userFollowVoList;
    }
    @Override
    public BaseResponse<Page<UserFollowVo>> list(HomePageReq homePageReq, String attributes) {
        Long userId = homePageReq.getUserId();
        Page<UserFollow> queryPage = new Page(homePageReq.getPage(),homePageReq.getSize());
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        //根据不同的接口来查询不同的属性，但值都一样，比如查询关注列表，属性为 user_id，查询粉丝列表，属性为  follow_user_id
        queryWrapper.eq(attributes,userId);
        queryWrapper.orderByDesc("id");
        Page<UserFollow> userFollowPage = userFollowDao.getBaseMapper().selectPage(queryPage, queryWrapper);
        Page<UserFollowVo> userFollowVoPage = new Page<>(userFollowPage.getCurrent(),userFollowPage.getSize(),userFollowPage.getTotal());
        List<UserFollow> records = userFollowPage.getRecords();
        userFollowVoPage.setRecords(getUserFollowVoList(records,attributes));
        return ResultUtils.success(userFollowVoPage);
    }
}




