package com.concurrency.miscellaneous;

public class AboutThreadCommunication {
/**
 线程间通信可采用共享内存的方式，共享内存关注 可见性 有序性 原子性
 实现方案有：
 1.将数据抽象成一个类，并将数据的操作作为这个类的方法
 2.将Runnable对象作为一个类的内部类
 3.ThreadLocal
  */
    //方案1
    static class MyData {
        private int j = 0;

        public synchronized void add() {
            j++;
            System.out.println("线程" + Thread.currentThread().getName() + "j 为:" + j);
        }

        public synchronized void dec() {
            j--;
            System.out.println("线程" + Thread.currentThread().getName() + "j 为:" + j);
        }

        public int getData() {
            return j;
        }
    }

    static class AddRunnable implements Runnable {
        MyData data;
        public AddRunnable(MyData data){
            this.data= data;

        }
        public void run() {
            data.add();
        }
    }
    static class DecRunnable implements Runnable {
        MyData data;
        public DecRunnable(MyData data){
            this.data = data;
        }
        public void run() {
            data.dec();
        }
    }

    public static void main(String[] args)throws Exception {
        MyData data = new MyData();
        Runnable add = new AddRunnable(data);
        Runnable dec = new DecRunnable(data);
        for(int i=0;i<2;i++){
            new Thread(add).start();
            new Thread(dec).start();
        }
    }

    /*
    方案2 将 Runnable 对象作为一个类的内部类，共享数据作为这个类的成员变量，每个线程对共享数
据的操作方法也封装在外部类，以便实现对数据的各个操作的同步和互斥，作为内部类的各
个 Runnable 对象调用外部类的这些方法。
    */
    static class MyData2 {
        private int j=0;
        public synchronized void add(){
            j++;
            System.out.println("线程"+Thread.currentThread().getName()+"j 为:"+j);
        }
        public synchronized void dec() {
            j--;
            System.out.println("线程" + Thread.currentThread().getName() + "j 为:" + j);
        }
        public int getData() {
            return j;
        }

        public static void main(String[] args) {
            final MyData2 data = new MyData2();
            for(int i=0;i<2;i++){
                new Thread(new Runnable(){
                    public void run() {
                        data.add();
                    }
                }).start();
                new Thread(new Runnable(){
                    public void run() {
                        data.dec();
                    }
                }).start();
            }
        }
    }










}
