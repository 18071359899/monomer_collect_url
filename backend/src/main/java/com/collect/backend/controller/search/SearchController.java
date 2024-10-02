package com.collect.backend.controller.search;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.vo.req.SearchReq;
import com.collect.backend.domain.vo.req.ShareUserBehaviorReq;
import com.collect.backend.domain.vo.req.common.CommonPage;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.domain.vo.resp.common.CommonBottomPageVo;
import com.collect.backend.service.search.SearchService;
import com.collect.backend.service.share.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 进行聚合搜索
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;
    /**
     *
     * @param
     * @return
     */
    @GetMapping("/get/")
    public BaseResponse<Page<?>> search(SearchReq searchReq){
        return searchService.search(searchReq);
    }


}
