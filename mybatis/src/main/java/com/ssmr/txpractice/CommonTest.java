package com.ssmr.txpractice;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * 不需要spring框架启动的一般测试
 */
public class CommonTest {
    @Test
    public void testDateTime(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.getSecond());
        System.out.println(now.getNano());
    }
    @Test  //测试读取配置文件
    public void testPropsRead()throws Exception{
        Properties props = new Properties();
        //相对路径是从src开头。。。
        File file = new File("src/main/resources/jdbc.properties");
        FileInputStream inputStream = new FileInputStream(file);
        props.load(inputStream);
        //从配置文件流生成的Property设置到DruidDataSource里
        System.out.println("user = "+props.getProperty("db.username")+props.getProperty("db.password")+props.getProperty("${db.url}")+props.getProperty("${db.driver}"));
        /**
         * user = root...999nullnull
         */
    }
}
