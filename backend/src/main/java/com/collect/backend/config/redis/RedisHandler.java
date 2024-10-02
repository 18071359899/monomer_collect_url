package com.collect.backend.config.redis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class RedisHandler implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化缓存
        //1.查询文章数据

        //2.入缓存
//        RedisUtils.
    }
}