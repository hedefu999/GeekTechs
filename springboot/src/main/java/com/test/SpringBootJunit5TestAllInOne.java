package com.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

/**
 * 为便于日常测试研究和代码维护，将一个案例所有的Java类都写在一个文件里，甚至可以分章节
 * 如果需要测试mvc功能，继续参考 https://mkyong.com/spring-boot/spring-boot-junit-5-mockito/
 */
@SpringBootTest(classes = {SpringBootJunit5TestAllInOne.class})
@ComponentScan(basePackages = "com.test") /* ComponentScan 要跟随 SpringBootTest注解所在类，否则会有bean注入失败 */
//@ContextConfiguration(classes = {SpringBootJunit5TestAllInOne.TestConfig.class})
@Slf4j
public class SpringBootJunit5TestAllInOne {

    /* 项目配置,不声明@Configuration就要在 @ContextConfiguration 中声明 */
    @Configuration
    public static class TestConfig{
        @Bean
        public PetDog getFriend(){
            return new PetDog();
        }
    }

    /* 一些类声明 */
    public static class PetDog{
        public String bark(){
            return "wof wang!";
        }
    }

    /* java bean 类声明，接口和实现类 */
    public static interface HelloService {
        public String get();
    }

    public interface HelloRepository {
        String get();
    }
    //所有类必须声明为static的，否则注入失败，public可有可无
    @Service
    static class HelloServiceImpl implements HelloService {
        @Autowired
        HelloRepository helloRepository;
        @Override
        public String get() {
            return helloRepository.get();
        }
    }

    @Repository
    public static class HelloRepositoryImpl implements HelloRepository {
        @Override
        public String get() {
            return "return one item from repository";
        }
    }

    @Autowired
    HelloService helloService;
    @Autowired
    HelloRepository helloRepository;
    @Autowired
    private PetDog petDog;

    @Test
    void testGet() {
        log.info("when you feel alone {}", petDog.bark());
        log.info("Hello JUnit 5 - {}", helloService.get());
        //log.info("check HelloRepository injection {}", helloRepository.get());
    }

}
