package com.misc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InnerClassTest {
    /**
     非静态内部类不允许有static成员变量或方法.
     否则将出现 Outer.Inner.staticField 访问方式
     企图在Wrapper Inner均未初始化时访问成员变量
     */
    static class Wrapper{
        public static String wrapperName = "jacky";
        public class Inner{
            public /*static*/ String name = "jack";
            public final Integer age = 12;
            public /*static*/ String getInfo(){
                return "name";//"name "+ age;
            }
        }
        static class Inner2{
            public static String name = "lucy";
            public static String getInfo(){
                return name;
            }
        }

        public static void main(String[] args) {
            //Inner.name
            //Wrapper.Inner.name;
        }
    }

    /**
     * 内部内的一个奇葩用法：改变集合的add写法、变量声明
     */
    /**
     * HashMap的双括号写法
     */
    static class TestForDubbleBrackets{
        public TestForDubbleBrackets(){
            System.out.println("---构造函数---");
        }
        static {
            System.out.println("---静态代码块---");
        }
        {
            System.out.println("---普通代码块---");
        }
        public static void main(String[] args) {
            new TestForDubbleBrackets();
            System.out.println("------");
            new TestForDubbleBrackets();
            /*
             ---静态代码块---
             ---普通代码块---
             ---构造函数---
             ------
             ---普通代码块---
             ---构造函数---
             */
        }
    }
    @Test
    public void test90() {
        Map<Integer,String> map = new HashMap<Integer, String>(4){{
            put(1,"jack");
            put(2,"lucy");
        }};
        /**
         class Test$1 extends HashMap{
         Test$1(){
         put(1,"jack");
         put(2,"lucy");
         }
         }
         */
        List<String> names = new ArrayList<String>(){{
            for (int i = 0; i < 5; i++) {
                add("name-"+i);
            }
        }};
    }
    /*
     这种匿名内部类写法存在的问题
     1.此种方式是匿名内部类的声明方式，所以引用中持有着外部类的引用。
     串行化这个集合时外部类也会被不知不觉的串行化，当外部类没有实现serialize接口时，就会报错 待验证

     2.声明了一个继承自HashMap的子类。然而有些串行化方法，例如要通过Gson串行化为json，或者要串行化为xml时，类库中提供的方式，是无法串行化Hashset或者HashMap的子类的，从而导致串行化失败。解决办法：重新初始化为一个HashMap对象：
     new HashMap(map);
     这样就可以正常初始化了。

     3.执行效率问题
     */
    static class VariableDeclareInnerClassTest {
        int e = 6;
        VariableDeclareInnerClassTest() {
            int c = 1;
            this.f = 5;
            int e = 66;
        }
        int f = 55;
        int c = 11;
        int b = 1;
        {
            a = 3;
            b = 22;
        }
        int a = 33;
        static {
            d = 4;
        }
        static int d = 44;
        int g = 7;
        int h = 8;
        public int test(){
            g = 77;
            int h = 88;
            System.out.println("h - 成员变量：" + this.h);
            System.out.println("h - 局部变量: " + h);
            return g;
        }
        /* 上面的内容反编译后变成
        int e = 6;
        int f = 55;
        int c = 11;
        int b = 1;
        int a = 3;
        static int d = 4;
        int g;
        int h;
        static {
            d = 44;
        }
        VariableDeclareInnerClassTest() {
            this.b = 22;
            this.a = 33;
            this.g = 7;
            this.h = 8;
            int c = true;
            this.f = 5;
            int e = true;
        }
         public int test() {
            this.g = 77;
            int h = 88;
            System.out.println("h - 成员变量：" + this.h);
            System.out.println("h - 局部变量: " + h);
            return this.g;
        }
         */
        public static void main(String[] args) {
            VariableDeclareInnerClassTest test = new VariableDeclareInnerClassTest();
            System.out.println("a: " + test.a);
            System.out.println("b: " + test.b);
            System.out.println("c: " + test.c);
            System.out.println("d: " + test.d);
            System.out.println("f: " + test.f);
            System.out.println("e: " + test.e);
            System.out.println("g: " + test.test());
        }
    }
}
