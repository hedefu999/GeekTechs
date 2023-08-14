package com.allkindsproxy.staticproxy;

public class MainTest {
    public static void main(String[] args) {
        IHello proxy = new HelloProxy(new HelloSpeaker());
        proxy.hello("start...");
    }
}
