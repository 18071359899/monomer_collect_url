package com.collect.backend.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.common.enums.UserShareBehaviorISAddType;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.Comment;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.mapper.CategoryMapper;
import com.collect.backend.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.collect.backend.common.Constants.USER_BEHAVIOR_TOTAL_VALUE;

@Service
public class CommentDao extends ServiceImpl<CommentMapper, Comment> {
    public void deleteCommentByShareId(Long shareId) {
        QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
        commentQuery.eq("share_id",shareId);
        getBaseMapper().delete(commentQuery);
    }

    /**
     * 查询某个文章的总评论数
     * @param shareId
     * @return
     */
    public Integer queryCommentCntShare(Long shareId){
        return lambdaQuery().eq(Comment::getShareId, shareId).count();
    }

    public Boolean updateCommentAgree(Long commentId,Integer type){
        String  typeString = "-";  //增加或减少
        if(UserShareBehaviorISAddType.ADD_TYPE.getType() == type){
            typeString = "+";
        }
        return lambdaUpdate().eq(Comment::getId, commentId)
                .setSql("`agree`=`agree`" + typeString + USER_BEHAVIOR_TOTAL_VALUE).update();
    }
}
