package com.collect.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.backend.domain.entity.UserShareBehavior;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user_share_behavior(用户对帖子的行为：点赞，收藏)】的数据库操作Mapper
* @createDate 2024-04-09 21:30:33
* @Entity generator.domain.UserShareBehavior
*/
@Mapper
public interface UserShareBehaviorMapper extends BaseMapper<UserShareBehavior> {
}




