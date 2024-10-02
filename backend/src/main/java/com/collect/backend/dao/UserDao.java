package com.collect.backend.dao;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.vo.cache.UserSpaceCache;
import com.collect.backend.mapper.ShareMapper;
import com.collect.backend.mapper.UserMapper;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserDao extends ServiceImpl<UserMapper, User> {
}
