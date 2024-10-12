package com.collect.backend;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.collect.backend.config.minio.MinioConfig;
import com.collect.backend.dao.CategoryDao;
import com.collect.backend.dao.ShareDao;
import com.collect.backend.dao.UserDao;
import com.collect.backend.dao.WebsiteDao;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.service.manage_url.WebsiteService;
import com.collect.backend.utils.minio.MinioUtils;
import com.collect.backend.utils.redis.RedisUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.json.JacksonJsonHandler;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.TaskInfo;
import io.lettuce.core.ScriptOutputType;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.DeleteObject;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.collect.backend.common.Constants.*;
import static com.meilisearch.sdk.model.MatchingStrategy.ALL;

@SpringBootTest
@Slf4j
class BackendApplicationTests {
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private WebsiteDao websiteDao;
    @Autowired
    private Client client;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @SneakyThrows
    public void getLock(){
        RLock lock = redissonClient.getLock("test");
        lock.tryLock(100000,-1, TimeUnit.MILLISECONDS);
        System.out.println("执行业务代码");
    }
    @SneakyThrows
    @Test
    public void testlock(){
        for (int i = 0; i < 2; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getLock();
                }
            });
            thread.start();
        }

        Thread.sleep(100000);
    }


    @Autowired
    private MinioUtils minioUtils;
    @SneakyThrows
    @Test
    public void testMinio(){
        List<DeleteObject> deleteObjects = new ArrayList<>();
        deleteObjects.add(new DeleteObject("56a5bb3c6f2bd72e06709e3618e8155b"));
        minioUtils.removeFiles("slice-file",deleteObjects);
    }

    @Test
    public void testRedisHash(){
//        long size = 1024*1024*10;
//        Long curr = 0L;
//        Map<Object, Object> finalUserSpaceRedisVo = new HashMap<>();
//        finalUserSpaceRedisVo.put(UPLOAD_FILE_USE_KEY,curr.toString());
//        RedisUtils.hmset(getUserUseFileKey(1L), finalUserSpaceRedisVo);
//        for (int i = 0; i < 100; i++) {
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("当前时间  "+ LocalDateTime.now());
//                    RedisUtils.hincr(getUserUseFileKey(1L),UPLOAD_FILE_USE_KEY,size);
//                }
//            });
//            thread.start();
//        }
//        System.out.println("结果： "+
//                RedisUtils.hget(getUserUseFileKey(1L),UPLOAD_FILE_USE_KEY));
//        System.out.println("比对  " + size * 100);
        System.out.println(RedisUtils.hmget(getUserUseFileKey(1L)));
    }


    @Test
    public  void testMeiliSearch() throws IOException, InterruptedException {
            List<Category> categories = categoryDao.getBaseMapper().selectList(null);
            List<Website> websites = websiteDao.getBaseMapper().selectList(null);
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm")
                    .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                    .create();
            String Json = gson.toJson(categories);
            String websiteJson = gson.toJson(websites);
            System.out.println(websiteJson);
            System.out.println(Json);
            client.deleteIndex("category");
            client.deleteIndex("website");
            Index index = client.index("category");
            Index websitesIndex = client.index("website");
            index.addDocuments(Json,"id");
            websitesIndex.addDocuments(websiteJson,"id");
            index.updateFilterableAttributesSettings(new String[]{"isDelete","userId","name","id"});
            websitesIndex.updateFilterableAttributesSettings(new String[]{"isDelete","userId","title","id"});

    //        String[] strs = new String[]{"username"};
    //        SearchResult search = (SearchResult) index.search(new SearchRequest("of")
    //                .setAttributesToSearchOn(strs)
    //                .setQ("yyds"));
    //        System.out.println(JSONUtil.toJsonStr(search));
        //        SearchResult search = index.search("ly");

//        index.addDocuments(Json,"id");
//        System.out.println(JSONUtil.toJsonStr(taskInfos));
//        SearchResult results = index.search("大四");
//        System.out.println(JSONUtil.toJsonStr(results));
//        Thread.sleep(100000);
    }
    @Test
    public void testHttpRequest(){
        String url = "https://zxc";
            //链式构建请求
            String result = HttpRequest.get(url)
                    .timeout(1000)//超时，毫秒
                    .execute().body();
            if(result.contains("html"))
                System.out.println(result);
    }
    //通过url解析网址title和logo
    public static Map<String,String> getUrlTilte(String url){
        try {
            Document document = Jsoup.connect(url).get();
            Element element = document.select("link[rel*=shortcut icon]").first();
            String faviconUrl = element.attr("href");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    @Autowired
    private WebsiteService websiteService;

    public static void main(String[] args) {
        getUrlTilte("https://app5608.acapp.acwing.com.cn/pk/");
    }
    @Test
    public void testWebsite(){
        Website website = new Website();
        website.setUrl("https://www.bilibili.com/");
        System.out.println(websiteService.addWebsite(website).getMessage());
    }
    //todo 这里修改你的数据源
    public static void assembleDev(DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("323533plm");
        dataSourceConfig.setUrl("jdbc:mysql://127.0.0.1:3306/collect_url?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC");
    }
    @Test
    void testSendObject() {
        Map<String, Object> msg = new HashMap<>(2);
        msg.put("name", "jack");
        msg.put("age", 21);

//        // 1.创建CorrelationData
//        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
//        // 2.添加ConfirmCallback
//        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                System.out.println("消息回调失败"+ex);
//            }
//
//            @Override
//            public void onSuccess(CorrelationData.Confirm result) {
//                System.out.println("收到confirm callback回执");
//                if (result.isAck()) {
//                    // 消息发送成功
//                    System.out.println("消息发送成功，收到ACK");
//                } else {
//                    // 消息发送失败
//                    System.out.println("消息发送失败，收到NACK，原因：{}"+result.getReason());
//                }
//            }
//        });

        rabbitTemplate.convertAndSend("work.queue", msg);

        System.out.println("到达");
    }


    @Test
    void testBF() {
        ConcurrentHashMap<Long,Long> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(1L,1L);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        long l = concurrentHashMap.getOrDefault(1L, 0L) + 1L;
                        concurrentHashMap.put(1L, l);
                        System.out.println("put预期结果："+l); //750
                    }
                }
            });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Iterator<Map.Entry<Long, Long>> iterator = concurrentHashMap.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<Long, Long> item = iterator.next();
                        System.out.println("one 已经获取到数据  " + item.getValue());
                        iterator.remove(); //749
                        System.out.println("one 删除成功");
                    }
                }
            }
        });
        thread2.start();
        thread.start();
    }
}
