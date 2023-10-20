package com.guava;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GuavaCache {
    static final Random random = new Random();
    //使用 CacheLoader Callable 两种方式创建缓存
    static class TwoWays2CreateCache{
        static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                //缓存最大容量，超出会进行回收
                .maximumSize(100)
                //缓存过期超时时间
                .expireAfterWrite(150, TimeUnit.SECONDS)
                //缓存刷新时间间隔
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                //key使用弱引用 WeakReference
                .weakKeys()
                //Entry被移除时的监听器
                .removalListener(notfication -> log.info("移除通知：{}", JSON.toJSONString(notfication)))
                //创建一个CacheLoader，必须重写load方法
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return "value_" + key;
                    }
                    //异步刷新缓存
                    @Override
                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                        return super.reload(key, oldValue);
                    }
                });

        public static void main(String[] args) throws Exception {
            String key = "aaa";
            //System.out.println(loadingCache.get(key));
            //传递的callable相当于对 CacheLoader的扩展
            String value = loadingCache.get(key, new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return "call_" + key;
                }
            });
            System.out.println(value);
            loadingCache.put("key", value);
            //手动使缓存失效
            loadingCache.invalidate("key");
            loadingCache.invalidateAll();
        }
    }

/**
 * 与失效/缓存刷新相关的配置有：expireAfterWrite、expireAfterAccess、refreshAfterWrite、CacheLoader#reload
 * 演示这些API的效果
 */
static class CacheInvalidationTimeBased{
    static CountDownLatch latch = new CountDownLatch(1);

    /**
     * 使用10个线程并发访问缓存中的同一个key
     * 首次访问时由于缓存从未加载过，所以一定会由一个用户线程加载缓存，其他线程阻塞等待
     * 当缓存expire之后再并发访问缓存，仍然会像首次访问缓存时阻塞其他线程（此时会有缓存移除通知，并执行reload方法）
     * 当缓存未expire，但refresh的间隔已经到达时，此时再并发访问缓存，只有1个用户线程会同步执行load方法，其他用户线程直接返回旧值
     *
     * 在不过期的情况下且过了 refresh 时间才会去reload（异步加载，同时返回旧值），所以推荐的设置是 refresh < expire
     */
    static void aboutExpireAfterWrite() throws Exception{
        LoadingCache<String, String> stringLoadingCache = CacheBuilder.newBuilder()
                //缓存最大容量，超出会进行回收
                .maximumSize(10)
                //缓存过期超时时间
                .expireAfterWrite(2, TimeUnit.SECONDS)
                //只有上面的expireAfterWrite会导致其他线程全被阻塞，加这个
                .refreshAfterWrite(Duration.ofSeconds(6))
                //Entry被移除时的监听器
                .removalListener(notfication -> log.info("移除缓存：{}", JSON.toJSONString(notfication)))
                //创建一个CacheLoader，必须重写load方法
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        log.info("将会查询数据库（模拟）");
                        TimeUnit.SECONDS.sleep(2);
                        String newVal = random.nextInt(10) + "_value_for_" + key;
                        log.info("获取到新缓存值 {}，将会在下一波查询获取", newVal);
                        return newVal;
                    }

                    @Override
                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                        log.info("执行reload方法");
                        return super.reload(key, oldValue);
                    }
                });
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("开始查询缓存");
                    String helloValue = stringLoadingCache.get("hello");
                    log.info("获取到缓存：{}", helloValue);
                } catch (Exception e) {
                    System.out.println("本地缓存查起来也会有异常？！");
                }
            }
        };
        //首次加载缓存，不论 expireAfterWrite 还是 refreshAfterWrite 都会阻塞其他线程
        for (int i = 0; i < 5; i++) {
            new Thread(task, "userThread" + i).start();
        }
        //等待10s后缓存过期了再来查询，就不是首次加载缓存
        TimeUnit.SECONDS.sleep(14);
        log.info("-=-=-=-=-=-=-=-=-=-=- 第二波查询 -=-=-=-=-=-=-=-=-=-");
        for (int i = 0; i < 5; i++) {
            new Thread(task, "userThread" + i).start();
        }
        //latch.await();
    }

    /**
     * 引入独立线程池，专门在发生访问时刷新缓存，所有用户线程都不会被阻塞，代价就是会获得旧的缓存值
     * 设置了刷新间隔并不意味着guava会定时刷新本地缓存，而是惰性刷新，当有用户线程访问缓存时，guava发现key需要refresh，就会异步加载缓存，同时返回旧值
     */
    static final ExecutorService executor = Executors.newFixedThreadPool(1);
    static void asyncLoadCache() throws Exception{
        LoadingCache<String, String> stringLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(40, TimeUnit.SECONDS)
                .refreshAfterWrite(Duration.ofSeconds(4))
                .removalListener(notfication -> log.info("移除缓存：{}", JSON.toJSONString(notfication)))
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        log.info("将会查询数据库（模拟）");
                        TimeUnit.SECONDS.sleep(2);
                        String newVal = random.nextInt(10) + "_value_for_" + key;
                        log.info("获取到新缓存值 {}，将会在下一波查询获取", newVal);
                        return newVal;
                    }
                    //覆写 reload 方法实现异步加载，真正让所有用户线程不被阻塞（不论是同一个key 还是恰巧同一时间失效的不同key）
                    @Override
                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                        log.info("执行reload方法");
                        ListenableFutureTask<String> task = ListenableFutureTask.create(() -> {
                            return load(key);
                        });
                        executor.execute(task);
                        return task;
                    }
                });
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("开始查询缓存");
                    String helloValue = stringLoadingCache.get("hello");
                    log.info("获取到缓存：{}", helloValue);
                } catch (Exception e) {
                    System.out.println("本地缓存查起来也会有异常？！");
                }
            }
        };
        for (int i = 0; i < 5; i++) {
            new Thread(task, "userThread" + i).start();
        }
        //stringLoadingCache.refresh("hello");
        TimeUnit.SECONDS.sleep(8);
        log.info("-=-=-=-=-=-=-=-=-=-=- 第二波查询 -=-=-=-=-=-=-=-=-=-");
        for (int i = 0; i < 5; i++) {
            new Thread(task, "userThread" + i).start();
        }
        TimeUnit.SECONDS.sleep(8);
        log.info("-=-=-=-=-=-=-=-=-=-=- 第三波查询 -=-=-=-=-=-=-=-=-=-");
        for (int i = 0; i < 5; i++) {
            new Thread(task, "userThread" + i).start();
        }
    }

    public static void main(String[] args) throws Exception{
        aboutExpireAfterWrite();
        //asyncLoadCache();
    }
}

}
