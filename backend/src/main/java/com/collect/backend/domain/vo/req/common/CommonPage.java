package com.collect.backend.domain.vo.req.common;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *   分页的依据
 */
@Data
public class CommonPage {
    @NotNull(message = "页码不能为空")
    //当前第几页
    private long   page;
    @NotNull(message = "页数不能为空")
    //一页多少数据
    private long   size;
}
