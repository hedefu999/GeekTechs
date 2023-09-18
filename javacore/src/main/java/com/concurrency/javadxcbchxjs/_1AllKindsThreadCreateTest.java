package com.concurrency.javadxcbchxjs;

import org.junit.Test;

import java.util.concurrent.*;

public class _1AllKindsThreadCreateTest {
    static class ExtendsThread extends Thread{
        private int ticket=5;
        @Override
        public void run(){
            for(int i=0;i<10;i++){
                if(this.ticket>0){
                    System.out.println(this.getName()+" 卖票：ticket"+this.ticket--);
                }
            }
        }
    }
    static class RunableThread implements Runnable{
        private int ticket=5;
        @Override
        public void run(){
            for(int i=0;i<10;i++){
                if(this.ticket>0){
                    System.out.println(Thread.currentThread().getName()+" 卖票：ticket"+this.ticket--);
                }
            }
        }
    }

    @Test
    public void testExtends(){
        new ExtendsThread().start();
        new ExtendsThread().start();
    }

    /** 两个独立的线程单独卖票，享有独立的数据
     * Thread-0 卖票：ticket5
     * Thread-1 卖票：ticket5
     * Thread-1 卖票：ticket4
     * Thread-1 卖票：ticket3
     * Thread-1 卖票：ticket2
     * Thread-0 卖票：ticket4
     * Thread-1 卖票：ticket1
     * Thread-0 卖票：ticket3
     * Thread-0 卖票：ticket2
     * Thread-0 卖票：ticket1
     */
    //extends方式在java单继承的情况下存在劣势
    @Test
    public void testRunnable(){
        RunableThread runableThread = new RunableThread();
        new Thread(runableThread).start();
        new Thread(runableThread).start();
    }
    /** 多个线程共享一个Runable对象
     * Thread-0 卖票：ticket5
     * Thread-0 卖票：ticket3
     * Thread-0 卖票：ticket2
     * Thread-0 卖票：ticket1
     * Thread-1 卖票：ticket4
     */
    /**
     * 实现Runable接口相比Extends Thread的优点：
     * 避免单继承的局限
     * 代码共享，代码与数据独立
     * 适合多个相同程序代码的线程区处理同一资源的情况
     */

    //Runable是执行工作的独立任务，不会返回任何值
    //在Java SE5中引入的Callable是一种具有类型参数的泛型
    static class CallableThread implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程名："+Thread.currentThread().getName());
            int i = 0;
            for (; i<5;i++){
                System.out.println("循环内容："+i);
            }
            Thread.sleep(3000);
            return i;
        }
    }
    @Test
    public void testCallableThread(){
        CallableThread callableThread = new CallableThread();
        FutureTask<Integer> futureTask = new FutureTask<>(callableThread);
        //FutureTask<String> futureTask1 = new FutureTask<>(() -> "");
        new Thread(futureTask,"有返回值的线程").start();
        try {
            Integer integer = futureTask.get();
            System.out.println(integer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前线程名：有返回值的线程
     * 循环内容：0
     * 循环内容：1
     * 循环内容：2
     * 循环内容：3
     * 循环内容：4
     * 5
     */
    /**
     * 实现Runable 与 Callable的区别
     * Runable since java1.1 Callable since java1.5
     * callable 可以返回值
     * call方法可以抛出异常，run不可以
     * 运行Callable可以获得一个Future对象，可以获取异步执行结果信息，或者停止异步执行
     * 加入线程池运行时，Runnable使用ExecutorService的execute方法，Callable使用submit方法
     */

    static class RunableThread2 implements Runnable{
        private int ticket=15;
        @Override
        /*synchronized */public void run(){
            for(int i=0;i<10;i++){
                if(this.ticket>0){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+" 卖票：ticket"+this.ticket--);
                }
            }
        }
    }

    @Test
    public void testRunnableThread2InJunit(){
        RunableThread2 runableThread = new RunableThread2();
        new Thread(runableThread).start();
        new Thread(runableThread).start();
        new Thread(runableThread).start();
    }
    /**
     * 没有任何输出：不要使用junit测试多线程
     */
    static void testRunnableThread2(){
        RunableThread2 runableThread = new RunableThread2();
        new Thread(runableThread).start();
        new Thread(runableThread).start();
        new Thread(runableThread).start();
    }
    /**
     * 出现超卖的情况 Thread-2 卖票：ticket-1
     * 这是多个线程共享数据带来的线程安全问题
     * 在run方法前加synchronized即可解决此问题
     */

    //在线程池里，Runable使用execute提交，Callable使用submit提交
    static void runableCallableThreadPool(){
        //Runable提交到线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        RunableThread runableThread = new RunableThread();
        executorService.execute(runableThread);
        //executorService.shutdown();

        //Callable提交到线程池
        CallableThread intCallableThread = new CallableThread();
        Future<Integer> submit = executorService.submit(intCallableThread);
        //executorService.shutdown();
        try {
            System.out.println(submit.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //使用FutureTask
        Callable<Integer> callable = new CallableThread();
        FutureTask<Integer> futureTask = new FutureTask(callable);
        executorService.submit(futureTask);
        //executorService.shutdown();
        try {
            Integer integer = futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        testRunnableThread2();
    }






}
