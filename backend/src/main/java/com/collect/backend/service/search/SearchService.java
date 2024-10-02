package com.collect.backend.service.search;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.vo.req.SearchReq;

public interface SearchService {
    BaseResponse<Page<?>> search(SearchReq searchReq);
}
