package com.concurrency.javadxcbchxjs;

/**
 * 比较run和start方法的不同
 */
public class _2startrunmethoddiff {
    /* 第一个例子 */
    static class ExtendsThread extends Thread{
        public ExtendsThread(String name){
            super(name);
        }
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" is running");
        }
    }

    static class RunableThread implements Runnable{
        @Override
        public void run() {

        }
    }

    public static void test1(){
        Thread mythread = new ExtendsThread("extends");
        System.out.println(Thread.currentThread().getName()+" call run()");
        mythread.run();//此时调用线程是主线程main而非mythread,run不会新建一个线程
        System.out.println(Thread.currentThread().getName()+" call start()");
        mythread.start();//源码中调用了start0() 新建了一个线程
    }
    /*
    main call run()
    main is running
    main call start()
    extends is running
    */

    /** 第二个例子 **/
    static void pong(){
        System.out.println("pong");
    }
    static void test2(){
        Thread t = new Thread(){
            @Override
            public void run() {
                pong();
            }
        };
        t.start();
        System.out.println("ping");
    }
    /*
    ping
    pong*/
    static void test3(){
        Thread t = new Thread(){
            @Override
            public void run() {
                pong();
            }
        };
        t.run();
        System.out.println("ping");
    }
    /*
    pong
    ping*/

    /**
     * start方法：使该线程开始执行，jvm调用该线程的run方法。效果是两个线程并发运行
     * 通过调用Thread类的start方法来启动一个线程，此时线程处于就绪状态，一旦得到cpu时间片，就开始执行run方法
     * run方法只是类的一个普通方法，直接调用run方法，程序中依然只有主线程这一个线程，还是要顺序执行，这样就没有达到写线程的目的
     *
     * 调用start方法才可以启动线程
     */

    /** 第三个例子 **/
    static class ExtendsThread2 extends Thread{
        public ExtendsThread2(){
            System.out.println("构造方法的调用线程： " + Thread.currentThread().getName());
        }

        @Override
        public void run() {
            System.out.println("run方法的打印：" + Thread.currentThread().getName());
        }
    }
    static void test4(){
        ExtendsThread2 thread2 = new ExtendsThread2();
        //thread2.start();
        /*
         构造方法的调用线程： main
         run方法的打印：Thread-0
        */
        thread2.run();
        /*
        构造方法的调用线程： main
        run方法的打印：main
        */
    }

    static class ExtendsThread3 extends Thread{
        public ExtendsThread3(){
            System.out.println("constructor: currentThread.getName() = "+Thread.currentThread().getName());
            System.out.println("constructor: this.getName() = "+this.getName());
        }
        @Override
        public void run() {
            System.out.println("run: currentThread.getName() = "+Thread.currentThread().getName());
            System.out.println("run: this.getName() = "+this.getName());
        }

        public static void main(String[] args) {
            ExtendsThread3 thread3 = new ExtendsThread3();
            Thread thread = new Thread(thread3,"AAA");
            thread.start();
        }
        /*
            constructor: currentThread.getName() = main
            constructor: this.getName() = Thread-0
            run: currentThread.getName() = AAA
            run: this.getName() = Thread-0 TODO getName是Thread-0而非AAA的原因？？？
        */
    }

    //线程的stop方法不安全，不建议使用，安全中止线程的自定义方法
    static class SafeStopThread implements Runnable{
        //定义线程终止的开关
        private volatile boolean stop = false;//此变量必须加上volatile
        int a = 0;
        @Override
        public void run() {
            while (!stop){
                synchronized (""){
                    a++;
                    try {
                        Thread.sleep(100);
                    }catch (Exception e){}
                    a--;
                    String tn = Thread.currentThread().getName();
                    System.out.println(tn + ": a = "+ a);
                }
            }
        }
        public void threadStop(){stop=true; }
    }
    static void testSafeStopThread(){
        SafeStopThread thread = new SafeStopThread();
        for (int i = 0; i < 5; i++) {
            new Thread(thread).start();
        }
        thread.threadStop();
    }

    public static void main(String[] args) {
        test4();
    }

}
