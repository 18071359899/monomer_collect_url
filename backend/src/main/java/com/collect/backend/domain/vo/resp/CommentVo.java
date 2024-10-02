package com.collect.backend.domain.vo.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.collect.backend.domain.entity.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo extends Comment {
    /**
     * 评论用户的头像
     */
    private String userPhoto;
    /**
     * 评论用户的用户名
     */
    private String userUsername;
    /**
     * 回复某个评论用户的用户名
     */
    private String toUserUsername;
    /**
     * 当前用户是否点赞了该评论
     */
    private Integer isLike;
    /**
     *  子评论列表
     */
    private List<CommentVo> children;
}
