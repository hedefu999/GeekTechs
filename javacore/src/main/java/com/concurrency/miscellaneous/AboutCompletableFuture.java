package com.concurrency.miscellaneous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 参考资料
 * https://www.baeldung.com/java-completablefuture
 * java5 引入了 Future接口
 * java 8 引入了 CompletableFuture接口
 */
public class AboutCompletableFuture {
    private static final Logger logger = LoggerFactory.getLogger(AboutCompletableFuture.class);
    static private void waitALittle(int timeout){
        try {
            TimeUnit.SECONDS.sleep(timeout);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    static Future<String> calcAsync() throws Exception{
        CompletableFuture<String> cf = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            //开始计算
            logger.info("开始计算");
            try {
                TimeUnit.SECONDS.sleep(4);
                logger.info("结束计算");
            } catch (InterruptedException e) {
                logger.info("{}", e.getMessage());
            }
            cf.complete("hello");
        });
        return cf;
    }

    /**
     * 使用Runable和Supplier函数式接口传入计算逻辑
     */
    static void functionalinterfaces() throws Exception{
        CompletableFuture<String> scf = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                logger.info("开始计算");
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                return "hello world";
            }
        });
        // Future的链式调用
        CompletableFuture<Integer> appliedCF = scf.thenApply(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.length();
            }
        });
        CompletableFuture<Void> acceptedCF = appliedCF.thenAccept(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                logger.info("consumer函数不会有返回值： {}", integer);
            }
        });
        CompletableFuture<Void> runCF = acceptedCF.thenRun(new Runnable() {
            @Override
            public void run() {
                logger.info("运行完毕后添加额外动作，打印一个结束标记：end");
            }
        });
        logger.info("开始获取");
        //Void unused = acceptedCF.get();
        //logger.info("得到最终结果 {}", unused);//得到最终结果 null
        runCF.get();
    }

    /**
     * CompletableFuture的链式API
     * 这在函数式接口中很常见，被称做 monadic design pattern
     */
    static void staticMethod4CompletableFuture() throws Exception{
        CompletableFuture.supplyAsync(() -> "hello ")
                .thenCompose(s -> CompletableFuture.supplyAsync(()-> s + "world"));

        //thenCombine 方法可以将几个独立的Futures的结果合并起来
        CompletableFuture<String> scf = CompletableFuture.supplyAsync(() -> "hello ").thenCombine(
                CompletableFuture.supplyAsync(() -> "world"),
                (s1, s2) -> s1 + s2);
        System.out.println(scf.get());

        //如果不需要返回两个Future的结果
        CompletableFuture<Void> vodCF = CompletableFuture.supplyAsync(() -> "hello")
                .thenAcceptBoth(CompletableFuture.supplyAsync(() -> "World"),
                        (s1, s2) -> System.out.println(s1 + " " + s2));
        Void unused = vodCF.get();

        //thenCompose 会接收前一个 CompletionStage 的结果，交给下一个 CompletionStage 返回结果
        //thenCompose 与 thenApply的区别相当于 java8 Stream API 的 flatMap与map
        CompletableFuture<Integer> iCF = scf.thenCompose(new Function<String, CompletionStage<Integer>>() {
            @Override
            public CompletionStage<Integer> apply(String s) {
                return CompletableFuture.supplyAsync(() -> s.length() - "java".length());
            }
        });
        System.out.println(iCF.get());

        //allOf方法 可以合并多个 CompletionStage
        CompletableFuture<String> future1
                = CompletableFuture.supplyAsync(() -> {waitALittle(2);return "Hello";});
        CompletableFuture<String> future2
                = CompletableFuture.supplyAsync(() -> {waitALittle(3);return "Beautiful";});
        CompletableFuture<String> future3
                = CompletableFuture.supplyAsync(() -> {waitALittle(4);return "World";});
        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(future1, future2, future3);
        logger.info("开始进入main线程等待");
        logger.info("任务执行状态：{} - {} - {}", future1.isDone(), future2.isDone(), future3.isDone());
        //下述 两行 方法调用都是阻塞的
        //logger.info("直接从allof方法拿结果：{}", combinedFuture.get());//null
        logger.info("任务执行结果：{}-{}-{}", future1.join(), future2.join(), future3.join());
        logger.info("任务执行状态：{} - {} - {}", future1.isDone(), future2.isDone(), future3.isDone());
    }

    static void errorHandling() throws Exception{
        CompletableFuture<String> handle = CompletableFuture.supplyAsync(() -> "hello").thenApply(str -> {
            return str.charAt(1) > 'h';
        }).handle(new BiFunction<Boolean, Throwable, String>() {
            //handle方法会传递两个参数：正常执行的结果 和 异常执行时的throwable
            //当正常执行时，throwable == null, 方法里的日志打印会发生空指针异常
            @Override
            public String apply(Boolean aBoolean, Throwable throwable) {
                logger.info("handle 入参：aBoolean = {}, e = {}", aBoolean, throwable.getMessage());
                return null;
            }
        });
        System.out.println(handle.get());

        //completeExceptionally方法
        CompletableFuture<String> cf = new CompletableFuture<>();
        cf.completeExceptionally(new RuntimeException("永远主动抛出异常"));
        cf.get(); //一旦执行一个get就会抛异常，似乎没啥用
    }

    public static void main(String[] args) throws Exception{
        //Future<String> future = calcAsync();
        //String result = future.get();
        //logger.info("result = {}", result);
        //staticMethod4CompletableFuture();
        //errorHandling();
    }
}
