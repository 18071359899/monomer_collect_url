package com.collect.backend.domain.vo.req;

import com.collect.backend.domain.vo.req.common.CommonPage;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessageNotifyListReq extends CommonPage{
    @NotNull(message = "用户参数不能为空")
    private Long userId;
}