package com.collect.backend.utils.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class GsonBean {
    @Bean("getGsonBean")
    @Primary
    public Gson getGson(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
//                .setLongSerializationPolicy(LongSerializationPolicy.STRING)  //将Long类型转成字符串
                .create();
        return gson;
    }
}
