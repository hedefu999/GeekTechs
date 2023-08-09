package com.jdk;

import java.util.HashMap;
import java.util.Map;

public class DoubleBraceInitialization {
    private String name;
    public DoubleBraceInitialization(String name){this.name=name;}
    public Map<String,String> getMap1(){
        Map<String, String> map = new HashMap<String, String>(){
            private static final long serialVersionUID = -8035748323739734089L;
            {
                put("name",name);
            }};
        return map;
    }
    public Map<String, String> getMap2(){
        HashMap map = new HashMap();
        map.put("name", name);
        return map;
    }
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Thread name: "+ Thread.currentThread().getName() + ", Object: "+this.name+" GC happens");
    }
    public static void main(String[] args) throws Exception{
        DoubleBraceInitialization first = new DoubleBraceInitialization("first");
        Map<String, String> map1 = first.getMap1();
        first = null;
        System.gc();
        Thread.sleep(1000);

        DoubleBraceInitialization second = new DoubleBraceInitialization("second");
        Map<String, String> map2 = second.getMap2();
        second = null;
        System.gc();
        Thread.sleep(1000);

        System.out.println("等待完毕");
        System.out.println(map1);
        System.out.println(map2);
    }
}
