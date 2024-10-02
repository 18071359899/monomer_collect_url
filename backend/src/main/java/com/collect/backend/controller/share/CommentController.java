package com.collect.backend.controller.share;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Comment;
import com.collect.backend.domain.vo.req.CommentListReq;
import com.collect.backend.domain.vo.req.MessageListCommentReq;
import com.collect.backend.domain.vo.resp.CommentVo;
import com.collect.backend.domain.vo.resp.common.CommonBottomPageVo;
import com.collect.backend.service.share.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @PostMapping("/add/")
    public BaseResponse<Long> addComment(@Valid @RequestBody Comment comment){
        return commentService.addComment(comment);
    }

    @PostMapping("/delete/")
    public BaseResponse<CommentVo> deleteComment(@RequestBody  CommentVo commentVo){
        commentService.deleteComment(commentVo);
        return commentService.getCommentsByRootId(commentVo.getRootId());
    }

    @PostMapping("/update/")
    public BaseResponse<Boolean> updateComment(@Valid @RequestBody Comment comment){
        return commentService.updateShare(comment);
    }
    @GetMapping("/list/")
    public BaseResponse<CommonBottomPageVo> listComment(CommentListReq commentListReq){
        return commentService.listComment(commentListReq);
    }
    /**
     * 获取从开始到消息对应的评论数据
     */
    @GetMapping("/message/list/")
    public BaseResponse<CommonBottomPageVo<CommentVo>> messageListComment(MessageListCommentReq messageListCommentReq){
        return commentService.messageListComment(messageListCommentReq);
    }
    /**
     * 获取剩下的评论数据
     */
    @GetMapping("/message/list/last/")
    public BaseResponse<CommonBottomPageVo> messageLastListComment(MessageListCommentReq messageListCommentReq){
        return commentService.messageLastListComment(messageListCommentReq);
    }
}
