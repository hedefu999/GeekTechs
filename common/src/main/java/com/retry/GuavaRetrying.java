package com.retry;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.ConnectException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Guava-Retrying 重试框架
 * 相比spring-retrying的一个优势是：可重试逻辑如果设定返回true才是完整走完所有逻辑，
 * 当其内部发生了未知异常，或捕获了异常返回false，都可以进入重试，直到最终成功返回true，或者重试次数耗尽
 *
 * spring-retry 和 guava-retry 工具都是线程安全的重试，能够支持并发业务场景的重试逻辑正确性。
 * 两者都很好的将正常方法和重试方法进行了解耦，可以设置超时时间、重试次数、间隔时间、监听结果、都是不错的框架。
 * 但是明显感觉得到，guava-retry在使用上更便捷，更灵活，能根据方法返回值来判断是否重试，而Spring-retry只能根据抛出的异常来进行重试。
 */
@Slf4j
public class GuavaRetrying {

    static boolean doWork(AtomicInteger info) throws Exception{
        log.info("开始执行需要重试的任务， param = {}", info.get());
        int flag = info.getAndIncrement();
        if (flag == 0){
            return false;
        }else if (flag == 1){
            throw new ConnectException("连接异常，受检异常");
        }else if (flag==2){
            throw new NullPointerException("空指针");
        }else if (flag==3){
            int i=1/0;
        }
        return true;
    }

    static void primary() throws Exception {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfException() //不用声明具体的Exception类型，但是抛出ERROR也不会重试
                //.retryIfRuntimeException() 抛出受检异常和ERROR时也不会重试
                //.retryIfExceptionOfType(ArithmeticException.class) 这样才可以对抛出的ERROR进行重试
                .retryIfResult(new Predicate<Boolean>() {
                    @Override
                    public boolean apply(@Nullable Boolean success) {
                        return !success;
                    }
                }) //指定在callable何种返回值时进行重试
                //.retryIfResult(Predicates.equalTo(false)) 上述写法可以简写
                //.retryIfResult(Predicates.containsPattern("_error$")) 以 _error 结尾的才重试
                //.retryIfResult(Objects::isNull)
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS)) //设置等待间隔时间
                .withStopStrategy(StopStrategies.stopAfterAttempt(5)) //设置最大重试次数
                .<Void>withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("第 {} 次执行, 上一次执行结果：{}", attempt.getAttemptNumber(),
                                attempt.hasException() ? attempt.getExceptionCause().getMessage() : attempt.getResult());
                    }
                })//注册重试监听器，可以多次注册
                .build();

        AtomicInteger info = new AtomicInteger(0);
        Boolean result = retryer.call(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return doWork(info);
            }
        });
        log.info("最终执行结果：{}", result);
    }

    public static void main(String[] args) throws Exception {
        primary();
    }
}
