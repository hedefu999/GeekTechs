package com.allkindsproxy.cglibproxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class AgencyOperator implements MethodInterceptor {
    //CGLib对比JDK动态代理的一个不同之处是：接口实现方法里不再需要传入被代理的对象
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //下面一行存在死循环
        //Object result = method.invoke(proxy, args);
        //这一行前后再进行操作就是拦截器链了
        Object result = methodProxy.invokeSuper(proxy, args);
        return result;
    }
}
