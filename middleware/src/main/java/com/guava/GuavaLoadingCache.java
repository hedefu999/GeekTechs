package com.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * guava之
 使用Guava Cache可以保证只让一个线程去加载数据(比如从数据库中)，而其他线程则等待这个线程的返回结果。这样就能避免大量用户请求穿透缓存。

但是也有一个很致命的缺陷：如果缓存过期，恰好有多个线程读取同一个key的值，那么guava只允许一个线程去加载数据，其余线程阻塞。这虽然可以防止大量请求穿透缓存，但是效率低下。使用refreshAfterWrite可以做到：只阻塞加载数据的线程，其余线程返回旧数据。

expireAfterWrite是在指定项在一定时间内没有创建/覆盖时，会移除该key，下次取的时候从loading中取
expireAfterAccess是指定项在一定时间内没有读写，会移除该key，下次取的时候从loading中取
refreshAfterWrite是在指定时间内没有被创建/覆盖，则指定时间过后，再次访问时，会去刷新该缓存，在新值没有到来之前，始终返回旧值。缓存项只有在被检索时才会真正刷新
跟expire的区别是，指定时间过后，expire是remove该key，下次访问是同步去获取返回新值
而refresh则是指定时间后，不会remove该key，下次访问会触发刷新，新值没有回来时返回旧值

但是对于系统启动阶段，还是存在问题。系统启动后，由于缓存没有数据，导致一个线程去加载数据的时候，别的线程依然都阻塞了(因为没有旧值可以返回)。所以一般系统启动的时候，我们需要将数据预先加载到缓存，不然就会出现这种情况。

还有一个问题，真正加载数据的那个线程一定会阻塞，我们希望这个加载过程是异步的。这样就可以让所有线程立马返回旧值，在后台刷新缓存数据。refreshAfterWrite默认的刷新是同步的，会在调用者的线程中执行。我们可以改造成异步的，办法是实现CacheLoader.reload()即可。
 */
public class GuavaLoadingCache {

    static class Primary{
        public static void main(String[] args) throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(1);

            LoadingCache cache = CacheBuilder.newBuilder()
                    .maximumSize(4)
                    .refreshAfterWrite(1, TimeUnit.SECONDS)
                    .build(new CacheLoader<Integer, String>() {
                        public String load(Integer key) throws InterruptedException {
                            System.out.println(System.currentTimeMillis() + "--->" +
                                    Thread.currentThread().getName() + " load start for key: " + key);
                            System.out.println(System.currentTimeMillis() + "--->" +
                                    Thread.currentThread().getName() + " load end for key: " + key);
                            return "" + key;
                        }

                        @Override
                        public ListenableFuture<String> reload(Integer key, String oldValue) throws Exception {
                            ListenableFutureTask<String> task = ListenableFutureTask.create(() -> {
                                return load(key + 1);
                            });
                            executor.execute(task);
                            return task;
                        }
                    });

            for (int i = 1; i < 10; i ++ ){
                int finalI = i;
                new Thread(() -> {
                    try {
                        System.out.println(System.currentTimeMillis() + "--->" +
                                Thread.currentThread().getName()  + ": get value: " + cache.get(1));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            Thread.sleep(4000);
            System.out.println("-------------------------------------------------");
            for (int i = 1; i < 10; i ++ ){
                int finalI = i;
                new Thread(() -> {
                    try {
                        System.out.println(System.currentTimeMillis() + "--->" +
                                Thread.currentThread().getName()  + ":again get value: " + cache.get(1));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    public void test90() {
        String dict = "helloworldiamandroid";
        LoadingCache<Integer,String> cache =
                CacheBuilder.newBuilder().initialCapacity(16).build(new CacheLoader<Integer, String>() {
                    @Override
                    public String load(Integer integer) throws Exception {
                        return integer+"==>"+dict.charAt(new Random().nextInt(dict.length()));
                    }
                });
        try {
            String s = cache.get(80);
            System.out.println(s);
        } catch (ExecutionException e) {
            e.printStackTrace();
            cache.refresh(2);
            System.out.println(e.getMessage());
        }
    }
}
