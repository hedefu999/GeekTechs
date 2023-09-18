package com.ssmr.c13;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("com.ssmr.c13.*")
@EnableTransactionManagement
public class TransacManageConfig implements TransactionManagementConfigurer {
    private DataSource dataSource = null;
    @Bean(name = "dataSource")
    public DataSource initDataSource(){
        if (dataSource != null){
            return dataSource;
        }
        Properties props = new Properties();
        props.setProperty("driverClassName","com.mysql.cj.jdbc.Driver");
        props.setProperty("url","jdbc:mysql://localhost:3306/ssmr");
        props.setProperty("username","root");
        props.setProperty("password","...999");
        props.setProperty("maxActive","200");
        props.setProperty("maxIdle","20");
        props.setProperty("maxWait","30000");
        try {
            dataSource = BasicDataSourceFactory.createDataSource(props);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate initJdbcTemplate(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(initDataSource());
        return jdbcTemplate;
    }

    @Bean(name = "transactionManager")
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(initDataSource());
        return transactionManager;
    }
}
