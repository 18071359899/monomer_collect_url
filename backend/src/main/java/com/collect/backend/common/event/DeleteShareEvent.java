package com.collect.backend.common.event;

import com.collect.backend.domain.entity.Share;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 删除分享数据后清楚与之关联的数据
 */
@Getter
public class DeleteShareEvent extends ApplicationEvent {
    private Share share;

    public DeleteShareEvent(Object source, Share share) {
        super(source);
        this.share = share;
    }
}
