package com.concurrency.javadxcbchxjs;

/**
  # 《java多线程编程核心技术》一书
 
 * @title 线程状态/生命周期转换研究
线程状态定义见 enum java.lang.Thread.State，注意：没有running这个状态定义
NEW 至今尚未启动的线程处于这种状态
RUNNABLE 正在JVM执行的线程处于该状态
BLOCKED 受阻塞并等待某个监视器锁的线程 synchronized等待锁
WAITING 无限期等待另一个线程来执行某一特定操作的线程处于这种状态 wait() join()
TIMED_WAITING 等待另一线程来执行取决于指定等待时间的操作的线程处于这种状态 sleep(xx) wait(xx) join(xx)
TERMINATED 已退出的线程

运行中状态 Running <-notify()- WAITING； NEW -start()-> RUNNABLE
 */
public class _0ThreadStateTransform {
    public static long timeStamp(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }
    public static String threadState(){return Thread.currentThread().getState().name();}
    public static void printThreadState(){System.out.printf("%s-%s-%s\n",timeStamp(),threadName(),threadState());}
    /**
     构造方法中打印出的状态实际上是main线程的状态 -- RUNNABLE
     */
    static class BasicTest{
        static class MyThread extends Thread{
            public MyThread(){
                System.out.printf("构造方法中的状态：%s - %s\n", threadName(), Thread.currentThread().getState());
            }
            @Override
            public void run() {
                System.out.printf("run方法中的状态：%s - %s\n",threadName(), Thread.currentThread().getState());
            }
        }
        public static void main(String[] args) {
            try {
                MyThread myThread = new MyThread();myThread.setName("myThread");
                System.out.println("main方法中myThread的状态1："+myThread.getState());
                Thread.sleep(1000);
                myThread.start();
                Thread.sleep(1000);
                System.out.println("main方法中myThread的状态2："+myThread.getState());
            }catch (Exception e){}
        }
        /*
         * 构造方法中的状态：main - RUNNABLE
         * main方法中myThread的状态1：NEW
         * run方法中的状态：myThread - RUNNABLE
         * main方法中myThread的状态2：TERMINATED
         */
    }
    /**
     * 展示sleep方法带来TIMED_WAITING状态
     */
    static class ShowTimedWaiting{
        public static void main(String[] args) throws InterruptedException {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        System.out.printf("%s - %s\n",threadName(),threadState());
                        Thread.sleep(5000);
                        System.out.printf("%s - %s\n",threadName(),threadState());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };thread.setName("thread");
            thread.start();
            Thread.sleep(1000);
            System.out.printf("thread线程的状态：%s\n",thread.getState());
            /*
             * thread - RUNNABLE
             * thread线程的状态：TIMED_WAITING
             * thread - RUNNABLE
             */
        }
    }
    /**
     * 展示一个线程处于BLOCKED状态
     */
    static class ShowBlock2{
        static Object lock = new Object();
        public static void main(String[] args) throws InterruptedException {
            Thread threadA = new Thread(){
                @Override
                public void run() {
                    synchronized (lock){
                        try {
                            System.out.printf("%s-%s-%s\n",timeStamp(),threadName(),threadState());
                            Thread.sleep(3000);
                        } catch (InterruptedException e) { }
                    }
                }
            };
            Thread threadB = new Thread(){
                @Override
                public void run() {
                    synchronized (lock){
                        System.out.printf("%s-%s-%s\n",timeStamp(),threadName(),threadState());
                    }
                }
            };
            threadA.start();
            System.out.println(threadA.getState());
            Thread.sleep(500);
            threadB.start();
            Thread.sleep(500);//BLOCK状态不是一下就到达的，main线程需要等一小会
            System.out.println(threadB.getState());
            /*
             * RUNNABLE
             * 7072-Thread-0-RUNNABLE
             * BLOCKED
             * 93-Thread-1-RUNNABLE
             */
        }
    }



}
