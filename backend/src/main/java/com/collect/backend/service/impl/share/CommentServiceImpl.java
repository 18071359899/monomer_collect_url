package com.collect.backend.service.impl.share;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.MessageNotifyTypeEnum;
import com.collect.backend.common.event.MessageNotifyEvent;
import com.collect.backend.common.exception.BusinessException;
import com.collect.backend.dao.CommentDao;
import com.collect.backend.dao.ShareDao;
import com.collect.backend.dao.UserDao;
import com.collect.backend.dao.UserShareBehaviorDao;
import com.collect.backend.domain.entity.*;
import com.collect.backend.domain.vo.req.CommentListReq;
import com.collect.backend.domain.vo.req.MessageListCommentReq;
import com.collect.backend.domain.vo.resp.CommentVo;
import com.collect.backend.domain.vo.resp.MessageNotifyVo;
import com.collect.backend.domain.vo.resp.common.CommonBottomPageVo;
import com.collect.backend.service.share.CommentService;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.collect.backend.common.ErrorCode.OPERATION_ERROR;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private UserShareBehaviorDao userShareBehaviorDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public BaseResponse<Long> addComment(Comment comment) {
        AssertUtil.isEmpty(comment.getId(),"编号应为空");
        comment.setCreateTime(new Date());
        comment.setAgree(0);
        int insert = commentDao.getBaseMapper().insert(comment);
        if(insert > 0){
            Share share = shareDao.getBaseMapper().selectById(comment.getShareId());
            //进行消息通知
            if(!comment.getFatherId().equals(0L))  //不是一级评论
            applicationEventPublisher.publishEvent(new MessageNotifyEvent(this,new MessageNotify(
                    null,comment.getToUserId(),comment.getUserId(), MessageNotifyTypeEnum.USER_REPLY_COMMENT.getType(),comment.getId(),false,new Date())
            ));
            if(!comment.getToUserId().equals(share.getUserId()))  //回复的评论不是文章的作者
            applicationEventPublisher.publishEvent(new MessageNotifyEvent(this,new MessageNotify(
                    null,share.getUserId(),comment.getUserId(), MessageNotifyTypeEnum.USER_COMMENT_ARTICLE.getType(),comment.getId(),false,new Date())
            ));
            return ResultUtils.success(comment.getId());
        }
        return ResultUtils.error(OPERATION_ERROR);
    }

    @Override
    public void deleteComment(CommentVo comment) {
        Queue<CommentVo> queue = new LinkedList<>();
        queue.offer(comment);
        Integer deleteCommentCount = 0;
        MessageNotify messageNotify = new MessageNotifyVo();
        //使用队列将当前这个评论底下的所有回复评论删掉
        while (!queue.isEmpty()){
            CommentVo currComment = queue.poll();
            int deleteById = commentDao.getBaseMapper().deleteById(currComment.getId());
            if(deleteById < 0)  {
                throw  new BusinessException("删除失败");
            }
            messageNotify.setBusinessId(currComment.getId());
            applicationEventPublisher.publishEvent(new
                    MessageNotifyEvent(this,messageNotify));
            deleteCommentCount++;
            List<CommentVo> commentVoList = currComment.getChildren();
            if(Objects.nonNull(commentVoList)){
                for(CommentVo sonComment: commentVoList){
                    queue.offer(sonComment);
                }
            }
        }
    }

    @Override
    public BaseResponse<Boolean> updateShare(Comment comment) {
        AssertUtil.isNotEmpty(comment.getId(),"编号应不为空");
        int update = commentDao.getBaseMapper().updateById(comment);
        if(update > 0){
            return ResultUtils.success(true);
        }
        return ResultUtils.error(OPERATION_ERROR);
    }
    //将评论人、回复评论的人的用户信息加入到对象中去
    public List<CommentVo> getCommentVoList(List<Comment> commentList){
        List<CommentVo> commentVoList = new ArrayList<>();
        for(Comment comment: commentList){
            commentVoList.add(getCommentVo(comment));
        }
        return commentVoList;
    }
    public CommentVo getCommentVo(Comment comment){
        if(comment == null){
            return null;
        }
        CommentVo commentVo = new CommentVo();
        BeanUtil.copyProperties(comment,commentVo);
        User user = userDao.getBaseMapper().selectById(comment.getUserId());
        commentVo.setUserPhoto(user.getPhoto());
        commentVo.setUserUsername(user.getUsername());
        commentVo.setIsLike(userShareBehaviorDao.queryIsLikeComment(
                ManageUserInfo.getUser().getId(),commentVo.getId()));
        //二级评论和一级评论不需要查询回复用户
        if(comment.getRootId() == comment.getFatherId() ){
            return commentVo;
        }
        User toUser = userDao.getBaseMapper().selectById(comment.getToUserId());
        commentVo.setToUserUsername(toUser.getUsername());
        return commentVo;
    }

    public void setCommentVoChildren(List<CommentVo> commentVoList, QueryWrapper<Comment> queryWrapper, Long shareId){
        //查询一级评论下面的子评论
        for (CommentVo commentVo : commentVoList){
            queryWrapper.clear();
            queryWrapper.eq("share_id",shareId);
            queryWrapper.eq("root_id",commentVo.getId());
            List<Comment> subCommentList = commentDao.getBaseMapper().selectList(queryWrapper);
            //判断是否有子评论
            if(subCommentList.size() > 0){
                commentVo.setChildren(getCommentVoList(subCommentList));
            }
        }
    }
    @Override
    public BaseResponse<CommonBottomPageVo> listComment(CommentListReq commentListReq) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("share_id",commentListReq.getShareId());
        queryWrapper.eq("to_user_id", 0);
        long page = commentListReq.getPage();
        long size = commentListReq.getSize();
        //查询一级评论总数
        long firstCount = commentDao.getBaseMapper().selectCount(queryWrapper);
        queryWrapper.orderByDesc("agree").orderByDesc("id");     //按给定列排序
        List<Comment> commentList = commentDao.getBaseMapper().selectPage(new Page<>(page, size), queryWrapper).getRecords();
        CommonBottomPageVo commonBottomPageVo = new CommonBottomPageVo();
        //添加用户的信息
        List<CommentVo> commentVoList = getCommentVoList(commentList);
        setCommentVoChildren(commentVoList,queryWrapper, commentListReq.getShareId());
        //滚动条下拉判断是否还有评论列表，false表示没有了，true表示还有,根据一级评论判断
        boolean isHave = firstCount > page * size;
        commonBottomPageVo.setData(commentVoList);
        commonBottomPageVo.setHave(isHave);
        return ResultUtils.success(commonBottomPageVo);
    }

    //前端删除后后端将新的数据传给前端
    @Override
    public  BaseResponse<CommentVo>   getCommentsByRootId(Long rootId){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",rootId);
        Comment comment = commentDao.getBaseMapper().selectOne(queryWrapper);
        CommentVo commentVo = getCommentVo(comment);
        if(commentVo != null){
            queryWrapper.clear();
            queryWrapper.eq("root_id",rootId);

            List<Comment> allByRootIdCommentList = commentDao.getBaseMapper().selectList(queryWrapper);
            commentVo.setChildren(getCommentVoList(allByRootIdCommentList));
        }
        return ResultUtils.success(commentVo);
    }

    @Override
    public BaseResponse<CommonBottomPageVo<CommentVo>> messageListComment(MessageListCommentReq messageListCommentReq) {
        Long commentId = messageListCommentReq.getCommentId();
        Comment comment = commentDao.getBaseMapper().selectById(commentId);
        AssertUtil.isNotEmpty(comment,"评论内容不存在");
        //查询比这个评论id大的一级数据，在查多级
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("share_id",messageListCommentReq.getShareId());
        queryWrapper.eq("to_user_id", 0);
        //查询总数
        Integer selectCount = commentDao.getBaseMapper().selectCount(queryWrapper);
        Long rootId = comment.getRootId();
        queryWrapper.ge("id", rootId == 0 ? commentId : rootId);
        //查询一级评论总数
        long firstCount = commentDao.getBaseMapper().selectCount(queryWrapper);
        queryWrapper.orderByDesc("agree").orderByDesc("id");     //按给定列排序
        List<Comment> commentList = commentDao.getBaseMapper().selectList(queryWrapper);
        CommonBottomPageVo<CommentVo> commonBottomPageVo = new CommonBottomPageVo<>();
        //添加用户的信息
        List<CommentVo> commentVoList = getCommentVoList(commentList);
        setCommentVoChildren(commentVoList,queryWrapper, messageListCommentReq.getShareId());
        //滚动条下拉判断是否还有评论列表，false表示没有了，true表示还有,根据一级评论判断
        boolean isHave = firstCount < selectCount;
        commonBottomPageVo.setData(commentVoList);
        commonBottomPageVo.setHave(isHave);
        return ResultUtils.success(commonBottomPageVo);
    }
    @Override
    public BaseResponse<CommonBottomPageVo> messageLastListComment(MessageListCommentReq messageListCommentReq) {
        Long commentId = messageListCommentReq.getCommentId();
        Comment comment = commentDao.getBaseMapper().selectById(commentId);
        AssertUtil.isNotEmpty(comment,"评论内容不存在");
        //查询比这个评论id大的一级数据，在查多级
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        Long shareId = messageListCommentReq.getShareId();
        long page = messageListCommentReq.getPage();
        long size = messageListCommentReq.getSize();
        Long rootId = comment.getRootId();
        queryWrapper.eq("share_id", shareId);
        queryWrapper.eq("to_user_id", 0);
        queryWrapper.lt("id",  rootId == 0 ? commentId : rootId);
        //查询一级评论总数
        long firstCount = commentDao.getBaseMapper().selectCount(queryWrapper);
        queryWrapper.orderByDesc("agree").orderByDesc("id");     //按给定列排序
        List<Comment> commentList = commentDao.getBaseMapper().selectPage(new Page<>(page,size),
                queryWrapper).getRecords();
        CommonBottomPageVo<CommentVo> commonBottomPageVo = new CommonBottomPageVo<>();
        //添加用户的信息
        List<CommentVo> commentVoList = getCommentVoList(commentList);
        setCommentVoChildren(commentVoList,queryWrapper, shareId);
        //滚动条下拉判断是否还有评论列表，false表示没有了，true表示还有,根据一级评论判断
        boolean isHave = firstCount > page * size;
        commonBottomPageVo.setData(commentVoList);
        commonBottomPageVo.setHave(isHave);
        return ResultUtils.success(commonBottomPageVo);
    }
}
