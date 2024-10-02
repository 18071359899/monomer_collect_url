package com.collect.backend.common.event;

import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 监听用户点击删除后进入回收站
 */
@Getter
public class DeleteDataEvent extends ApplicationEvent {
    private List<CommonDirectory> directoryVos;
    public DeleteDataEvent(Object source, List<CommonDirectory> directoryVos) {
        super(source);
        this.directoryVos = directoryVos;
    }
}
