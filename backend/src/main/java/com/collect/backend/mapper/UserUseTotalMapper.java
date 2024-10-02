package com.collect.backend.mapper;

import com.collect.backend.domain.entity.UserUseTotal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user_use_total(用户使用空间、总空间)】的数据库操作Mapper
* @createDate 2024-09-30 19:15:48
* @Entity com.collect.backend.domain.entity.UserUseTotal
*/
@Mapper
public interface UserUseTotalMapper extends BaseMapper<UserUseTotal> {

}




