package com.collect.backend.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.common.enums.UserShareBehaviorType;
import com.collect.backend.domain.entity.UserShareBehavior;
import com.collect.backend.mapper.UserShareBehaviorMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_share_behavior(用户对帖子的行为：点赞，收藏)】的数据库操作Service实现
* @createDate 2024-04-09 21:30:33
*/
@Service
public class UserShareBehaviorDao extends ServiceImpl<UserShareBehaviorMapper,UserShareBehavior>{
    /**
     * 查询当前用户是否点赞了该帖子
     */
    public Integer queryIsLikeShare(Long userId,Long shareId){
       return  lambdaQuery().eq(UserShareBehavior::getUserId,userId)
               .eq(UserShareBehavior::getRelationId,shareId)
               .eq(UserShareBehavior::getType, UserShareBehaviorType.SHARE_AGREE.getType()).list().size() > 0 ?  1 : 0;
    }
    /**
     * 查询当前用户是否收藏了该帖子
     */
    public Integer queryIsCollectShare(Long userId,Long shareId){
        return  lambdaQuery().eq(UserShareBehavior::getUserId,userId)
                .eq(UserShareBehavior::getRelationId,shareId)
                .eq(UserShareBehavior::getType,UserShareBehaviorType.SHARE_COLLECT.getType()).list().size() > 0 ? 1 : 0;
    }
    /**
     * 查询当前用户是否点赞了该评论
     */
    public Integer queryIsLikeComment(Long userId,Long commentId){
        return  lambdaQuery().eq(UserShareBehavior::getUserId,userId)
                .eq(UserShareBehavior::getRelationId,commentId)
                .eq(UserShareBehavior::getType,UserShareBehaviorType.COMMENT_AGREE.getType()).list().size() > 0 ? 1 : 0;
    }
    /**
     * 通过用户id、类型和帖子id查询是否有该数据
     */
    public UserShareBehavior queryByUserIdAndTypeAndShareId(Long userId, Integer type,Long shareId) {
        return  lambdaQuery().eq(UserShareBehavior::getUserId,userId)
                .eq(UserShareBehavior::getType,type)
                .eq(UserShareBehavior::getRelationId,shareId).
                one();
    }
    /**
     * 查询用户是否点赞了该评论
     */
    public UserShareBehavior queryByUserIdAndTypeAndCommentId(Long userId, Integer type, Long id) {
        return  lambdaQuery().eq(UserShareBehavior::getUserId,userId)
                .eq(UserShareBehavior::getType,type)
                .eq(UserShareBehavior::getRelationId,id).
                one();
    }
}




