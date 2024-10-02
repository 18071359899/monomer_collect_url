package com.collect.backend.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.entity.UserFollow;
import com.collect.backend.mapper.ShareMapper;
import com.collect.backend.mapper.UserFollowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFollowDao extends ServiceImpl<UserFollowMapper, UserFollow> {
    public UserFollow queryByUserIdAndFollowUserId(Long userId, Long followUserId) {
        return lambdaQuery().eq(UserFollow::getUserId, userId)
                .eq(UserFollow::getFollowUserId, followUserId)
                .one();
    }

    /**
     * 查询某个用户的所有粉丝
     * @param userId
     * @return
     */
    public List<UserFollow> queryUserAllFans(Long userId) {
        return lambdaQuery()
                .eq(UserFollow::getFollowUserId, userId)
                .list();
    }
}
