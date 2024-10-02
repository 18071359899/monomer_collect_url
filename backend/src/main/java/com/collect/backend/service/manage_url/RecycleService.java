package com.collect.backend.service.manage_url;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.domain.vo.resp.RecycleVo;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【recycle(回收站表)】的数据库操作Service
* @createDate 2024-02-16 22:24:52
*/

public interface RecycleService{

    Page<RecycleVo> listRecycle(long page, long size);

    String removeDataRecycle(List<RecycleVo> recycleVoList);

    String reductionDataRecycle(List<RecycleVo> recycleVoList);
}
