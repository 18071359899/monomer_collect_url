package com.collect.backend.domain.vo.req.common;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeleteDataByIds {
    @NotNull(message = "删除列表不能为空")
    private List<Long> ids;
}
