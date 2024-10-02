package com.collect.backend.domain.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessageListCommentReq extends CommentListReq{
    @NotNull(message = "评论编号不能为空")
    private Long commentId;  //要查找的评论id
}
