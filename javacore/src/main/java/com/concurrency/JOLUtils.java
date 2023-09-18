package com.concurrency;

import org.openjdk.jol.info.ClassLayout;

public class JOLUtils {
    public static void printObjectHeader(Object obj){
        //System.identityHashCode(obj); 这一行在测试中发现会导致锁升级，慎用
        String info = ClassLayout.parseInstance(obj).toPrintable();
        System.out.println(info);
    }
    public static void printMarkWord(Object obj,String location){
        String info = ClassLayout.parseInstance(obj).toPrintable();
        String[] splits = info.split("\n");
        System.out.println(location);
        System.out.println(splits[1]);
        System.out.println(splits[2]);
        System.out.println(splits[3]);
    }
}
