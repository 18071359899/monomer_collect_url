package com.collect.backend.service.share;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Comment;
import com.collect.backend.domain.vo.req.CommentListReq;
import com.collect.backend.domain.vo.req.MessageListCommentReq;
import com.collect.backend.domain.vo.resp.CommentVo;
import com.collect.backend.domain.vo.resp.common.CommonBottomPageVo;

public interface CommentService {
    BaseResponse<Long> addComment(Comment comment);

    void deleteComment(CommentVo comment);

    BaseResponse<Boolean> updateShare(Comment comment);

    BaseResponse<CommonBottomPageVo> listComment(CommentListReq commentListReq);

    BaseResponse<CommentVo> getCommentsByRootId(Long rootId);

    BaseResponse<CommonBottomPageVo<CommentVo>> messageListComment(MessageListCommentReq messageListCommentReq);

    BaseResponse<CommonBottomPageVo> messageLastListComment(MessageListCommentReq messageListCommentReq);
}
