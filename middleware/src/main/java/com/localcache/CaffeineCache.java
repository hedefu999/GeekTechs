package com.localcache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class CaffeineCache {
    static class AboutCaffeineCacheAPI{
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

    }

}
