package com.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring Retry是从Spring Batch独立出来的一个功能，已经广泛应用于Spring Batch,Spring Integration, Spring for Apache Hadoop等Spring项目。
 * 保证容错性，可用性，一致性等。一般用来应对外部系统的一些不可预料的返回、异常等，特别是网络延迟，中断等情况。
 * 还有在现在流行的微服务治理框架中，通常都有自己的重试与超时配置，比如dubbo可以设置retries=1，timeout=500调用失败只重试1次，
 * 超过500ms调用仍未返回则调用失败。如果我们要做重试，要为特定的某个操作做重试功能，则要硬编码，重复开发效率低下
 */
@Slf4j
public class RetryTemplateDemo {
    /**
        哪些异常需要重试，key表示异常的字节码，value=true表示需要重试，写false表示遇到这种异常就不重试
        之所以有false的设定，是因为可以填父类Exception.class，true，再来排除一些子类
     */
    static Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<Class<? extends Throwable>, Boolean>(){{
        //put(RemoteAccessException.class, true);
        put(Exception.class, true);
    }};

    //复习下关于异常的常识 https://blog.51cto.com/u_15951177/6033087
    public static boolean doWork(int flag) throws Exception{
        if (flag == 0){
            return false;//true or false并不能直接重试
        }else if (flag == 1){
            throw new ConnectException("连接异常，受检异常");
        }else if (flag==2){
            throw new RemoteAccessException("远程接入异常，非受检 运行时异常");
        }else if (flag==3){
            int i=1/0;
        }
        return true;
    }

    /**
     代码中的SimpleRetryPolicy是重试策略，其他还有：
     - NeverRetryPolicy 只允许调用 RetryCallback 一次，不允许重试
     - AlwaysRetryPolicy 允许无限重试，直到成功
     - SimpleRetryPolicy 固定次数重试，默认最大重试次数3，RetryTemplate默认使用的重试策略
     - TimeoutRetryPolicy 超时时间重试策略，默认超时时间1s，在指定的超时时间内允许重试
     - CompositeRetryPolicy 组合重试策略，有两种组合方式，（注意组合中的每一个策略都会执行）
        - 乐观组合重试策略：只要有一个策略允许即可以重试
        - 悲观组合重试策略：只要有一个策略不允许即可以重试
     - ExceptionClassifierRetryPolicy 设置不同异常的重试策略，类似组合重试策略，区别在于这里只区分不同异常的重试
     - CircuitBreakerRetryPolicy 有熔断功能的重试策略，需设置 openTimeout、resetTimeout、delegate
     示例中的BackoffRetryPolicy是重试回退策略：定义每次重试是立即重试还是等待一段时间重试
     - NoBackOffPolicy 无退避算法策略，每次重试立即重试
     - FixedBackoffPolicy 固定时间的退避策略，需设置参数sleeper和backOffPeriod
        - sleeper 指定等待策略，默认是Thread.sleep
        - backOffPeriod 指定休眠时间，默认1s
     - UniformRandomBackOffPolicy 随机时间退避策略，需设置 sleeper、minBackOffPeriod（默认500ms）、maxBackOffPeriod（默认1500ms）
        - 此策略会在 minBackOffPeriod ~ maxBackOffPeriod 之间取一个随机休眠时间
     - ExponentialBackOffPolicy 指数退避策略，需设置 sleeper、initialInterval（初始休眠时间，默认100ms）、
        - maxInterval（最大休眠时间，默认30s）、
        - multiplier（指定乘数，即下一次休眠时间为 当前休眠时间*multiplier）
     - ExponentialRandomBackOffPolicy 随机指数退避策略，引入随机乘数可以实现随机乘数回退
     */
    static void simpleRetryHowTo() throws Exception{
        //设置重试策略，主要是重试次数
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, exceptionMap);
        //设置重试回退操作策略，设置重试时间间隔(backoff 补偿)
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        //backOffPolicy.setSleeper(); 指定等待策略，默认是Thread.sleep，即线程休眠
        backOffPolicy.setBackOffPeriod(1000L);//重试时间间隔，默认1000ms

        //构建重试模板实例
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        AtomicInteger flag = new AtomicInteger(1);
        Boolean execute = retryTemplate.execute(new RetryCallback<Boolean, Exception>() {
            @Override
            public Boolean doWithRetry(RetryContext context) throws Exception {
                log.info("重试中：count = {}, exception = ", context.getRetryCount(), context.getLastThrowable());
                return doWork(flag.getAndIncrement());
            }
        }, new RecoveryCallback<Boolean>() {
            @Override
            public Boolean recover(RetryContext context) throws Exception {
                log.info("重试结束：totalCount = {}, lastException = ", context.getRetryCount(), context.getLastThrowable());
                //int i=1/0; recover 方法中不能抛异常，会抛给main方法
                return false;
            }
        });
        log.info("最终的执行结果：{}", execute);
    }

    /**
     * 限时10s执行，每隔3s重试,不考虑方法执行耗时的情况下，至多可以执行4次方法，最后执行recovery方法
     *            每隔4s重试，不考虑方法执行耗时的情况下，至多可以执行3次方法
     *            每隔3s重试，方法执行耗时3s，至多可以执行2次方法，只重试了1次
     */
    static void timeoutRetryHowTo() throws Exception{
        TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
        timeoutRetryPolicy.setTimeout(10*1000);
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(3000L);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(timeoutRetryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        System.out.println(LocalDateTime.now());
        String executeResult = retryTemplate.execute(context -> {
            System.out.printf("entered count = %d , lastExp = %s\n", context.getRetryCount(), context.getLastThrowable());
            //TimeUnit.SECONDS.sleep(3); 模仿网络连接超时
            int i = 1/0;
            return "";
        }, context -> {
            System.out.printf("enter recovery method,count = %d , lastExp = %s\n", context.getRetryCount(), context.getLastThrowable());
            return "success";
        });
        System.out.printf("%s - %s\n", LocalDateTime.now().toString(), executeResult);
    }


    public static void main(String[] args) throws Exception{
        timeoutRetryHowTo();
    }
}
