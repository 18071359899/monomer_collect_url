package com.collect.backend.domain.vo.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.domain.vo.req.common.CommonPage;
import com.collect.backend.domain.vo.resp.common.CommonBottomPageVo;
import lombok.Data;

@Data
public class SearchReq extends CommonPage {
    /**
     * 查询类型
     */
    private String type;
    /**
     * 查询字符串
     */
    private String search;
}
