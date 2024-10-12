package com.collect.backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableAsync   //异步方法 async 注解
@EnableScheduling  //定时任务注解
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
//        Share updateShare = new Share(
//                1L, "share.getTitle()", 10L,
//                "share.getContent()", null, null,
//                0,0,0,0
//        );
//        RedisUtils.zAdd("test",updateShare,System.currentTimeMillis());
//        Set<ZSetOperations.TypedTuple<String>> typedTuples =
//                RedisUtils.zReverseRangeByScoreWithScores("test", 0,
//                        System.currentTimeMillis(), 0, 10);
//        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
//            String value = typedTuple.getValue();
//            Gson gson = new Gson();
//            Share share = gson.fromJson(value, Share.class);
//            System.out.println(share);
//        }
//        CanalClient canalClient = SpringUtil.getBean(CanalClient.class);
//        canalClient.synchronousData();
    }
}
