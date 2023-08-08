package com.misc.mqdemo;

import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue延时队列有一个延时获取元素的功能，可以拿来做消息队列
 */
public class CustomerDelayQueue {
    //延迟消息队列
    private static DelayQueue delayQueue = new DelayQueue();
    //生产者
    public static void producer(){
        delayQueue.put(new MyDelay(2000,"你好，世界"));
        delayQueue.put(new MyDelay(0,"开天辟地"));
    }
    //消费者
    public static void consumer() throws InterruptedException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String format = dateFormat.format(new Date());
        System.out.println("开始时间："+format);
        while (!delayQueue.isEmpty()){
            Delayed take = delayQueue.take();
            System.out.println(take.toString());
        }
        System.out.println("结束时间：" + System.currentTimeMillis());
    }

    /**
     * 自定义延迟队列
     */
    static class MyDelay implements Delayed{
        //延迟截止时间
        long delayTime = System.currentTimeMillis();
        @Getter
        @Setter
        private String message;
        //构造函数初始化 设置延迟执行时间
        public MyDelay(long delayTime, String message){
            this.delayTime = (this.delayTime + delayTime);
            this.message = message;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long convert = unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            return convert;
        }
        //队列里元素的排序依据
        @Override
        public int compareTo(Delayed o) {
            if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)){
                return 1;
            }else if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)){
                return -1;
            }else {
                return 0;
            }
        }

        @Override
        public String toString() {
            return this.message;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        producer();
        consumer();
    }

    /**
     * DelayQueue如何使用
     */
    static class AboutDelayQueue{
        public static void main(String[] args) throws InterruptedException, ExecutionException {
            DelayQueue queue = new DelayQueue();
            long startTime = System.currentTimeMillis();
            queue.put(new Delayed() {
                @Override
                public String toString() {
                    return "test";
                }
                @Override
                public long getDelay(TimeUnit unit) {
                    long convert = unit.convert(startTime + 6000 - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    return convert;
                    //直接return 2000 以为能2秒后take到元素的还是新手
                }
                @Override
                public int compareTo(Delayed o) {
                    return 1;
                }
            });
            // queue.put(new MyDelay(2000,"你好，世界"));
            System.out.println("start taking"+System.currentTimeMillis());
            Callable<String> callable = new Callable<String>() {
                @Override
                public String call() {
                    Delayed take = null;
                    try {
                        take = queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("end taking" + System.currentTimeMillis());
                    System.out.println(take);
                    return "success" + take;
                }
            };
            FutureTask<String> futureTask = new FutureTask<>(callable);
            // test(futureTask);
            test2(futureTask);
        }

        private static void test2(FutureTask<String> futureTask) throws InterruptedException {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    futureTask.run();
                }
            };
            thread.start();
            Thread.sleep(2000);
            thread.interrupt();//take方法可以响应中断
            try {
                futureTask.get();
            } catch (ExecutionException e) {
                System.out.println("拿到独立线程里的异常信息："+e.getMessage());//2秒后就得到异常信息
            }
        }

        private static void test(FutureTask<String> futureTask) throws ExecutionException, InterruptedException {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            try {
                executorService.submit(futureTask);
                String result = futureTask.get();
                System.out.println(result);
                // futureTask.cancel(false);
            }finally {
                executorService.shutdown();
            }
        }
    }
}
