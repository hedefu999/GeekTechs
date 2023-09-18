package com.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)//加载junit4框架
//@ContextConfiguration
@Slf4j
public class SpringJunit4TestAllInOne {
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

    @Test
    public void test(){
        retryService.service();
    }

}
