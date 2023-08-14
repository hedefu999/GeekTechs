package com.allkindsproxy.dynamicproxy2;

public class HelloImpl implements IHello {
    @Override
    public void sayHello() {
        System.out.println("你好！");
    }
}
