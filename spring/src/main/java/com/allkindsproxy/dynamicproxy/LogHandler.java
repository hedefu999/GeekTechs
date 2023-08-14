package com.allkindsproxy.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LogHandler implements InvocationHandler {
    //private Logger log = LoggerFactory.getLogger(getClass());
    //被代理对象
    private Object delegate;
    //返回代理对象，他会自动执行invoke方法,并将返回的result作为被代理对象的执行结果
    public Object bind(Object object){
        this.delegate = object;
        //动态代理源码解析 refto https://blog.csdn.net/wangqyoho/article/details/77584832
        return Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(),
                delegate.getClass().getInterfaces(),
                //java8特性：函数式接口可以简写成双冒号，InvocationHandler接口方法有两个就无法这样写了
                //this);
                //(Object proxy, Method method, Object[] args) -> this.invoke(proxy,method,args));
                this::invoke);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try{
            //log.info("方法执行前。。。");
            //proxy是被代理对象
            //System.out.println(proxy instanceof IHello);
            //通过反射执行被代理对象的方法
            // method.setAccessible(true); JDK动态代理的方法是从接口过来的，不会是private
            result = method.invoke(delegate,args);
            //log.info("方法执行后。。。");
        }catch (Exception e){}
        //返回通过反射执行的被代理的方法的执行结果
        return result;
    }
    private void proxyPriavteMehthod(){
        System.out.println("这是代理对象的私有方法！");
    }
}
