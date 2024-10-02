package com.collect.backend.common.event;

import com.collect.backend.domain.entity.MessageNotify;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 消息通知事件，比如用户关注了、评论、点赞了某个人
 */
@Getter
public class MessageNotifyEvent extends ApplicationEvent {
    private MessageNotify messageNotify;

    public MessageNotifyEvent(Object source,MessageNotify messageNotify) {
        super(source);
        this.messageNotify = messageNotify;
    }
}
