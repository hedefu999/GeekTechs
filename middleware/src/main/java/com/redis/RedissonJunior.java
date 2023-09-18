package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonJunior {
    @Configuration
    static class RedissionConfig{
        @Value("redis://127.0.0.1:6379")
        private String redisHostPort;
        private String password;

        public RedissonClient getRedission(){
            Config config = new Config();
            config.useSingleServer()
                    .setAddress(redisHostPort)
                    .setPassword(password);
            config.setCodec(new JsonJacksonCodec());
            return Redisson.create(config);
        }
    }
    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {
        String locKey = "";
        RLock rLock = redissonClient.getLock(locKey);
        try {
            boolean isLocked = rLock.tryLock(10, TimeUnit.SECONDS);
            log.info("acquire lock successfully?: {}", isLocked);
        }catch (Exception e){
            log.error("acquire lock with exception {}", e.getMessage());
        }finally {
            rLock.unlock();
        }

    }
}
