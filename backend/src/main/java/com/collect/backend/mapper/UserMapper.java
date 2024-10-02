package com.collect.backend.mapper;

import com.collect.backend.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Lenovo
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-01-31 01:19:16
* @Entity com.collect.backend.domain.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




