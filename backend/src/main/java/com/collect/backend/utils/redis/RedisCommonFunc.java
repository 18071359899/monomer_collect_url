package com.collect.backend.utils.redis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.collect.backend.common.exception.BusinessException;
import com.collect.backend.domain.vo.req.common.CursorPageBaseReq;
import com.collect.backend.domain.vo.resp.common.CursorPageBaseResp;
import com.collect.backend.utils.cursor.CursorUtils;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.function.Supplier;

import static com.collect.backend.common.Constants.ARTICLE_DETAILS_BL_KEY;

/**
 * 缓存常用方法
 */
@Slf4j
public class RedisCommonFunc {
    /**
     * 添加游标列表缓存通用封装方法，解决、缓存击穿 @param redisKey
     * @param redissonClient
     * @param getRedisDataSupplier
     * @param redisLockKey
     * @param supplier
     * @return
     * @param <T>
     */
    public static  <T> T addCacheCommonFun(RedissonClient redissonClient,
                                             Supplier<T> getRedisDataSupplier,
                                           String redisLockKey, Supplier<T> supplier){
        T redisData = getRedisDataSupplier.get();
        if(Objects.nonNull(redisData)){  //走redis
            return redisData;
        }
        RLock lock = redissonClient.getLock(redisLockKey);
        try {
            lock.lock();
        } catch (Throwable e) {
            log.error("获取分布式锁出问题",e);
            throw new BusinessException("获取数据失败");
        }
        try {
            redisData = getRedisDataSupplier.get();
            if(Objects.nonNull(redisData)){  //走redis
                return redisData;
            }
            return supplier.get();
        }finally {
            // 只能释放自己的锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 通过redis获取游标数据封装
     * @param cursorPageBaseReq
     * @param redisKey
     * @param clazz
     * @param cursorColumn
     * @return
     * @param <T>
     */
    public static <T> CursorPageBaseResp<T> getCursorDataByRedis(CursorPageBaseReq cursorPageBaseReq,
                                             String redisKey, Class<T> clazz, SFunction<T, ?> cursorColumn){
        String cursor = cursorPageBaseReq.getCursor();
        Integer pageSize = cursorPageBaseReq.getPageSize();
        long max = Long.MAX_VALUE;
        long offset = 0;
        if(StringUtils.isNotBlank(cursor)){
            max = Long.parseLong(cursor);offset = 1;
        }
        Set<ZSetOperations.TypedTuple<String>> typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, 0, max, offset, pageSize);
        List<T> arrayList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            arrayList.add(JsonUtils.toObj(typedTuple.getValue(),clazz));
        }
        if(CollectionUtil.isEmpty(arrayList)){
            return null;
        }
        String newCursor = Optional.ofNullable(CollectionUtil.getLast(arrayList))
                .map(cursorColumn)
                .map(CursorUtils::toCursor)
                .orElse(null);
        return CursorPageBaseResp.init(newCursor,arrayList);
    }

    /**
     * 添加单个缓存通用封装方法，解决 缓存穿透、缓存击穿* @param redisKey
     * 使用空对象
     * @param redissonClient
     * @param redisKey
     * @param redisLockKey
     * @param clazz
     * @param supplier
     * @return
     * @param <T>
     */
    public static  <T> T addCacheCommonFun(RedissonClient redissonClient,
                                           String redisKey,
                                           String redisLockKey,
                                           Class<T> clazz, Supplier<T> supplier){
        String redisData = RedisUtils.get(redisKey);
        if(StrUtil.isNotBlank(redisData)){  //走redis
            return JsonUtils.toObj(redisData,clazz);
        }
        //缓存空对象解决缓存穿透
        AssertUtil.isEmpty(redisData,"页面不存在");
        RLock lock = redissonClient.getLock(redisLockKey);
        try {
            lock.lock();
        } catch (Throwable e) {
            log.error("获取分布式锁出问题",e);
            throw new BusinessException("获取数据失败");
        }
        try {
            redisData = RedisUtils.get(redisKey);
            if(StrUtil.isNotBlank(redisData)){  //走redis
                return JsonUtils.toObj(redisData,clazz);
            }
            return supplier.get();
        }finally {
            // 只能释放自己的锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 添加缓存通用封装方法，解决 缓存穿透、缓存击穿* @param redisKey
     * 使用布隆过滤器
     * @param redissonClient
     * @param redisKey
     * @param redisLockKey
     * @param clazz
     * @param supplier
     * @param businessId
     * @return
     * @param <T>
     */
    public static <T> T addCacheCommonFunForBloom(RedissonClient redissonClient,
                                                  String redisKey,
                                                  String redisLockKey,
                                                  Class<T> clazz, Supplier<T> supplier,
                                                  Long businessId){  //布隆过滤器方案
        String redisData = RedisUtils.get(redisKey);
        if(StrUtil.isNotBlank(redisData)){  //走redis
            return JsonUtils.toObj(redisData,clazz);
        }
        //布隆过滤器解决缓存穿透
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(ARTICLE_DETAILS_BL_KEY);
        AssertUtil.isTrue(bloomFilter.contains(businessId),"页面不存在");
        RLock lock = redissonClient.getLock(redisLockKey);
        try {
            lock.lock();
        } catch (Throwable e) {
            log.error("获取分布式锁出问题",e);
            throw new BusinessException("获取数据失败");
        }
        try {
            redisData = RedisUtils.get(redisKey);
            if(StrUtil.isNotBlank(redisData)){  //走redis
                return JsonUtils.toObj(redisData,clazz);
            }
            return supplier.get();
        }finally {
            // 只能释放自己的锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
