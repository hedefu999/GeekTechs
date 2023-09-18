package com.allkindsproxy.dynamicproxy;

import com.allkindsproxy.staticproxy.HelloSpeaker;
import com.allkindsproxy.staticproxy.IHello;

public class DynamicTest {
    public static void main(String[] args) {
        System.getProperties().setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        LogHandler logHandler = new LogHandler();
        IHello bind = (IHello) logHandler.bind(new HelloSpeaker());
        bind.hello("talk！");

    }
}
