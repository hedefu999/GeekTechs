package com.allkindsproxy.dynamicproxy2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class BusinessConselor implements InvocationHandler {
    private Object realEntity;
    private Object agency;
    public Object createAgency(Object realEntity){
        this.realEntity = realEntity;
        Object agency = Proxy.newProxyInstance(
                realEntity.getClass().getClassLoader(),
                realEntity.getClass().getInterfaces(),
                this);
        this.agency = agency;
        return agency;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //该方法内对proxy的任何引用都会使得程序死循环抛出异常
        //System.out.println(proxy.hashCode());
        //System.out.println(agency.hashCode());
        //这里传入proxy会死循环
        Object result = method.invoke(this.realEntity, args);
        return result;
    }
}
