package com.collect.backend.mapper;

import com.collect.backend.domain.entity.Recycle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Lenovo
* @description 针对表【recycle(回收站表)】的数据库操作Mapper
* @createDate 2024-02-16 22:24:52
* @Entity com.collect.backend.domain.entity.Recycle
*/
@Mapper
public interface RecycleMapper extends BaseMapper<Recycle> {

}




