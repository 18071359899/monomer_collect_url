package com.collect.backend.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.Comment;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.mapper.CategoryMapper;
import com.collect.backend.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentDao extends ServiceImpl<CommentMapper, Comment> {
    public void deleteCommentByShareId(Long shareId) {
        QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
        commentQuery.eq("share_id",shareId);
        getBaseMapper().delete(commentQuery);
    }
}
