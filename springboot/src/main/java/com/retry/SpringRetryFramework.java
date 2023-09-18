package com.retry;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = {SpringRetryFramework.class})
@ComponentScan({"com.retry"})
@Slf4j
public class SpringRetryFramework {


    @Configuration
    @EnableRetry
    public static class DailyTestApplicationConfig {
    }

    @Component
    public static class RetryWorkWrapper{
        //@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L,multiplier = 2))
        @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff())
        public boolean doWork(AtomicInteger info) throws Exception{
            log.info("doWork {}", info.get());
            int flag = info.getAndIncrement();
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

        @Recover
        public boolean 兜底方法(Throwable t, AtomicInteger info){
            log.info("达到最大重试次数或出现不在预期内的异常, count = {}, lastThrowable = {}", info.get(), t.getMessage());
            return false;
        }
    }

    @Autowired
    RetryWorkWrapper retryWorkWrapper;

    @Test
    public void testRetry() throws Exception {
        AtomicInteger flag = new AtomicInteger(1);
        boolean b = retryWorkWrapper.doWork(flag);
        log.info("外层拿到的最终结果 {}", b);
    }
}
