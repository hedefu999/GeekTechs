package com.concurrency.miscellaneous;

import java.io.Writer;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class AbountCyclicBarrier {
    static class Writer extends Thread {
        private int timeComsuneCount;
        private CyclicBarrier cyclicBarrier;

        public Writer(CyclicBarrier cyclicBarrier,int timeComsuneCount) {
            this.cyclicBarrier =cyclicBarrier;
            this.timeComsuneCount = timeComsuneCount;
        }

        @Override
        public void run() {
            try{
                TimeUnit.SECONDS.sleep(timeComsuneCount);//以睡眠来模拟线程需要预定写入数据操作
                System.out.println(" 线 程 " + Thread.currentThread().getName() + " 写 入 数 据 完 毕，等待其他线程写入完毕");
                cyclicBarrier.await();
            } catch(Exception e){
                e.printStackTrace();
            }
            System.out.println("所有线程写入完毕，继续处理其他任务，比如数据操作");
        }

    }
    public static void main(String[] args) {
        int N = 4;
        CyclicBarrier barrier = new CyclicBarrier(N);
        for (int i = 0; i < N; i++){
            new Writer(barrier,i).start();
        }
        System.out.println("main线程");
    }


}