package com.collect.backend.mapper;

import com.collect.backend.domain.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Lenovo
* @description 针对表【category(给网站做分类的表)】的数据库操作Mapper
* @createDate 2024-02-01 22:50:29
* @Entity com.collect.backend.domain.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




