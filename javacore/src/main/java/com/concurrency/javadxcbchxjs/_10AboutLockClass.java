package com.concurrency.javadxcbchxjs;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * @title 第4章 Lock的使用
 * @desc java5 新增的Lock类也能用于实现同步效果
 */
public class _10AboutLockClass {
    public static long timeStamp(){
        return System.currentTimeMillis()%10000;
    }
    public static String threadName(){
        return Thread.currentThread().getName();
    }
    private static void timeCosumedOperation(){
        long start = System.currentTimeMillis();
        String baidu = "https://www.guancha.cn/";
        try {
            URL url = new URL(baidu);
            URLConnection urlConn = url.openConnection();
            InputStream stream = urlConn.getInputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = stream.read(bytes)) != -1){
                String str = new String(bytes, 0, len);
                if (str.contains("中方")){
                    System.out.println(stream.available());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end - start));
    }

    static class SimepleReentrantLockTest{
        static class LockService{
            private Lock lock = new ReentrantLock();
            public void test(){
                lock.lock();
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {}
                System.out.println(threadName()+" running.");
                lock.unlock();
            }
        }
        public static void main(String[] args) {
            LockService lockService = new LockService();
            new Thread(){
                @Override
                public void run() {
                    lockService.test();
                }
            }.start();
            new Thread(){
                @Override
                public void run() {
                    lockService.test();
                }
            }.start();
        }
    }

    //演示"可重入"这一概念。可重入锁即递归锁
    static class ReEntrantLock{
        //todo
    }

    //Lock new 出来多个Condition被不同线程使用，可以分别唤醒线程
    static class SimpleConditionTest{
        static class Operation{
            private Lock lock = new ReentrantLock();
            private Condition condition = lock.newCondition();
            public void await() {
                try {
                    lock.lock();
                    System.out.println("before await");
                    condition.await(); //调condition的await必须拿到锁，不然IllegalMonitorException
                    System.out.println("after singnal");//要signal()后才能执行到这一行
                } catch (InterruptedException e) {
                } finally {
                    System.out.println("释放锁");
                    lock.unlock();
                }
            }
            public void signal(){
                try {
                    lock.lock();
                    condition.signalAll();//同样，执行signal也要拿到锁
                }finally {
                    lock.unlock();
                }
            }
        }
        static class AThread extends Thread{
            private Operation operation;
            public AThread(Operation operation){
                this.operation = operation;
            }
            @Override
            public void run() {
                operation.await();
            }
        }
        public static void main(String[] args) throws InterruptedException {
            Operation operation = new Operation();
            AThread aThread = new AThread(operation);
            aThread.start();
            Thread.sleep(2000);
            operation.signal();
        }
    }
    //生产者、消费者交替打印
    static class CrossPrint{
        static class PrintService{
            private ReentrantLock lock = new ReentrantLock();
            private Condition condition = lock.newCondition();
            private boolean print = true;
            public void printA(){
                try {
                    lock.lock();
                    while (print){
                        condition.await();
                    }
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("A A A");
                    print = true;
                    condition.signalAll();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
            public void printB(){
                try {
                    lock.lock();
                    while (!print){
                        condition.await();
                    }
                    Thread.sleep(1000);
                    System.out.println("B B B");
                    print = false;
                    condition.signalAll();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
        }
        public static void main(String[] args) {
            PrintService service = new PrintService();
            new Thread(){
                @Override
                public void run() {
                    while (true){
                        service.printA();
                    }
                }
            }.start();
            new Thread(){
                @Override
                public void run() {
                    while (true){
                        service.printB();
                    }
                }
            }.start();
        }
    }

    static class CrossPrint2{
        static ReentrantLock lock = new ReentrantLock();
        static Condition conditionA = lock.newCondition();
        static Condition conditionB = lock.newCondition();
        static class AThread extends Thread{
            @Override
            public void run() {
                try {
                    while (true){
                        lock.lock();
                        System.out.println("A A A");
                        Thread.sleep(1000);
                        conditionB.signalAll();
                        //lock.unlock(); 放到finally里就可以
                        conditionA.await();
                    }
                }catch (Exception e){}finally {
                    lock.unlock();
                }

            }
        }
        static class BThread extends Thread{
            @Override
            public void run() {
                try {
                    while (true){
                        lock.lock();
                        System.out.println("B B B");
                        TimeUnit.SECONDS.sleep(1);
                        conditionA.signalAll();
                        conditionB.await();
                    }
                }catch (Exception e){}finally {
                    lock.unlock();
                }
            }
        }
        public static void main(String[] args) {
            AThread aThread = new AThread();
            BThread bThread = new BThread();
            aThread.start();bThread.start();
        }
    }

    static class FairAndNonFairLock{
        //不公平锁打印结果居然也比较有序
        static Lock lock = new ReentrantLock(false);
        static Condition condition = lock.newCondition();
        static class AThread extends Thread{
            @Override
            public void run() {
                try {
                    while (true){
                        lock.lock();
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(threadName());
                        condition.signalAll();
                        condition.await();
                    }
                }catch (Exception e){}finally {
                    lock.unlock();
                }
            }
        }
        public static void main(String[] args) throws InterruptedException {
            Thread[] threads = new AThread[10];
            for (int i = 0; i < 10; i++) {
                threads[i] = new AThread();
            }
            for (int i = 0; i < 10; i++) {
                //TimeUnit.SECONDS.sleep(1);
                threads[i].start();
            }
        }
    }

    /**
     * @title ReentrantLock的一些方法的含义
     * getHoldCount：线程连续调用lock()的次数
     * getQueueLength：线程排队拿lock锁的个数
     * getWaitQueueLength：处于await状态等通知的线程的数量
     */
    static class GetHoldCountUsage{
        static class Operation{
            private ReentrantLock lock = new ReentrantLock();
            public void method1(){
                lock.lock();
                System.out.println("holdCount1 = "+lock.getHoldCount());
                method2();
                lock.unlock();
            }
            public void method2(){
                lock.lock();
                System.out.println("holdCount2 = "+lock.getHoldCount());
                lock.unlock();
            }
        }
        public static void main(String[] args) {
            Operation operation = new Operation();
            //直接用main线程操作
            operation.method1();
            operation.method2();
        }
        /**
         * holdCount1 = 1
         * holdCount2 = 2
         * holdCount2 = 1
         */
    }

    static class GetQueueLengthUsage{
        static class Operation{
            public static ReentrantLock lock = new ReentrantLock();
            Condition condition = lock.newCondition();
            public void method1(){
                try {
                    lock.lock();
                    System.out.println(threadName());
                    Thread.sleep(10000);
                }catch (Exception e){}finally {
                    lock.unlock();
                }
            }
            public void wait2(){
                try {
                    lock.lock();
                    condition.await();
                    System.out.println(threadName()+"拿到锁");
                }catch (Exception e){}finally {
                    lock.unlock();
                }
            }
            public void notify2(){
                try {
                    lock.lock();
                    System.out.printf("有%d个线程正在等待拿到Condition锁",lock.getWaitQueueLength(condition));
                    condition.signalAll();
                }finally {
                    lock.unlock();
                }
            }
        }
        public static void main2(String[] args) throws InterruptedException {
            Operation operation = new Operation();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    operation.method1();
                }
            };
            Thread[] threads = new Thread[10];
            for (int i = 0; i < 10; i++) {
                threads[i] = new Thread(runnable);
            }
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }
            Thread.sleep(2000);
            System.out.printf("有%d线程在等待获取锁", Operation.lock.getQueueLength());//9
        }
        public static void main(String[] args) throws InterruptedException {
            Operation operation = new Operation();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    operation.wait2();
                }
            };
            Thread[] threads = new Thread[10];
            for (int i = 0; i < 10; i++) {
                threads[i] = new Thread(runnable);
            }
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }
            Thread.sleep(2000);
            operation.notify2();
            /**
             * 有10个线程正在等待拿到Condition锁Thread-0拿到锁
             * Thread-1拿到锁
             * Thread-2拿到锁
             * Thread-3拿到锁
             * Thread-4拿到锁
             * Thread-5拿到锁
             * Thread-6拿到锁
             * Thread-7拿到锁
             * Thread-8拿到锁
             * Thread-9拿到锁
             */
        }
    }

    /**
     * Lock的3个方法：
     * hasQueuedThread:查询指定线程是否正在等待此锁,是否正在占有此锁
     * hasQueuedThreads:是否有线程正在等这把锁
     * hasWaiters:是否有线程正在等锁的condition
     */
    static class HasQueuedThreadUsage{
        static ReentrantLock lock = new ReentrantLock();
        static Condition condition = lock.newCondition();
        public static void main(String[] args) throws InterruptedException {
            Thread firstThread = new Thread(){
                @Override
                public void run() {
                    try {
                        lock.lock();
                        Thread.sleep(Integer.MAX_VALUE);//占着锁不放
                    }catch (Exception e){}finally {
                        lock.unlock();
                    }
                }
            };firstThread.start();
            Thread.sleep(1000);
            Thread secondThread = new Thread(){
                @Override
                public void run() {
                    lock.lock();
                    condition.signalAll();
                    lock.unlock();
                }
            };secondThread.start();
            System.out.printf("firstThread在等这个lock吗：%s \n",
                    lock.hasQueuedThread(firstThread));//false
            System.out.println(lock.hasQueuedThread(secondThread));//true
            System.out.println(lock.hasQueuedThreads());//true
        }
    }
    static class HasWaitersUsage{
        static ReentrantLock lock = new ReentrantLock();
        static Condition condition = lock.newCondition();
        public static void main(String[] args) throws InterruptedException {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                        condition.await();
                    }catch (Exception e){}finally {
                        lock.unlock();
                    }
                }
            };
            for (int i = 0; i < 5; i++) {
                Thread thread = new Thread(runnable);
                thread.start();
            }
            Thread.sleep(1000);
            lock.lock();
            System.out.printf("是否有线程在等condition的signal：%s？有%d个线程等着condition的signal来唤醒\n",
                    lock.hasWaiters(condition),lock.getWaitQueueLength(condition));
            /**
             * 是否有线程在等condition的signal：true？有5个线程等着condition的signal来唤醒
             */
        }
    }
    /**
     * hasWaiters getWaitQueueLength 用于判断condition，在使用时必须拿到lock
     */
    /**
     * 属性、状态方法
     * isFair:lock是否是公平锁
     * isHeldByCurrentThread：lock是否被当前线程持有
     * isLocked：是否有线程占有此锁
     */
    static class OtherLockMethodUsage{
        static ReentrantLock lock = new ReentrantLock();
        public static void main(String[] args) {
            //默认情况下ReentrantLock使用的是非公平锁
            System.out.println(lock.isFair());//false
            System.out.println(lock.isHeldByCurrentThread());//false
            System.out.println(lock.isLocked());//false
            lock.lock();
            System.out.println(lock.isHeldByCurrentThread());//true
            System.out.println(lock.isLocked());//true
        }
    }
    /**
     * 操作方法
     * lockInterruptibly:如果线程调了这个方法，再传递中断标记,会抛出异常
     * tryLock:尝试获取锁，并返回是否获得到boolean
     * tryLock(long timeout, TimeUnit unit)：如果在给定timeout时间后锁没有被其他线程占有，并且自身没有被设置中断标志，就获得这把锁
     */
    static class LockInterruptiblyTest{
        static ReentrantLock lock = new ReentrantLock();

        public static void main(String[] args) {
            Runnable runnable = new Runnable(){
                @Override
                public void run() {
                    try {
                        System.out.println(threadName()+"进入");
                        timeCosumedOperation();
                        lock.lockInterruptibly();
                        System.out.println(threadName());
                    }catch (Exception e){e.printStackTrace();}finally {
                        if (lock.isHeldByCurrentThread()){
                            System.out.println("持有锁需要释放");
                            lock.unlock();
                        }
                    }
                }
            };
            Thread threadA = new Thread(runnable,"A");
            Thread threadB = new Thread(runnable,"B");

            //交换下面两行的顺序
            //threadB.interrupt();//线程未start做中断标记没有效果
            //threadA.start();threadB.start();
            /**
             * A进入
             * B进入
             * B
             * 持有锁需要释放
             * A
             * 持有锁需要释放
             */

            threadA.start();threadB.start();
            threadB.interrupt();//设置中断标记，这样threadB在lockInterruptibly()时会抛异常
            /**
             * A进入
             * B进入
             * 耗时：619
             * A
             * 持有锁需要释放
             * 耗时：627
             * java.lang.InterruptedException
             */
        }
    }

    static class TryLockTest{
        static ReentrantLock lock = new ReentrantLock();
        public static void main(String[] args) throws InterruptedException {
            new Thread(){
                @Override
                public void run() {
                    lock.lock();
                    try {
                        Thread.sleep(3000);//占着锁,调整这里的时间可以决定main形成能不能拿到锁
                    } catch (Exception e) {}finally {
                        if (lock.isHeldByCurrentThread()){
                            lock.unlock();
                        }
                    }
                }
            }.start();

            //下面两块代码分别测试tryLock()跟tryLock(timeout),不可共用

            //Thread.sleep(2000);
            //System.out.println(threadName()+"尝试获取锁："+lock.tryLock());
            //System.out.println(lock.isHeldByCurrentThread());//true

            Thread.currentThread().interrupt();//有这一行会直接抛出异常
            try {
                System.out.println(lock.tryLock(4,TimeUnit.SECONDS));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        /**
         * tryLock()会真正地去拿锁，并返回是否拿到的信息
         */
    }

    /**
     * Condition的方法：
     * awaitUninterruptibly:线程wait时调interrupt会抛出异常，awaitUninterruptibly就是为解决这一问题的
     * awaitUntil(Date deadline) wait直到某个时间点才进入就绪状态，但可以提前用signal唤醒，提前唤醒会返回false
     */
    static class AwaitUninterruptiblyUsage{
        static ReentrantLock lock = new ReentrantLock();
        static Condition condition = lock.newCondition();

        public static void main(String[] args) throws InterruptedException {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        lock.lock();
                        System.out.println("wait begin");
                        //condition.await();//await()方法需要捕获异常
                        condition.awaitUninterruptibly();
                        //condition.awaitUntil()
                        System.out.println("wait end");
                    }catch (Exception e){e.printStackTrace();}finally {
                        lock.unlock();
                    }
                }
            };thread.start();
            thread.interrupt();
        }
    }

    /**
     * @title 可重入读写锁ReentrantReadWriteLock
     * @desc 读锁非互斥，读写锁互斥，写写锁互斥，读锁非互斥的效果效率更高
     *
     */
    static class ReentrantReadWriteLockUsage{
        public static void main(String[] args) {
            ReadWriteLock lock = new ReentrantReadWriteLock();
            Runnable runnable = new Runnable(){
                @Override
                public void run() {
                    //拿到非互斥的读锁
                    //lock.readLock().lock();
                    //拿互斥的写锁
                    lock.writeLock().lock();
                    System.out.println(threadName()+" 获得读锁 at "+timeStamp());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { }
                    //lock.readLock().unlock();
                    lock.writeLock().unlock();
                }
            };
            Thread threadA = new Thread(runnable);
            Thread threadB = new Thread(runnable);
            threadA.start();
            threadB.start();
            /**
             * 使用读读锁：
             * Thread-0 获得读锁 at 7426
             * Thread-1 获得读锁 at 7426
             *
             * 使用写写锁：
             * Thread-0 获得读锁 at 2323
             * Thread-1 获得读锁 at 3326
             *
             * 不论是读写锁（一个线程先拿到读锁，另一个再拿写锁），还是写读锁都是互斥的
             */
        }
    }
}
