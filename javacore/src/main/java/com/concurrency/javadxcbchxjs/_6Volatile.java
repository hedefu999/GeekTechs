package com.concurrency.javadxcbchxjs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @title 2.3 votaile关键字
 * @desc votaile用于使得变量在多个线程间可见
 */
public class _6Volatile {
    public static long timeStamp(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }

    /**
     * @title 2.3.1 关键字votaile与死循环
     *
     */
    static class CantDepartWhileDeadLoop{
        static class PrintService{
            private boolean print = true;
            public boolean isPrint() {
                return print;
            }
            public void setPrint(boolean print) {
                this.print = print;
            }
            public void print(){
                while (print){
                    System.out.println(threadName()+" 正在打印。。。");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }
        public static void main(String[] args) {
            PrintService printService = new PrintService();
            printService.print();
            System.out.println(threadName()+" 打算停掉这个打印");
            printService.setPrint(false);
            /**
             * 打印内容：一直在打印
             * main 正在打印。。。
             * main 正在打印。。。
             * 根本停不下来，且不会打印"打算停掉这个打印"这部分内容
             * 原因： main线程掉进while里出不来了
             */
        }
    }

    static class DepartDeadLoopInSeprateThread{
        static class PrintService implements Runnable{
            private /* volatile */ boolean print = true;
            public boolean isPrint() {
                return print;
            }
            public void setPrint(boolean print) {
                this.print = print;
            }
            @Override
            public void run() {
                while (print){
                    System.out.println(threadName()+" 正在打印。。。");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }
        public static void main(String[] args) throws InterruptedException {
            PrintService printService = new PrintService();
            new Thread(printService).start();
            Thread.sleep(4000);
            System.out.println(threadName()+" 计划停止这个打印");
            printService.setPrint(false);
            /**
             * 打印内容
             * Thread-0 正在打印。。。
             * Thread-0 正在打印。。。
             * Thread-0 正在打印。。。
             * Thread-0 正在打印。。。
             * main 计划停止这个打印
             *
             * 该案例在-server服务器模式中64bit的JVM上时，会出现死循环
             * 使用volatile可以强制线程从公共堆栈中取得变量的值，而不是从线程私有数据栈中取得变量的值
             */
        }
    }

    /**
     * 在JVM被设置为-server模式时为了线程运行的效率，线程一直在私有堆栈中取得print变量的值是true
     * 而代码.setPrint(false)更新的公共堆栈中的print变量
     * 所以上述案例在-server模式下仍然会死循环，此时就需要使用volatile关键字，强制线程从公共堆栈中取值
     * 使用volatile关键字增加了实例变量在多个线程之间的可见性；但volatile的缺陷是不支持原子性
     *
     *
     * 关键字synchronized与volatile比较：
     * 关键字volatile解决的是变量在多个线程之间的可见性，而synchronized关键字解决的是多个线程之间访问资源的同步性。
     *
     * 1. 关键字volatile是线程同步的轻量级实现，性能上优于synchronized
     * 2. volatile只能修饰变量，synchronized可以修饰方法、代码块，随着JDK的更新，synchronized关键字在执行效率上有很大提升，实际应用较多；
     * 3. 多线程访问volatile不会发生阻塞，但synchronized会出现阻塞；
     * 4. volatile能保证数据的可见性，但不能保证原子性？；而synchronized可以保证原子性，也可以间接保证可见性，它会将私有内存和公共内存中的数据做同步；
     *
     *
     * 线程安全包括原子性和可见性两个方面，java的同步机制都是围绕这两个方面确保线程安全的
     */

    /**
     * @title volatile 非原子特性
     * @desc volatile虽然增加了实例变量在多个线程间的可见性，但它不具备同步性，也就不具备原子性；
     */
    static class ShowVolatileNonAtomicity{
        static class CountThread extends Thread{
            public volatile static int count;
            private static void addCount(){
                for (int i = 0; i < 50; i++) {
                    count ++;
                }
                System.out.println("count = " + count);
            }
            @Override
            public void run(){
                addCount();
            }
        }
        static class CountThread2 extends Thread{
            public /*volatile*/ static int count; //使用了synchronized关键字后volatile可以省掉
            synchronized private static void addCount(){
                for (int i = 0; i < 50; i++) {
                    count++;
                }
                System.out.println("count = " + count);
            }
            @Override
            public void run() {
                addCount();
            }
        }
        public static void main(String[] args) {
            CountThread[] countThreads1 = new CountThread[20];
            CountThread2[] countThreads2 = new CountThread2[20];
            for (int i = 0; i < 20; i++) {
                countThreads1[i] = new CountThread();
                countThreads2[i] = new CountThread2();
            }
            for (int i = 0; i < 20; i++) {
                //countThreads1[i].start(); //A
                countThreads2[i].start();  //B
            }
            /**
             * 据说A处的代码会加不到1000，B处执行时因为有完整的同步锁，所以可以加到1000
             */
        }
    }

