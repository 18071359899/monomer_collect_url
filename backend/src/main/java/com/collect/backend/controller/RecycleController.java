package com.collect.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.domain.vo.req.common.CommonPage;
import com.collect.backend.domain.vo.resp.RecycleVo;
import com.collect.backend.service.manage_url.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 *  回收站controller
 */
@RestController
@RequestMapping("/recycle")
public class RecycleController {

    @Autowired
    private RecycleService recycleService;

    @GetMapping("/list/")
    public BaseResponse<Page<RecycleVo>> listRecycle(@Valid CommonPage commonPageObject){
        long page = commonPageObject.getPage();
        long size = commonPageObject.getSize();
        return ResultUtils.success(recycleService.listRecycle(page,size));
    }

    @PostMapping("/remove/")
    public BaseResponse<String> removeDataRecycle(@RequestBody List<RecycleVo> recycleVoList){
        return ResultUtils.success(recycleService.removeDataRecycle(recycleVoList));
    }

    @PostMapping("/reduction/")
    public BaseResponse<String> reductionDataRecycle(@RequestBody List<RecycleVo> recycleVoList){
        return ResultUtils.success(recycleService.reductionDataRecycle(recycleVoList));
    }
}
