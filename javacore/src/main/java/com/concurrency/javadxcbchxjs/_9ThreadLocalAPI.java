package com.concurrency.javadxcbchxjs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class _9ThreadLocalAPI {
    public static long timeStamp(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }

    //演示SimpleDateFormat是线程不安全的以及使用ThreadLocal解决
    static class ShowSimpDateFormatNotThSafe{
        static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        static Date dateA = new Date(1243254);
        static Date dateB = new Date(93778532);
        private static void showThreadNotSafeA(){
            String format = dateFormat.format(dateA);
            String formatB = dateFormat.format(dateB);
            System.out.println(format +" "+ formatB);
        }
        private static void showThreadNotSafeB() throws Exception{
            String format = dateFormat.format(dateB);
            Date parse = dateFormat.parse(format);
            String format1 = dateFormat.format(parse);
            System.out.println(format.equals(format1));
        }
        public static void main(String[] args) {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            while (true){
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //showThreadNotSafeA();
                            showThreadNotSafeB();
                        }catch (Exception e){}
                    }
                });
            }
            /*
              A方法打印结果：
                1970-01-01 1970-01-02
                1970-01-01 1970-01-01
                1970-01-01 1970-01-02
                出现了一个不一样的打印，多线程环境下由于SimpleDateFormat中的Canlender操作不是原子的
                容易在format的时候传入的数据被其他线程篡改，出现连续打印1970-01-01的情况
                出现这种也是可能的 1970-01-02 1970-01-02
                不光format方法，parse方法也存在线程安全问题

              B方法打印结果：
                出现了false
            **/
        }
    }
    /**
     * 使用ThreadLocal解决SimpleDateFormat的线程安全问题
     * 1.static ThreadLocal指定一个覆写初始化方法initialValue,返回SimpleDateFormat
     * 2.更改其他时间API
     * */
    static class ThreadSafeSimpleDateFormat{
        //写法1
        private static final ThreadLocal<DateFormat> dateFormatInThreadLocal = new ThreadLocal<DateFormat>(){
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd");
            }
        };
        //写法2
        private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();
        private static SimpleDateFormat getDateFormat(){
            SimpleDateFormat dateFormat = threadLocal.get();
            //每个线程单独判断并生成和保存实例
            if (dateFormat == null){
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                threadLocal.set(dateFormat);
            }
            return dateFormat;
        }

    }

    /**
     * ThreadLocal既能共享变量，也能隔离自定义变量
     */
    static class TestThreadLocal{
        static class ThreadLocalExt extends ThreadLocal{
            @Override
            protected Object initialValue() {
                return new Date().getTime();
            }
        }
        public static void main(String[] args) throws InterruptedException {
            ThreadLocalExt threadLocalExt = new ThreadLocalExt();
            Thread a = new Thread(){
                @Override
                public void run() {
                    System.out.println(threadName()+threadLocalExt.get());
                }
            };
            Thread b = new Thread(){
                @Override
                public void run() {
                    System.out.println(threadName()+threadLocalExt.get());
                }
            };
            a.start();
            TimeUnit.SECONDS.sleep(2);
            System.out.println(threadName()+threadLocalExt.get());
            b.start();
            /**
             * Thread-01581605987461
             * main1581605989465
             * Thread-11581605989465
             * ThreadLocal不光隔离各线程自己的变量，还能隔离默认变量
             */
        }
    }

    /**
     * @title 父子线程的值继承
     */
    static class SimpleThreadLocal{
        static InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
        public static void main(String[] args) {
            threadLocal.set("main"); //A
            new Thread(){
                @Override
                public void run() {
                    System.out.println(threadLocal.get());
                }
            }.start();
            //B
            System.out.println(threadLocal.get());
        }
        /**
         * 打印结果：
         * main
         * main
         * 如果将A行的代码移到B处：
         * main
         * null  似乎这是因为父线程还没设置，所以取不到
         */
    }

    //采用另外一种父子线程的写法
    static class AnotherFormOfParentChildThread{
        static InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
        static class ParentThread extends Thread{
            @Override
            public void run() {
                new Thread(){
                    @Override
                    public void run() {
                        threadLocal.set("child");
                        System.out.println("chile thread : "+ threadLocal.get());
                    }
                }.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("parent thread : "+ threadLocal.get());
            }
        }
        public static void main(String[] args) {
            new ParentThread().start();
        }
        /**
         * chile thread : child
         * parent thread : null
         * child设置的值parent拿不到
         */
    }

    static class InheritableThreadLocalTest{
        static class InheritableThreadLocalExt extends InheritableThreadLocal<String>{
            @Override
            protected String initialValue() {
                return "parent_default"; //设置父线程的默认值
            }
            @Override
            protected String childValue(String parentValue) {
                System.out.println("parentValue = "+parentValue);
                String childValue = "child_default"; //设置子线程的默认值，可以选择与父线程相同
                System.out.println("childValue = "+childValue);
                return childValue;
            }
        }
        public static void main(String[] args) throws InterruptedException {
            InheritableThreadLocalExt localExt = new InheritableThreadLocalExt();
            System.out.println("父线程："+ localExt.get()); //A
            //Thread.sleep(1000);
            new Thread(){
                @Override
                public void run() {
                    System.out.println("子线程："+ localExt.get());
                }
            }.start(); //B
        }
        /**
         * -- B 早于 A 执行，ThreadLocal的childValue方法没有执行
         * 父线程：parent_default
         * 子线程：parent_default
         *
         * -- A 早于 B 执行：
         * 父线程：parent_default
         * parentValue = parent_default
         * childValue = child_default
         * 子线程：child_default
         */
    }

}
