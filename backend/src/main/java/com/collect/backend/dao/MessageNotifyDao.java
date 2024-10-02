package com.collect.backend.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.collect.backend.domain.entity.MessageNotify;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.vo.req.MessageNotifyListReq;
import com.collect.backend.mapper.mapper_join.MessageNotifyMapperJoin;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Service;

@Service
public class MessageNotifyDao extends MPJBaseServiceImpl<MessageNotifyMapperJoin, MessageNotify> {
    /**
     * 设置当前用户的所有消息通知为已读
     */
    public void setCurrUserIdAllReadByUserId(Long userId){
        lambdaUpdate().eq(MessageNotify::getAcceptUserId, userId)
                .set(MessageNotify::getIsRead,true)
                .update();
    }

    public void setReadById(Long messageNotifyId) {
        lambdaUpdate().eq(MessageNotify::getId, messageNotifyId)
                .set(MessageNotify::getIsRead,true)
                .update();
    }

    public Integer getMessageNotifyTotal(Long userId) {
        return Math.toIntExact(lambdaQuery().eq(MessageNotify::getAcceptUserId, userId)
                .eq(MessageNotify::getIsRead, false)
                .count());
    }

    public void deleteMessageNotifyByBussinessId(Long businessId) {
        QueryWrapper<MessageNotify> messageNotifyQueryWrapper = new QueryWrapper<>();
        messageNotifyQueryWrapper.eq("business_id",businessId);
        getBaseMapper().delete(messageNotifyQueryWrapper);
    }
}
