package com.collect.backend.mapper;

import com.collect.backend.domain.entity.UserFollow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user_follow(用户关注的信息表)】的数据库操作Mapper
* @createDate 2024-04-10 23:37:46
* @Entity com.collect.backend.domain.entity.UserFollow
*/
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

}




