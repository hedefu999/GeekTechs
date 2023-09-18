package com.allkindsproxy.cglibproxy;

import org.springframework.cglib.proxy.Enhancer;

public class Client {
    public static void main(String[] args) {
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
    }
}
