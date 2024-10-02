package com.collect.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.collect.backend.common.exception.BusinessException;
import com.collect.backend.domain.entity.User;
import com.collect.backend.mapper.UserMapper;
import com.collect.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username",username);
        User user = userMapper.selectOne(query);

        if(user == null){
            throw  new BusinessException("用户不存在");
        }
        return new UserDetailsImpl(user);
    }
}
