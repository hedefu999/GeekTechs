package com.javaagent.primary;

import java.lang.instrument.Instrumentation;
/**
 * 含有premain方法的HelloAgent类
 * premain有两个重载的方法，注意第一个参数是String，优先执行带有Instrumentation入参的方法
 */
public class HelloAgent {
    public static void premain(String arg, Instrumentation inst) {
        System.out.println("Hello Agent with Instrument: " + arg);
    }

    public static void premain(String arg) {
        System.out.println("Hello Agent: " + arg);
    }
}
