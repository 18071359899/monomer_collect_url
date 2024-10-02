package com.collect.backend.mapper.mapper_join;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.backend.domain.entity.MessageNotify;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【message_notify(用户消息通知表)】的数据库操作Mapper
* @createDate 2024-07-22 19:06:53
* @Entity com.collect.backend.domain.entity.MessageNotify
*/
@Mapper
public interface MessageNotifyMapperJoin extends MPJBaseMapper<MessageNotify> {

}