    /**
     * @title 2.3.5 使用原子类进行i++操作
     * @desc 上述volatile遇到的原子性问题可以使用原子类（AtomicXXX）来操作
     * 原子操作是不能分割的整体，没有其他线程能够中断或检查正在原子操作中的变量，可以在没有锁的情况下做到线程安全
     */
    static class AtomicIntegerTest{
        static class AddCountThread extends Thread{
            private AtomicInteger count = new AtomicInteger(0);
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("count = "+count.incrementAndGet());
                }
            }
        }
        public static void main(String[] args) {
            AddCountThread countThread = new AddCountThread();
            Thread t1 = new Thread(countThread);t1.start();
            Thread t2 = new Thread(countThread);t2.start();
        }
    }

    //演示Atomic不原子性的一面
    static class NotSafeAtomicClass{
        static class AtomicAddService {
            public static AtomicLong aiRef = new AtomicLong();
            /*synchronized*/ public void addNum(){ //S
                System.out.println(threadName() + "加了100之后的值是："+ aiRef.addAndGet(100));
                aiRef.addAndGet(1);
            }
        }
        static class MyThread extends Thread{
            private AtomicAddService addService;
            public MyThread(AtomicAddService addService){
                this.addService = addService;
            }
            @Override
            public void run() {
                addService.addNum();
            }
        }
        public static void main(String[] args) {
            AtomicAddService addService = new AtomicAddService();
            MyThread[] threads = new MyThread[5];
            for (int i = 0; i < 5; i++) {
                threads[i] = new MyThread(addService);
            }
            for (int i = 0; i < 5; i++) {
                threads[i].start();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("原子类操作结果："+AtomicAddService.aiRef.get());
            /**
             * 打印结果
             * Thread-1加了100之后的值是：100
             * Thread-0加了100之后的值是：300
             * Thread-4加了100之后的值是：500
             * Thread-2加了100之后的值是：200
             * Thread-3加了100之后的值是：400
             * 原子类操作结果：505
             *
             * 应当在每次加100后接着加1
             * Atomic类的方法内是原子的，但方法之间的调用不是原子的，解决上面的问题需要进行同步
             *
             * S行代码的synchronized关键字打开后打印结果
             * Thread-0加了100之后的值是：100
             * Thread-4加了100之后的值是：201
             * Thread-3加了100之后的值是：302
             * Thread-2加了100之后的值是：403
             * Thread-1加了100之后的值是：504
             * 原子类操作结果：505
             */
        }
    }

    /**
     * @title 2.3.6 synchronized关键字有volatile的同步功能
     */
    static class SyncAsVolatile{
        static class OperateService{
            private boolean run = true;
            String lock = "lock";
            public void runMethod(){
                while (run){
                    synchronized (lock){} //B
                }
                System.out.println("线程将停止");
            }
            public void stopMethod(){
                run = false;
            }
        }
        static class ThreadA extends Thread{
            private OperateService opService;
            public ThreadA(OperateService opService){
                this.opService = opService;
            }
            @Override
            public void run() {
                opService.runMethod();
            }
        }
        static class ThreadB extends Thread{
            private OperateService opService;
            public ThreadB(OperateService opService){
                this.opService = opService;
            }
            @Override
            public void run() {
                opService.stopMethod();
            }
        }
        public static void main(String[] args) throws InterruptedException {
            OperateService opService = new OperateService();
            ThreadA threadA = new ThreadA(opService);threadA.start();
            Thread.sleep(1000);
            ThreadB threadB = new ThreadB(opService);threadB.start();
            System.out.println("线程B已发送停止信号");
            /**
             * 打印：
             * 线程B已发送停止信号
             *
             * 添加B处的代码的打印结果：
             * 线程B已发送停止信号
             * 线程将停止
             *
             * 解释：
             * synchronized关键字也有volatile关键字那样的同步效果：让
             */
        }
    }
}
