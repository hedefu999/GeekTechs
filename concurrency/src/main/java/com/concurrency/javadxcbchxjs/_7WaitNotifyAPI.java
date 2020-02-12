package com.concurrency.javadxcbchxjs;

import java.util.ArrayList;
import java.util.List;

/**
 * @title 3.1 等待/通知机制
 */
public class _7WaitNotifyAPI {
    public static long timeStamp(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }
    static class CollectService{
        private List<String> list = new ArrayList<>();
        public void add(String item){
            list.add(item);
        }
        /*synchronized*/ public int size(){//A
            return list.size();
        }
    }
    //3.1.1 不适用等待/通知机制实现线程间通信
    static class LinkWithNonWaitNotify{
        static class ThreadA extends Thread{
            private CollectService cService;
            public ThreadA(CollectService cService,String threadName){
                this.cService = cService;
                setName(threadName);
            }
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    cService.add("name "+i);
                    System.out.println("添加了第"+(i+1)+"个元素");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { }
                }
            }
        }
        static class ThreadB extends Thread{
            private CollectService cService;
            public ThreadB(CollectService cService,String threadName){
                this.cService = cService;
                setName(threadName);
            }
            @Override
            public void run() {
                try {
                    while (true){
                        //System.out.println("列表大小："+cService.size()); B
                        if (cService.size() == 5){
                            System.out.println(" == 5, 线程B将退出");
                            throw new InterruptedException();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public static void main(String[] args) {
            CollectService cService = new CollectService();
            ThreadA a = new ThreadA(cService,"A");
            ThreadB b = new ThreadB(cService,"B");
            a.start();b.start();
        }
        /**
         * 线程B不断轮询list大小，==5时抛异常退出，线程A10个元素打完后退出
         * A处的代码不添加synchronized关键字B线程取不到size为5，会死循环
         * 发现添加B处的代码也能恢复正常。。。？？？
         */
    }

    /**
     * @title 3.1.3 wait-notify mechanism
     * @desc wait是线程停止运行，notify使停止的线程继续运行
     */
    static class LinkWithWaitNotify{
        static class AThread extends Thread{
            private Object lock;
            public AThread(Object lock){
                this.lock = lock;
            }
            @Override
            public void run() {
                synchronized (lock){
                    System.out.println("开始 wait time = "+timeStamp());
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {}
                    System.out.println("结束 wait time = "+timeStamp());
                }
            }
        }
        static class BThread extends Thread{
            private Object lock;
            public BThread(Object lock){
                this.lock = lock;
            }

            @Override
            public void run() {
                synchronized (lock){
                    System.out.println("开始 notify time = "+timeStamp());
                    //lock.notify();//B
                    System.out.println("结束 notify time = "+timeStamp());
                }
            }
        }
        public static void main(String[] args) throws Exception {
            Object lock = new Object();
            AThread aThread = new AThread(lock);
            aThread.start();
            Thread.sleep(3000);
            BThread bThread = new BThread(lock);
            bThread.start();
        }/**
         * 打印内容：
         * 开始 wait time = 1588
         * 开始 notify time = 4593
         * 结束 notify time = 4594
         * 结束 wait time = 4594
         *
         * B行代码被注释掉：
         * 开始 wait time = 5489
         * 开始 notify time = 8492
         * 结束 notify time = 8492
         * 线程无法退出
         */
    }

    //演示notify后线程如果并不运行完释放锁，wait线程即使收到通知也需要等待
    static class WaitNotifySize5{
        static class ListService{
            private static List<String> list = new ArrayList<>();
            public static void add(String item){
                list.add(item);
            }
            public static int size(){
                return list.size();
            }
        }
        static class AThread extends Thread{
            private Object lock;
            public AThread(Object lock){
                this.lock = lock;
            }
            @Override
            public void run() {
                synchronized (lock){
                    if (ListService.size() != 5){
                        System.out.println("wait begin "+timeStamp());
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {}
                        System.out.println("wait end "+timeStamp());
                    }
                }
            }
        }
        static class BThread extends Thread{
            private Object lock;
            public BThread(Object lock){
                this.lock = lock;
            }
            @Override
            public void run() {
                synchronized (lock){
                    for (int i = 0; i < 10; i++) {
                        ListService.add("item "+i);
                        if (ListService.size() == 5){
                            lock.notify();
                            System.out.println("线程已发出通知！");
                        }
                        System.out.println("添加了 "+(i+1)+" 个元素！");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {}
                    }
                }
            }
        }
        public static void main(String[] args) throws InterruptedException {
            Object lock = new Object();
            AThread aThread = new AThread(lock);aThread.start();
            Thread.sleep(50);
            BThread bThread = new BThread(lock);bThread.start();
        }
    }

    //方法wait锁释放与notify锁不释放
    //sleep不会释放锁
    static class WaitReleaseLock{
        static class WaitService{
            public void waitMethod(Object lock){
                synchronized (lock){
                    System.out.println("begin wait");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {}
                    System.out.println("end wait");
                }
            }
        }
        static class AThread extends Thread{
            private Object lock;
            public AThread(Object lock){
                this.lock = lock;
            }
            @Override
            public void run() {
                WaitService waitService = new WaitService();
                waitService.waitMethod(lock);
            }
        }
        public static void main(String[] args) {
            Object lock = new Object();
            AThread aThread = new AThread(lock);
            aThread.start();
            AThread bThread = new AThread(lock);
            bThread.start();
        }
        /**
         * begin wait
         * end wait
         * begin wait
         * end wait
         */
    }

    //处于wait状态的线程执行interrupt方法会抛出InterruptedException
    static class InterruptWhenWait{
        static class ThreadA extends Thread{
            private Object lock = null;
            public ThreadA(Object lock, String name){
                this.lock = lock;
                setName(name);
            }
            @Override
            public void run() {
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        public static void main(String[] args) {
            Object lock = new Object();
            ThreadA threadA = new ThreadA(lock,"A");
            threadA.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            //threadA.interrupt(); //这一行会导致抛出InterruptedException
        }
    }

    /**
     * 3.1.8 方法wait(long)的使用
     * 方法wait(long time)的功能是等待某一时间内是否有线程对锁进行唤醒，如果超过这个时间就自动唤醒。
     */
    static class TwoThreadsSequential{
        private String lock = new String("lock");
        private boolean hasRunB = false;
        private Runnable runnableA = new Runnable() {
            @Override
            public void run() {
                synchronized (lock){
                    while (!hasRunB){
                        System.out.println("A线程需要等待B先跑完");
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("A线程运行完毕");
                    }
                }
            }
        };
        private Runnable runnableB = new Runnable() {
            @Override
            public void run() {
                synchronized (lock){
                    hasRunB = true;
                    System.out.println("B线程运行即将完成");
                    lock.notify();
                    System.out.println("B线程结束前发出信号");
                }
            }
        };
        public static void main(String[] args) throws InterruptedException {
            TwoThreadsSequential wrapper = new TwoThreadsSequential();
            Thread threadA = new Thread(wrapper.runnableA);
            Thread threadB = new Thread(wrapper.runnableB);
            //A B 谁先运行顺序不可颠倒
            threadA.start();
            Thread.sleep(1000);
            threadB.start();
        }
        /**
         * 打印内容：
         * A线程需要等待B先跑完
         * B线程运行即将完成
         * B线程结束前发出信号
         * A线程运行完毕
         *
         * 颠倒AB运行顺序后：
         * B线程运行即将完成
         * B线程结束前发出信号
         */
    }

    //[有趣案例]
    //3.1.10 wait等待的条件发生变化，造成程序逻辑混乱
    static class WaitDirtyRead{
        private static List<String> list = new ArrayList<>();
        private static String strLock = new String("lock");
        static class AddThread extends Thread{
            private String lock;
            public AddThread(String lock,String name){
                this.lock = lock;
                setName(name);
            }
            @Override
            public void run() {
                synchronized (lock){
                    list.add(timeStamp()+"");
                    lock.notifyAll();
                }
            }
        }
        static class SubThread extends Thread{
            private String lock;
            public SubThread(String lock,String name){
                this.lock = lock;
                setName(name);
            }
            @Override
            public void run() {
                synchronized (lock){
                    if (list.size() == 0){ //A
                        System.out.println(threadName()+" 线程开始调wait");
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {}
                        System.out.println(threadName()+" 线程从wait中恢复");
                    }
                    list.remove(0);
                    System.out.println("list`s size = "+list.size());
                }
            }
        }
        public static void main(String[] args) throws InterruptedException {
            AddThread addThread = new AddThread(strLock,"Thread-A");
            SubThread subThread = new SubThread(strLock,"Thread-B");
            SubThread subThread2 = new SubThread(strLock,"Thread-B2");
            subThread.start();
            subThread2.start();
            Thread.sleep(1000);
            addThread.start();
            /**
             * Add线程只加了一个元素有两个线程被唤醒做数据删除，一共两次，会抛出IndexOutOfBoundsException
             *
             * 将A处的代码从 if 改为 while，此时就不再抛出异常：
             * Thread-B 线程开始调wait
             * Thread-B2 线程开始调wait
             * Thread-B2 线程从wait中恢复
             * list`s size = 0
             * Thread-B 线程从wait中恢复
             * Thread-B 线程开始调wait
             */
        }
    }

    /**
     * [经典案例]
     * @title 3.1.11 生产者/消费者模式
     */

    //1. 一个生产者对一个消费者
    static class OneProducerOneConsumer{
        private static String lock = "monitor";
        private static List<String> foods = new ArrayList<>();
        static class Producer{
            private String lock;
            public Producer(String lock){
                this.lock = lock;
            }
            public void produce(){
                synchronized (lock){
                    if (foods.size()>0){
                        System.out.println(threadName()+"休息了");
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {}
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    System.out.println(threadName()+"生产一张煎饼"+ timeStamp());
                    foods.add("BREAD");
                    lock.notifyAll();
                }
            }
        }
        static class Consumer{
            private String lock;
            public Consumer(String lock){
                this.lock = lock;
            }
            public void consume(){
                synchronized (lock){
                    if (foods.size() == 0){
                        System.out.println(threadName()+"休息了");
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {}
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    System.out.println(threadName()+"消耗了一个手抓饼"+ timeStamp());
                    foods.remove(0);
                    lock.notifyAll();
                }
            }
        }
        static class PThread extends Thread{
            private Producer producer;
            public PThread(Producer p,String name){this.producer = p;setName(name);}
            @Override
            public void run() {
                while (true){producer.produce();}
            }
        }
        static class CThread extends Thread{
            private Consumer consumer;
            public CThread(Consumer c,String name){this.consumer = c;setName(name);}
            @Override
            public void run() {
                while (true){consumer.consume();}
            }
        }
        public static void main1(String[] args) {
            Producer producer = new Producer(lock);
            PThread pThread = new PThread(producer,"producer");
            Consumer consumer = new Consumer(lock);
            CThread cThread = new CThread(consumer,"consumer");
            pThread.start();cThread.start();
        }/**
         * 生产者生产一个炊饼7324
         * 消费者消耗一个食品8327
         * 生产者生产一个炊饼9333
         * 消费者消耗一个食品338
         * 生产者生产一个炊饼1343
         * 消费者消耗一个食品2345
         */
        public static void main(String[] args) throws InterruptedException {
            Producer producer = new Producer(lock);
            Consumer consumer = new Consumer(lock);
            PThread[] pThreads = new PThread[2];
            CThread[] cThreads = new CThread[2];
            for (int i = 0; i < 2; i++) {
                pThreads[i] = new PThread(producer,"producer"+i);
                cThreads[i] = new CThread(consumer,"consumer"+i);
            }
            for (int i = 0; i < 2; i++) {
                pThreads[i].start();cThreads[i].start();
            }
            Thread.sleep(5000);
            Thread[] threadArray = new Thread[Thread.currentThread().getThreadGroup().activeCount()];
            Thread.currentThread().getThreadGroup().enumerate(threadArray);
            for (int i = 0; i < threadArray.length; i++) {
                System.out.println(threadArray[i].getName()+""+threadArray[i].getState());
            }
        }
        /**
         * 上面的notifyAll()改成notify()后
         * producer和consumer会各有一半假死一半运行，很奇怪。。。
         */
    }

    //2. 多生产者与多消费者：操作栈
    static class StackByWaitNotify{
        static class Stack{
            private List<String> list = new ArrayList<>();
            synchronized public void push(){
                if (list.size() > 0){ //此if判断一次后到被notify时可能>0仍然成立，会导致list size超过1
                    try {
                        this.wait();
                    } catch (InterruptedException e) {}
                }
                list.add("item"+timeStamp());
                this.notifyAll();
                System.out.println(threadName()+" push, size = "+list.size());
            }
            synchronized public String pop(){
                while (list.size() == 0){//这里使用if会导致某些consumer抛异常导致退出，会发现consumer越来越少
                    try {
                        this.wait();
                    } catch (InterruptedException e) {}
                }
                String item = list.remove(0);
                this.notifyAll();
                System.out.println(threadName()+" pop, size = "+list.size());
                return item;
            }
        }

        static class Producer extends Thread{
            private Stack stack;
            public Producer(Stack stack,String name){
                this.stack = stack;
                setName(name);
            }
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    stack.push();}
            }
        }
        static class Consumer extends Thread{
            private Stack stack;
            public Consumer(Stack stack,String name){
                this.stack = stack;
                setName(name);
            }
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    stack.pop();
                }
            }
        }
        public static void main(String[] args) {
            Stack stack = new Stack();
            Producer[] producers = new Producer[4];
            Consumer[] consumers = new Consumer[4];
            for (int i = 0; i < 4; i++) {
                producers[i] = new Producer(stack, "produer-"+i);
                consumers[i] = new Consumer(stack, "consumer-"+i);
            }
            for (int i = 0; i < 4; i++) {
                producers[i].start();consumers[i].start();
            }
        }
    }

}
