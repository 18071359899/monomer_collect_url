package com.collect.backend.domain.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AllReadMessageNotifyReq {
    @NotNull(message = "用户参数不能为空")
    private Long userId;
}
