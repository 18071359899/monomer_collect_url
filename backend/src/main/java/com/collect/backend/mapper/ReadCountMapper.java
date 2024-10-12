package com.collect.backend.mapper;

import com.collect.backend.domain.entity.ReadCount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【read_count(计数表)】的数据库操作Mapper
* @createDate 2024-10-08 17:28:48
* @Entity com.collect.backend.domain.entity.ReadCount
*/
@Mapper
public interface ReadCountMapper extends BaseMapper<ReadCount> {

}




