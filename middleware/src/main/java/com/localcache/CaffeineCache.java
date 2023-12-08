package com.localcache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Weigher;
import lombok.Data;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class CaffeineCache {
private static final Logger logger = LoggerFactory.getLogger(CaffeineCache.class);
static class JuniorStage{
static void base(){
    LoadingCache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(8L)
            .initialCapacity(1)
            .expireAfterAccess(Duration.ofDays(2))
            .expireAfterWrite(Duration.ofHours(2))
            .refreshAfterWrite(Duration.ofHours(1))
            .recordStats()
            .build(new CacheLoader<String, String>() {
                @Override
                public @Nullable String load(String key) throws Exception {
                    //从存储介质中查询这个缓存
                    return key + "'s value = " + new Random().nextInt(10);
                }
            });
    String aValue = cache.getIfPresent("AAA");
    cache.put("bbb","bValue");
    //类似computeIfAbsent的用法
    String aaa = cache.get("aaa", new Function<String, String>() {
        @Override
        public String apply(String s) {
            return null;
        }
    });//不存在 -> 执行function生成一个 -> 返回并插入缓存，当多线程同时get不存在的key的，只会有一个线程执行function，相比getIfAbsent更推荐使用
    Map<String, String> allCache = cache.getAll(Arrays.asList("keyA", "keyB"));
    //删除缓存key
    cache.invalidate("key");

    //异步加载缓存 这种方式会返回一个 CompletableFuture
    AsyncLoadingCache<String, Object> asyncLoadingCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .buildAsync(new CacheLoader<String, Object>() {
                @Override
                public @Nullable Object load(String key) throws Exception {
                    return null;
                }
            });
    CompletableFuture<Void> completableFuture = asyncLoadingCache.get("aaa").thenAccept(new Consumer<Object>() {
        @Override
        public void accept(Object o) {
            //异步加载的数据已到达
        }
    });
}

/**
 * Caffeine缓存的三种填充策略（cache population strategies）：手动、同步加载、异步加载
 */
@Data
static class DataObject{
    private final String data;
    private static int objectCounter = 0;
    public static DataObject get(String data){
        objectCounter++;
        return new DataObject("Data for " + data);
    }
}
static void threeStrategiesCaffeineCachePop(){
    /** manual populating */
    Cache<String, DataObject> cache =
            Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(100).build();
    cache.put("A",new DataObject("dt"));
    DataObject dataObject = cache.getIfPresent("A");
    //类型 java HashMap#compute 方法 不存在就执行初始化方法生成一个，再放入缓存并返回
    DataObject defaultVal = cache.get("A", key -> DataObject.get("dt"));
    //缓存手动失效
    cache.invalidate("A");
    dataObject = cache.getIfPresent("A"); //null

    /** 同步加载 synchronous loading */
    LoadingCache<String, DataObject> loadingCache = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(Duration.ofMinutes(1))
            .build(new CacheLoader<String, DataObject>() {
                @Override
                public @Nullable DataObject load(@NonNull String key) throws Exception {
                    return DataObject.get(key + "-system");
                }
            });
    DataObject systemGenA = loadingCache.get("A");
    //一次获取多个缓存
    Map<@NonNull String, @NonNull DataObject> dataObjectMap = loadingCache.getAll(Arrays.asList("A", "B", "C"));

    /** 异步加载 asynchronous loading */
    AsyncLoadingCache<String, DataObject> asyncLoadingCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(1))
            .buildAsync(new CacheLoader<String, DataObject>() {
                @Override
                public @Nullable DataObject load(@NonNull String s) throws Exception {
                    return DataObject.get("async " + s);
                }
            });
    CompletableFuture<DataObject> aCache = asyncLoadingCache.get("A");
    aCache.thenAccept(dataObj -> {
        System.out.println("异步获得缓存 "+dataObj.data);
    });
    asyncLoadingCache.getAll(Arrays.asList("A","B")).thenAccept(dataObjMap -> {
        System.out.println("一次获取多个缓存值：map = "+ dataObjMap);
    });
}
/**
 * Caffeine缓存的三种缓存清除（value eviction）策略：基于空间、时间、引用
 */
@Test
public void cacheEvictionStrategies(){
    //Size-Based Eviction 数量策略
    LoadingCache<String, DataObject> cache = Caffeine.newBuilder().maximumSize(1)
            .build(key -> DataObject.get(key));
    cache.get("A");
    System.out.println(cache.estimatedSize());//1

    cache.get("B");
    cache.cleanUp();//没这个方法下一行将返回 2，这是因为缓存清除的动作是异步的，cleanUp方法可以确保同步等待缓存清除工作的执行完成
    System.out.println(cache.estimatedSize());

    //Weight-Based Eviction 权重策略
    LoadingCache<String, DataObject> weightCache = Caffeine.newBuilder()
            .maximumWeight(10).weigher(new Weigher<String, DataObject>() {
                @Override
                public @NonNegative int weigh(@NonNull String key, @NonNull DataObject value) {
                    logger.info("对一个缓存称重：key = {}, value = {}", key, value);
                    return 5;//这里可以自定义一个缓存的权重
                }
            }).build(DataObject::get);
    System.out.println(weightCache.estimatedSize());
    weightCache.get("A");
    System.out.println(weightCache.estimatedSize());
    weightCache.get("B");
    System.out.println(weightCache.estimatedSize());
    weightCache.get("C");
    weightCache.cleanUp();
    System.out.println(weightCache.estimatedSize());

/**
 Time-Based Eviction 时间策略，存在下述几种
 expire-after-access 访问后过期，最后一次读或写之后经历的时长超出阈值
 expire-after-write 写后过期，最后一次写。。。
 自定义，自定义过期判断规则
 */
    LoadingCache<String,DataObject> timeCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .weakKeys().weakValues() //利于对象垃圾回收
            .softValues() //利于 JVM 全局的最近最少使用回收策略
            .build(DataObject::get);
    //自定义缓存清除策略
    timeCache = Caffeine.newBuilder().expireAfter(new Expiry<String, DataObject>() {
        @Override
        public long expireAfterCreate(@NonNull String key, @NonNull DataObject value, long currentTime) {
            return value.getData().length() * 1000;
        }
        @Override
        public long expireAfterUpdate(@NonNull String key, @NonNull DataObject value, long currentTime, @NonNegative long currentDuration) {
            return currentDuration;
        }
        @Override
        public long expireAfterRead(@NonNull String key, @NonNull DataObject value, long currentTime, @NonNegative long currentDuration) {
            return currentDuration;
        }
    }).build(DataObject::get);

    //Caffeine缓存内部的统计数据
    timeCache.stats().hitCount();
    timeCache.stats().missCount();
}
/**
 缓存刷新：Caffeine.newBuilder().refreshAfterWrite(Duration.ofMinutes(5))
 注意区分 expireAfter 和 refreshAfter
 expireAfter 已过期 key 被请求时，阻塞等待获取新值才返回
 refreshAfter 已到刷新时间的 key 被请求时，如果新值没有被异步加载进来，将返回旧值
 */


}
}
