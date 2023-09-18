package com.ssmr.txpractice;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.MalformedURLException;

@Configuration
//下面两种扫描包的方式任选
//@ComponentScan(basePackageClasses = {RoleBasicService.class,StudentBasicService.class})
@ComponentScan(basePackages = {"com.ssmr.txpractice"})//,excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {RoleBasicService.class})})
@PropertySource(value = {"classpath:jdbc.properties"})
@EnableTransactionManagement
public class SpringMybatisConfig {
    //从property文件里读取属性
    @Bean
    public static DataSource dataSource(@Value("${db.username}") String userName,
                                        @Value("${db.password}") String password,
                                        @Value("${db.url}") String url,
                                        @Value("${db.driver}") String driver){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        return druidDataSource;
    }

    //此bean扫描mapper.java
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer scannerConfig = new MapperScannerConfigurer();
        scannerConfig.setBasePackage("com.ssmr.txpractice.mapper");
        scannerConfig.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        //强制Mapper.java必须加注解才能被注进来
        scannerConfig.setAnnotationClass(Repository.class);
        return scannerConfig;
    }



    /**
     * 试验记录
     * factoryBean.setDataSource(dataSource())仅适用于入参为空的dataSource注入方式
     * 使用全局变量static DataSource dataSource = null; 在dataSource(...)方法中初始化
     * -- sqlSessionFactoryBean早于dataSource方法执行，@Configuration类里的方法执行顺序不是书写顺序
     * 但发现@Configuration类的@Bean方法执行顺序使用@Order无法控制,@Order应该是应用在类上的。
     *
     * 解决方案是将dataSource作为bean注入，这样方法执行会转而初始化DataSource，类似xml中ref的效果
     * 这个@Autowired加到方法上、入参前、删掉都不影响spring按照入参名寻找并初始化bean
     */
    //此bean扫描mapper.xml
    @Bean
    public static SqlSessionFactoryBean sqlSessionFactoryBean(@Autowired DataSource dataSource){
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        try {
            //java代码配置sqlsessionfactorybean,mapperLocations是一个数组，可以在xml里<array><value></value><value></value></array>
            FileUrlResource fileUrlResource = new FileUrlResource("src/main/resources/com/ssmr/txpractice/sqlmap/*.xml");
            FileSystemResource roleMapperResource = new FileSystemResource("src/main/resources/com/ssmr/txpractice/sqlmap/RoleMapper.xml");
            FileSystemResource stuMapperResource = new FileSystemResource("src/main/resources/com/ssmr/txpractice/sqlmap/StudentMapper.xml");
            FileSystemResource userMapperResource = new FileSystemResource("src/main/resources/com/ssmr/txpractice/sqlmap/UserMapper.xml");
            Resource[] resources = {roleMapperResource,stuMapperResource,userMapperResource};
            factoryBean.setMapperLocations(resources);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        config.setCacheEnabled(true);
        config.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
        config.setLogPrefix("ssmr :- ");
        config.setCallSettersOnNulls(false);
        factoryBean.setConfiguration(config);
        return factoryBean;
    }

    @Bean
    public static PlatformTransactionManager transactionManager(DataSource dataSource){
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

}
