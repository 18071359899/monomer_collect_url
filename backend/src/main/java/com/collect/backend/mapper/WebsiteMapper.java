package com.collect.backend.mapper;

import com.collect.backend.domain.entity.Website;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Lenovo
* @description 针对表【website】的数据库操作Mapper
* @createDate 2024-02-06 21:10:39
* @Entity com.collect.backend.domain.entity.Website
*/
@Mapper
public interface WebsiteMapper extends BaseMapper<Website> {

}




