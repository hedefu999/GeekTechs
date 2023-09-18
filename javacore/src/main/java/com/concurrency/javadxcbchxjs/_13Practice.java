package com.concurrency.javadxcbchxjs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class _13Practice {
    public static String threadName(){
        return Thread.currentThread().getName();
    }
    /**
     * 通用案例： 如何保证线程顺序地执行，在给定某系列序号的情况下
     * 为保证线程执行的有序性，下面的程序里使用了计数取模判断的方式实现
     */
    static class SequentialThreadExecution{
        static class MyThread extends Thread{
            private Object lock;
            private String showChar;
            private int showNumPosition;
            private int printCount = 0;
            volatile private static int addNumber = 1;
            public MyThread(Object lock, String showChar, int showNumPosition){
                this.lock = lock;
                this.showChar = showChar;
                this.showNumPosition = showNumPosition;
            }
            @Override
            public void run() {
                synchronized (lock){
                    try {
                        while (true){
                            if (addNumber % 3 == showNumPosition){
                                System.out.printf("ThreadName = %s,runCount = %d,showChar = %s\n",threadName(),addNumber,showChar);
                                lock.notifyAll();
                                addNumber++;
                                printCount++;
                                if (printCount == 3){break;}//一个线程打印三次结束
                            }else {
                                lock.wait();
                            }
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        }
        public static void main(String[] args) {
            Object lock = new Object();
            MyThread a = new MyThread(lock,"A",1);
            MyThread b = new MyThread(lock,"B",2);
            MyThread c = new MyThread(lock,"C",0);
            a.start();
            b.start();
            c.start();
        }/*
         * ThreadName = Thread-0,runCount = 1,showChar = A
         * ThreadName = Thread-1,runCount = 2,showChar = B
         * ThreadName = Thread-2,runCount = 3,showChar = C
         * ThreadName = Thread-0,runCount = 4,showChar = A
         * ThreadName = Thread-1,runCount = 5,showChar = B
         * ThreadName = Thread-2,runCount = 6,showChar = C
         * ThreadName = Thread-0,runCount = 7,showChar = A
         * ThreadName = Thread-1,runCount = 8,showChar = B
         * ThreadName = Thread-2,runCount = 9,showChar = C
         */
    }

    /**
     * 演示SimpleDateFormate的线程不安全特性的基本逻辑是：
     * 在多线程环境下使用单例的SimpleDateFormat对象，将String->Date->String,发现两头的String在一个线程内不一致
     * —— com.concurrency.javadxcbchxjs._9ThreadLocalAPI.ShowSimpDateFormatNotThSafe中也提供了一个例子
     * 原因：SimpleDateFormat未做同步，内部传值在多线程下会串数据
     * 解决方案：1. 不使用单例的SimpleDateFormat,每个线程单独创建
     * 2. 使用ThreadLocal，本质上还是各个线程各自的SimpleDateFormat
     */
    //演示 方案1
    static class NewDateFormatEveryTime{
        /*
         * 使用内部的单例singleton会报下述异常
         * Exception in thread "Thread-0" java.lang.ArrayIndexOutOfBoundsException: -1
         * 并且打印: 前后不一致：source = 2012-12-20, target = 2013-08-20
         */
        static class DateTool{
            private static SimpleDateFormat singleton = new SimpleDateFormat("yyyy-MM-dd");
            public static Date parse(String formatPattern, String dateStr){
                SimpleDateFormat format = new SimpleDateFormat(formatPattern);
                Date parse = null;
                try {
                    parse = format.parse(dateStr);//singleton替换format得到不安全效果
                } catch (ParseException e) { }
                return parse;
            }
            public static String format(String pattern,Date date){
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                String formatStr = format.format(date);//singleton替换format得到不安全效果
                return formatStr;
            }
        }
        static class MyThread extends Thread{
            private static final String pattern = "yyyy-MM-dd";
            private String dateStr;
            public MyThread(String dateStr){
                this.dateStr = dateStr;
            }
            @Override
            public void run() {
                Date dateRef = DateTool.parse(pattern,dateStr);
                String newDateStr = DateTool.format(pattern,dateRef);
                if (!newDateStr.equals(dateStr)){
                    System.out.printf("前后不一致：source = %s, target = %s\n",dateStr,newDateStr);
                }
            }
        }
        public static void main(String[] args) {
            String date1Str = "2020-12-20";
            String date2Str = "2012-12-20";
            for (int i = 0; i < 10; i++) {
                if (i%2 == 0){
                    new MyThread(date2Str).start();
                }else {
                    new MyThread(date1Str);
                }
            }
        }
    }

    /**
     * 使用ThreadLocal解决SimpleDateFormat的线程不安全问题
     */
    static class ThreadLocalDateFormat{
        //方案2
        static class DateTools{
            //写法1
            private static final ThreadLocal<DateFormat> dateFormatInThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
            public static DateFormat getDateFormat1(){
                return dateFormatInThreadLocal.get();
            }

            //写法2
            private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();
            private static SimpleDateFormat getDateFormat2(){
                SimpleDateFormat dateFormat = threadLocal.get();
                //每个线程单独判断并生成和保存实例
                if (dateFormat == null){
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    threadLocal.set(dateFormat);
                }
                return dateFormat;
            }
        }
        static class MyThread extends Thread{
            private static final String pattern = "yyyy-MM-dd";
            private String dateStr;
            public MyThread(String dateStr){
                this.dateStr = dateStr;
            }
            @Override
            public void run() {
                try {
                    DateFormat dateFormat = DateTools.getDateFormat1();//1或2都可以
                    Date dateRef = dateFormat.parse(dateStr);
                    String newDateStr = dateFormat.format(dateRef);
                    if (!newDateStr.equals(dateStr)){
                        System.out.printf("前后不一致：source = %s, target = %s\n",dateStr,newDateStr);
                    }
                }catch (Exception e){e.printStackTrace();}

            }
        }
        public static void main(String[] args) {
            String date1Str = "2020-12-20";
            String date2Str = "2012-12-20";
            for (int i = 0; i < 10; i++) {
                if (i%2 == 0){
                    new MyThread(date2Str).start();
                }else {
                    new MyThread(date1Str);
                }
            }
        }
    }

    /**
     * 线程的异常可以使用UncaughtExceptionHandler进行处理，达到一个异常兜底的效果
     * 线程对象通过调用setUncaughtExceptionHandler(new UncaughtExceptionHandler(){})进行设置
     * Thread类提供了一个设置默认ExceptionHandler的方法Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
     * 这样所有线程对象都使用这个ExceptionHandler
     */
    static class ThreadExceptionHandler{
        public static void main(String[] args) {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    String a = null;a.hashCode();
                }
            };
            thread.setName("test");
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.out.println(threadName()+"发生了异常");
                    System.out.println(e.getMessage());
                }
            });
            thread.start();
        }
    }

    /**
     * 线程组中有一个线程出现异常是不影响其他线程的运行的，要实现一个线程发生问题，所有线程退出的效果:
     * ThreadGroup中有一个线程发生异常时会回调它的uncaughtException方法，可以在这是通过ThreadGroup执行interrupt方法达到停止所有线程的目的
     * 注意：
     * 1. ThreadGroup要使用interrupt方法，每个线程的run方法中不能有sleep方法
     * 2. 线程run方法不能把所有异常都捕获了，这样无法透传给ThreadGroup
     * 3. 线程的setUncaughtExceptionHandler方法优先级高于ThreadGroup#uncaughtException
     * 4. ThreadGroup#uncaughtException方法的优先级高于线程的setDefaultUncaughtExceptionHandler方法
     *    可以通过修改ThreadGroup#uncaughtException方法达到与setDefaultUncaughtExceptionHandler方法优先级同级的效果
     *    就是在uncaughtException方法中添加super.uncaughtException(t,e);
     *    原因是ThreadGroup的uncaughtException方法会查找defaultExceptionHandler并去执行
     */
    static class ExceptionInThreadGroup{
        static class MyThread extends Thread{
            private String num;//num的作用就是用于抛出异常
            public MyThread(ThreadGroup group,String name,String num){
                super(group, name);
                this.num = num;
            }
            @Override
            public void run() {
                Integer.parseInt(num);
                while (!this.isInterrupted()){
                    //try {
                    //    TimeUnit.SECONDS.sleep(2);
                    //} catch (InterruptedException e) { e.printStackTrace(); }
                    System.out.println(threadName()+"循环中");
                }
            }
        }
        public static void main(String[] args) {
            ThreadGroup group = new ThreadGroup("线程组"){
                //ThreadGroup的UncaughtExceptionHandler
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    super.uncaughtException(t,e);
                    System.out.printf("线程 %s 是叛徒，它抛出了一个异常：%s\n",t.getName(),e.getMessage());
                    this.interrupt();
                }
            };
            //线程类的DefaultUncaughtExceptionHandler
            MyThread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.out.println("线程类默认的异常捕获器");
                }
            });
            //这里的两处线程创建并运行的代码的顺序任意，不会因为taraitor在最前面MyThread-4 1次也得不到执行
            MyThread myThread1 = new MyThread(group, "traitor", "poision");
            //线程单独定制的ExceptionHandler 会让ThreadGroup拿不到异常
            //myThread1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            //    @Override
            //    public void uncaughtException(Thread t, Throwable e) {
            //        System.out.println("traitor逃避了ThreadGroup的检查");
            //    }
            //});
            myThread1.start();
            for (int i = 0; i < 5; i++) {
                MyThread myThread = new MyThread(group, "" + i, i + "");
                myThread.start();
            }
        }
    }

}
