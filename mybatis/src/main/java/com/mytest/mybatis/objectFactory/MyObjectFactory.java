package com.mytest.mybatis.objectFactory;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * @author ...
 */
public class MyObjectFactory extends DefaultObjectFactory{
    private final Logger log = LoggerFactory.getLogger("MyObjectFactory");
    private Object temp = null;
    @Override
    public <T> T create(Class<T> type) {
        T result = super.create(type);
        log.info("创建对象：{}", result.toString());
        log.info("对象创建比对:{}.", result == temp);
        return result;
    }

    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        T result = super.create(type, constructorArgTypes, constructorArgs);
        log.info("Bean创建结果:{}.", result.toString());
        temp = result;
        return result;
    }

    @Override
    public void setProperties(Properties properties) {
        log.info("properties=:{}.", properties.toString());
        super.setProperties(properties);
    }
}
