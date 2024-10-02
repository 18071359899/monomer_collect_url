package com.collect.backend.utils.bean;
import cn.hutool.core.thread.ThreadException;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.collect.backend.common.Constants.MEILI_SEARCH_KEY;

@Configuration
@Slf4j
public class ClientBean {
    @Value("${lcwyyds.meilisearch.url}")
    private String url;
    @Bean("getClientBean")
    @Primary
    public Client getClient(){
        Client   client = new Client(new Config(
                    url,MEILI_SEARCH_KEY));
        return client;
    }
}
