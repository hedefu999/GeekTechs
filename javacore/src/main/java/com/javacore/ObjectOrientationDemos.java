package com.javacore;

import java.util.HashMap;
import java.util.Map;

public class ObjectOrientationDemos {
    //奇怪的死循环
    static class Foo{
        private Foo foo = new Foo();
        public Foo() {}
        public static void main(String[] args) {
            new Foo().foo.toString();
        }
    }
    //Double Brackets Initializer
    static class DoubleBracketsInitializerDemos{
        static class Primary{
            /** 双括号初始化，使用匿名内部类实现，字节码反编译：
             class Primary$1 extends HashMap{
                 Primary$1(){
                     put(1,"jack");
                     put(2,"lucy");
                 }
             }
             */
            Map<Integer,String> map = new HashMap<Integer, String>(4){{
                put(1,"jack");
                put(2,"lucy");
            }};
        }
        static class DBIGCGlitch {
            private String name;
            public DBIGCGlitch(String name){this.name=name;}
            public Map<String,String> getMap1(){
                Map<String, String> map = new HashMap<String, String>(){
                    private static final long serialVersionUID = -8035748323739734089L;
                    {
                        put("name",name);
                    }};
                return map;
            }
            public Map<String, String> getMap2(){
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                return map;
            }
            @Override
            protected void finalize() throws Throwable {
                System.out.println("Thread name: "+ Thread.currentThread().getName() + ", Object: "+this.name+" GC happens");
            }
            public static void main(String[] args) throws Exception{
                DBIGCGlitch first = new DBIGCGlitch("first");
                Map<String, String> map1 = first.getMap1();
                first = null;
                System.gc();
                Thread.sleep(1000);

                DBIGCGlitch second = new DBIGCGlitch("second");
                Map<String, String> map2 = second.getMap2();
                second = null;
                System.gc();
                Thread.sleep(1000);

                System.out.println("等待完毕");
                System.out.println(map1);
                System.out.println(map2);
            }
        }
    }

    static class InitializationSequence{
        static class Foo {
            int i = 1;
            Foo() {
                System.out.println(i);
                int x = getValue();
                System.out.println(x);
            }
            {i = 2;}
            protected int getValue() {
                return i;
            }
        }
        //子类
        static class Bar extends Foo {
            int j = 1;
            Bar() {
                j = 2;
            }
            {j = 3;}
            @Override
            protected int getValue() {
                return j;
            }
        }
        static class ConstructorExample {
            public static void main(String... args) {
                Bar bar = new Bar();
                System.out.println(bar.getValue());
            }
        }
    }
}
