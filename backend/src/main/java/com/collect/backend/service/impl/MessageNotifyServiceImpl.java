package com.collect.backend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.MessageNotifyTypeEnum;
import com.collect.backend.dao.CommentDao;
import com.collect.backend.dao.MessageNotifyDao;
import com.collect.backend.dao.ShareDao;
import com.collect.backend.domain.entity.Comment;
import com.collect.backend.domain.entity.MessageNotify;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.vo.req.AllReadMessageNotifyReq;
import com.collect.backend.domain.vo.req.MessageNotifyListReq;
import com.collect.backend.domain.vo.req.common.DeleteDataByIds;
import com.collect.backend.domain.vo.resp.MessageNotifyVo;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.service.MessageNotifyService;
import com.collect.backend.mapper.MessageNotifyMapper;
import com.collect.backend.utils.ManageUserInfo;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
* @author Administrator
* @description 针对表【message_notify(用户消息通知表)】的数据库操作Service实现
* @createDate 2024-07-22 19:06:53
*/
@Service
public class MessageNotifyServiceImpl implements MessageNotifyService{
    @Autowired
    private MessageNotifyDao messageNotifyDao;
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private CommentDao commentDao;
    public MPJLambdaWrapper<MessageNotify> setSearchByAllFieldQuery(MessageNotifyListReq messageNotifyListReq){
        MPJLambdaWrapper<MessageNotify> wrapper = new MPJLambdaWrapper<MessageNotify>()
                .leftJoin(User.class,User::getId,MessageNotify::getSendUserId);
        wrapper.eq("accept_user_id",messageNotifyListReq.getUserId());
        wrapper.orderByDesc("t.id");
        return wrapper;
    }

    public void setBusinessData(List<MessageNotifyVo> messageNotifyVos){
        for (MessageNotifyVo messageNotifyVo : messageNotifyVos) {
            Integer type = messageNotifyVo.getType();
            if(type.equals(MessageNotifyTypeEnum.USER_AGREE_ARTICLE.getType()) ||
                    type.equals(MessageNotifyTypeEnum.USER_COLLECT_ARTICLE.getType()) ){
                Share share = Optional.ofNullable(
                        shareDao.getBaseMapper().selectById(messageNotifyVo.getBusinessId())).orElse(new Share());
                messageNotifyVo.setBusinessData(share.getTitle());
            }
            if(type.equals(MessageNotifyTypeEnum.USER_AGREE_COMMENT.getType()) ||
               type.equals(MessageNotifyTypeEnum.USER_REPLY_COMMENT.getType()) ||
               type.equals(MessageNotifyTypeEnum.USER_COMMENT_ARTICLE.getType())){
                Comment comment = Optional.ofNullable(
                        commentDao.getBaseMapper().selectById(messageNotifyVo.getBusinessId())).orElse(new Comment());
                Share share = Optional.ofNullable(shareDao.getBaseMapper().selectById(comment.getShareId()))
                        .orElse(new Share());
                messageNotifyVo.setShareId(share.getId());
                messageNotifyVo.setBusinessData(share.getTitle());
            }
        }
    }
    @Override
    public BaseResponse<Page<MessageNotifyVo>> messageNotifyList(MessageNotifyListReq messageNotifyListReq) {
        Page page = new Page<>(messageNotifyListReq.getPage(), messageNotifyListReq.getSize());
        page.setSearchCount(false);
        MPJLambdaWrapper<MessageNotify> wrapper = setSearchByAllFieldQuery(messageNotifyListReq);
        Long total = messageNotifyDao.getBaseMapper().selectJoinCount(wrapper);
        wrapper.selectAll(MessageNotify.class)
                .select("username as sendUserUsername","photo as sendPhoto");
        Page<MessageNotifyVo> messageNotifyVoPage = messageNotifyDao.getBaseMapper().
                selectJoinPage(page,MessageNotifyVo.class,wrapper);
        messageNotifyVoPage.setTotal(total);

        setBusinessData(messageNotifyVoPage.getRecords());
        return ResultUtils.success(messageNotifyVoPage);
    }

    @Override
    public BaseResponse<String> deleteMessageNotify(DeleteDataByIds deleteDataByIds) {
        List<Long> ids = deleteDataByIds.getIds();
        for (Long id : ids) {
            messageNotifyDao.getBaseMapper().deleteById(id);
        }
        return ResultUtils.success("ok");
    }

    @Override
    public BaseResponse<String> allReadMessageNotify(AllReadMessageNotifyReq allReadMessageNotifyReq) {
        messageNotifyDao.setCurrUserIdAllReadByUserId(allReadMessageNotifyReq.getUserId());
        return ResultUtils.success("ok");
    }

    @Override
    public BaseResponse<String> readMessageNotify(Long messageNotifyId) {
        messageNotifyDao.setReadById(messageNotifyId);
        return ResultUtils.success("ok");
    }

    @Override
    public BaseResponse<Integer> messageNotifyTotal() {
        return ResultUtils.success(messageNotifyDao.getMessageNotifyTotal(ManageUserInfo.getUser().getId()));
    }
}




