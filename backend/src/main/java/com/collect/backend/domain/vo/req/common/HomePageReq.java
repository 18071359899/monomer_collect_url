package com.collect.backend.domain.vo.req.common;

import lombok.Data;

/**
 * 个人主页通用查询条件
 */
@Data
public class HomePageReq extends CommonPage{
    private Long userId;
}
