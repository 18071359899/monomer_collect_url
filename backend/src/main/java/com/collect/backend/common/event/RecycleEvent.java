package com.collect.backend.common.event;

import com.collect.backend.domain.vo.resp.RecycleVo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RecycleEvent extends ApplicationEvent {
    private RecycleVo recycleVo;
    public RecycleEvent(Object source,RecycleVo recycleVo) {
        super(source);
        this.recycleVo = recycleVo;
    }
}
