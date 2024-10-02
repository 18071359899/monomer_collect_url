package com.collect.backend.utils;

import com.collect.backend.utils.redis.RedisUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.collect.backend.common.Constants.ARTICLE_LIST_KEY;
import static com.collect.backend.common.Constants.FEED_KEY;

/**
 * 定时清理redis热点key
 */
@Component
public class ScheduleClearKey {
    @Async("threadPoolTaskExecutor")
    @Scheduled(cron ="0 0/10 * * * ? ")
    public void clearArticleListKey(){  //每十分钟清除文章游标Key，防止无用数据缓存到Redis中
        RedisUtils.del(ARTICLE_LIST_KEY);
    }

    @Async("threadPoolTaskExecutor")
    @Scheduled(cron ="0 0 0 * * ? ")
    public void clearFollowUserKey(){  //每晚清除用户的收件箱
        RedisUtils.del(FEED_KEY+"*");
    }
}
