package com.collect.backend.common.event.listener;

import com.collect.backend.common.event.MessageNotifyEvent;
import com.collect.backend.dao.MessageNotifyDao;
import com.collect.backend.domain.entity.MessageNotify;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 监听消息通知相关事件，进行插入操作
 */
@Component
public class MessageNotifyListener {
    @Autowired
    private MessageNotifyDao messageNotifyDao;
    @EventListener(classes = MessageNotifyEvent.class)
    @Async("threadPoolTaskExecutor")
    public void insertMessageNotify(MessageNotifyEvent messageNotifyEvent){
        MessageNotify messageNotify = messageNotifyEvent.getMessageNotify();
        if(Objects.isNull(messageNotify.getAcceptUserId()) && Objects.isNull(messageNotify.getSendUserId())) return;
        if(!messageNotify.getAcceptUserId().equals(messageNotify.getSendUserId())) //收发信息的人不能是同一个
            messageNotifyDao.getBaseMapper().insert(messageNotify);
    }
    @EventListener(classes = MessageNotifyEvent.class)
    @Async("threadPoolTaskExecutor")
    public void deleteMessageNotify(MessageNotifyEvent messageNotifyEvent){
        MessageNotify messageNotify = messageNotifyEvent.getMessageNotify();
        messageNotifyDao.deleteMessageNotifyByBussinessId(messageNotify.getBusinessId());
    }

}
