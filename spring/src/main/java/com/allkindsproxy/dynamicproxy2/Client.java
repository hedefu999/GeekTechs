package com.allkindsproxy.dynamicproxy2;

public class Client {
    public static void main(String[] args) {
        BusinessConselor conselor = new BusinessConselor();
        IHello iHello = new HelloImpl();
        IHello agency = (IHello) conselor.createAgency(iHello);
        System.out.println("客户拿到代理执行");
        agency.sayHello();
    }

}
