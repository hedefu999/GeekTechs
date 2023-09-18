package com.allkindsproxy.cglibproxy;

import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Welcom proxy = cglibUsage();

        testManyFeatures(proxy);
    }
    public static Welcom cglibUsage(){
        AgencyOperator operator = new AgencyOperator();

        Enhancer enhancer = new Enhancer();
        //设置增强类型
        enhancer.setSuperclass(Welcom.class);
        //定义代理逻辑对象为当前对象，代理逻辑对象必须实现MethodInterceptor接口
        enhancer.setCallback(operator);
        //生成代理对象
        Welcom agency = (Welcom) enhancer.create();
        //cglib被代理方法如果是private的，这里编译不通过
        // 所以@Transactional加在private方法上，即便是编译通过（IDEA提示“使用@Transactional注解的方法必须是overridable）
        agency.sayHello();
        System.out.println();
        //final方法是可以编译通过，但CGLib就代理不了了，JDKProxy却可以（接口实现的Override方法上额外增加final）
        //agency.myfaith();
        return agency;
    }
    /**
     * 如果希望代理目标对象的所有方法，包括非来自接口的方法，就需要使用CGLib
     * 但对这些方法的定义有要求：
     * 通常来自接口的方法只会是一个public的
     * 但非来自接口的方法如果是final、private的CGLib无法代理,protected方法可以代理
     * spring在其文档上提示使用native AspectJ weaving
     */
    public static void testManyFeatures(Welcom proxy)throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        //sayBadWords是private的，java语法上不能调用，使用反射试试
        Method sayBackWordsMethod = Welcom.class.getDeclaredMethod("myfaith");//sayBadWords
        sayBackWordsMethod.setAccessible(true);
        sayBackWordsMethod.invoke(proxy,null);
        //通过反射执行的没有任何advice执行
    }
}
