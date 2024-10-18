package com.collect.backend.common;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * 通用常量信息
 * 
 * @author neusoft
 */
public class Constants{
    public static final String IMAGES_URL_PREFIX = "http://localhost:9000/api/images/get/";
    /**
     *验证码 redis key
     */
    public static final String codeKey = "code:";
    /**
     * 标识shareId  和 userId
     */
    public static final String shareReadingKey = "shareReading:";
    /**
     *  阅读量有效期
     */
    public static final Integer READING_EXPIRIION = 1;
    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 5;
    /**
     * 用户点赞、收藏、取消点赞、取消收藏所增加或减少总量的值
     */
    public static final Integer USER_BEHAVIOR_TOTAL_VALUE = 1;
    /**
     *  meilisearch key
     */
    public static final String MEILI_SEARCH_KEY = "lcwyyds";
    /**
     * 拉模式收件箱 sortset 前缀
     */
    public static final String FEED_KEY = "feed:";
    /**
     * 文章详情key
     */
    public static final String ARTICLE_DETAILS_KEY = "articleDetails:";
    /**
     * 文章详情分布式锁key
     */
    public static final String ARTICLE_DETAILS_LOCK_KEY = "articleDetails:lock:";
    /**
     * 文章列表分布式锁key
     */
    public static final String ARTICLE_LIST_LOCK_KEY = "articleList:lock:";
    /**
     * 文章列表key
     */
    public static final String ARTICLE_LIST_KEY = "articleList:";
    /**
     * 文章详情布隆过滤器key
     */
    public static final String ARTICLE_DETAILS_BL_KEY = "articleBloomFilter";
    /**
     * 下载code key
     */
    public static  final  String DOWNLOAD_KEY = "download:";
    /**
     * 缓存网盘空间、总空间对象 key
     */
    public static  final  String UPLOAD_FILE_COMPUTED_KEY = "userUseFileComputed:";
    /**
     * hash数据结构 用户网盘使用空间 key
     */
    public static  final  String UPLOAD_FILE_USE_KEY = "userUse";
    /**
     * hash数据结构 用户网盘总空间 key
     */
    public static  final  String UPLOAD_FILE_TOTAL_KEY = "userTotal";

    /**
     * 文章相关计数hash结构 key
     * @param userId
     * @return
     */
    public static  final  String ARTICLE_COUNT_HASH_KEY = "articleCount:";
    /**
     * 文章相关计数hash结构lock锁 key
     */
    public static  final  String ARTICLE_COUNT_HASH_LOCK_KEY = "articleLockCount:";
    /**
     * 阅读量
     */
    public static  final  String ARTICLE_COUNT_HASH_READING_CNT = "readingCnt:";
    /**
     * 点赞数
     */
    public static  final  String ARTICLE_COUNT_HASH_LIKE_CNT = "likeCnt:";
    /**
     * 评论数
     */
    public static  final  String ARTICLE_COUNT_HASH_COMMENT_CNT = "commentCnt:";
    /**
     * 收藏数
     */
    public static  final  String ARTICLE_COUNT_HASH_COLLECT_CNT = "collectCnt:";
    public static String getArticleCountHashLockKey(Long shareId){
        return ARTICLE_COUNT_HASH_LOCK_KEY + shareId;
    }
    public static String getArticleCountHashKey(Long shareId){
        return ARTICLE_COUNT_HASH_KEY + shareId;
    }

    public static String getUserUseFileKey(Long userId){
        return UPLOAD_FILE_COMPUTED_KEY + userId;
    }

    public static String getDownloadKey(String code) {
        return Constants.DOWNLOAD_KEY + code;
    }
}