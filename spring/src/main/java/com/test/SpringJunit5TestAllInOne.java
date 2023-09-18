package com.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = SpringJunit5TestAllInOne.SpringAppConfig.class)
@Slf4j
public class SpringJunit5TestAllInOne {
    @Configuration
    @EnableRetry
    @ComponentScan(value = "com.test")
    static class SpringAppConfig{

    }

    @Service
    static class RetryService{
        @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(value = 1500, maxDelay = 0, multiplier = 1))
        public void service(){
            log.info("service method ...");
        }
        @Recover
        public void recover(Exception e){
            log.info("service retry times used up，lastExp = {}", e.getMessage());
        }
    }

    @Autowired
    private RetryService retryService;
    @Test //注意这个Test是jupiter的那个
    public void test(){
        retryService.service();
    }

    //这种main方法里写 new ApplicationContext 的初始化容器方式比较麻烦
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringAppConfig.class);
        RetryService retryService = context.getBean(RetryService.class);
        retryService.service();
    }

}
