package com.concurrency.javadxcbchxjs;

public class _3ThreadUnsafeCases {
    static class ExtendsThread extends Thread{
        private int i = 5;
        @Override
        public void run() {
            System.out.println("ThreadName = "+Thread.currentThread().getName()+", i = "+--i);
        }
    }

    public static void main(String[] args) {
        ExtendsThread thread = new ExtendsThread();
        Thread t1 = new Thread(thread);
        Thread t2 = new Thread(thread);
        Thread t3 = new Thread(thread);
        Thread t4 = new Thread(thread);
        Thread t5 = new Thread(thread);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
    /**
     * 上述写法存在概率性线程安全问题
     * ThreadName = Thread-2, i = 4
     * ThreadName = Thread-5, i = 1
     * ThreadName = Thread-4, i = 2
     * ThreadName = Thread-1, i = 3
     * ThreadName = Thread-3, i = 3
     * 虽然println方法的源码加了锁，但 --i 是在println方法之外执行的操作，仍然会存在线程安全问题
     * 所以仍然需要对run方法加锁
     */
}
