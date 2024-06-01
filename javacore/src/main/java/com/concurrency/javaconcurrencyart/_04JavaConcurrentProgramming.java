package com.concurrency.javaconcurrencyart;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class _04JavaConcurrentProgramming {
    static class Chapter04Utils{
        public static final void toSleep(long seconds) {
            try {
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 展示一个简单java应用涉及到的线程
     * 使用ThreadMXBean获取线程信息
     */
    static class ShowThreadInSimpleJavaApplication{
        public static void main(String[] args) {
            //获取java线程管理MXBean
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            //不需要获取同步的monitor和synchronizer信息，仅获取线程和线程的堆栈信息
            ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
            for(ThreadInfo threadInfo : threadInfos) {
                System.out.println("【"+threadInfo.getThreadId()+"】"+threadInfo.getThreadName());
            }
        }
        /* 打印内容：
         【5】Monitor Ctrl-Break
         【4】Signal Dispatcher 分发处理发送给JVM信号的线程
         【3】Finalizer 调用对象finalize方法的线程
         【2】Reference Handler 清除Reference的线程
         【1】main main线程，用户程序入口
         */
    }
    static class ThreadPriorityWorksOrNot{
        static class Job implements Runnable{
            private int priority;
            private long jobCount;

            public Job(Integer priority) {
                this.priority = priority;
            }

            @Override
            public void run() {
                while (notStart){
                    Thread.yield();
                }
                while (notEnd) {
                    //try {
                    //    Thread.sleep(2);
                    //} catch (InterruptedException e) {
                    //    e.printStackTrace();
                    //}
                    jobCount++;
                }
            }
        }
        private static volatile boolean notStart = true;
        private static volatile boolean notEnd = true;

        public static void main(String[] args) throws InterruptedException {
            List<Job> jobs = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                int priority  = i<5?Thread.MIN_PRIORITY:Thread.MAX_PRIORITY;
                Job job = new Job(priority);
                jobs.add(job);
                Thread thread = new Thread(job, "Thread:"+i);
                thread.setPriority(priority);
                thread.start();
            }
            notStart = false;
            TimeUnit.SECONDS.sleep(10);
            notEnd = false;
            for (Job job : jobs){
                System.out.println("Job Priority : " + job.priority + ", count:"+job.jobCount);
            }
        }
        /**
         分析打印结果：
         前5个线程的count计数大小与后5个基本一致，表明线程优先级没有生效
         说明 程序正确性不能依赖线程的优先级高低？
         ？？如果CPU资源紧张时，优先级会生效吗？？
         */
    }
    /**
     * 演示处于不同状态的线程
     * */
    static class ThreadStateShowcase{
        //线程不断进行睡眠
        static class TimeWaiting implements Runnable{
            @Override
            public void run() {
                while (true) {
                    Chapter04Utils.toSleep(100);
                }
            }
        }
        //线程在Waiting.class上等待
        static class Waiting implements Runnable{
            @Override
            public void run() {
                while (true) {
                    synchronized (Waiting.class) {
                        try {
                            Waiting.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //该线程在Blocked.class上加锁后不会释放
        static class Blocked implements Runnable{
            @Override
            public void run() {
                synchronized (Blocked.class){
                    while (true){
                        Chapter04Utils.toSleep(100);
                    }
                }
            }
        }
        public static void main(String[] args) {
            new Thread(new TimeWaiting(),"TimeWaitingThread").start();
            new Thread(new Waiting(), "WaitingThread").start();
            // 使用两个Blocked线程，一个获取锁成功，另一个被阻塞
            new Thread(new Blocked(), "BlockedThread-1").start();
            new Thread(new Blocked(), "BlockedThread-2").start();
            Lock lock = new ReentrantLock();
            lock.lock();
        }
    }

    static class DaemonTest{
        static class DaemonRunner implements Runnable {
            @Override
            public void run() {
                try {
                    Chapter04Utils.toSleep(10);
                } finally {
                    System.out.println("DaemonThread finally run.");
                }
            }
        }
        public static void main(String[] args) {
            Thread thread = new Thread(new DaemonRunner(), "DaemonRunner");
            thread.setDaemon(true);
            thread.start();
        }
        /**
         * mian线程在执行完退出后JVM中已没有非Daemon线程，Daemon线程DaemonRunner此时便会立即终止
         * 但DaemonRunner中的finally方法块不会执行，所以Daemon线程不能依靠finally块中的内容来确保执行关闭或清理资源
         */
    }

    static class ShowThreadInterruptAction{
        public static void main(String[] args) throws Exception {
            // sleepThread不停的尝试睡眠
            Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
            sleepThread.setDaemon(true);
            // busyThread不停的运行
            Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
            busyThread.setDaemon(true);
            sleepThread.start();
            busyThread.start();
            // 休眠5秒，让sleepThread和busyThread充分运行
            TimeUnit.SECONDS.sleep(3);
            sleepThread.interrupt();
            busyThread.interrupt();
            System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
            System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());
            // 防止sleepThread和busyThread立刻退出
            Chapter04Utils.toSleep(2);
        }
        static class SleepRunner implements Runnable {
            @Override
            public void run() {
                while (true) {
                    Chapter04Utils.toSleep(10);
                }
            }
        }
        static class BusyRunner implements Runnable {
            @Override
            public void run() {
                while (true) {
                }
            }
        }
        /*
         * SleepThread interrupted is false
         * BusyThread interrupted is true
         * java.lang.InterruptedException: sleep interrupted
         */
    }

    static class DeprecatedThreadMethod{
        public static void main(String[] args) throws Exception {
            DateFormat format = new SimpleDateFormat("HH:mm:ss");
            Thread printThread = new Thread(new Runner(), "PrintThread");
            printThread.setDaemon(true);
            printThread.start();
            TimeUnit.SECONDS.sleep(5);
            // 将PrintThread进行暂停，输出内容工作停止
            printThread.suspend();
            System.out.println("main suspend PrintThread at " + format.format(new Date()));
            TimeUnit.SECONDS.sleep(5);
            // 将PrintThread进行恢复，输出内容继续
            printThread.resume();
            System.out.println("main resume PrintThread at " + format.format(new Date()));
            TimeUnit.SECONDS.sleep(5);
            // 将PrintThread进行终止，输出内容停止
            printThread.stop();
            System.out.println("main stop PrintThread at " + format.format(new Date()));
            TimeUnit.SECONDS.sleep(3);
        }
        static class Runner implements Runnable {
            @Override
            public void run() {
                DateFormat format = new SimpleDateFormat("HH:mm:ss");
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " Run at " +
                            format.format(new Date()));
                    Chapter04Utils.toSleep(1);
                }
            }
        }
        /*
         * PrintThread Run at 16:10:31
         * PrintThread Run at 16:10:32
         * PrintThread Run at 16:10:33
         * PrintThread Run at 16:10:34
         * PrintThread Run at 16:10:35
         * main suspend PrintThread at 16:10:36
         * PrintThread Run at 16:10:41
         * main resume PrintThread at 16:10:41
         * PrintThread Run at 16:10:42
         * PrintThread Run at 16:10:43
         * PrintThread Run at 16:10:44
         * PrintThread Run at 16:10:45
         * main stop PrintThread at 16:10:46
         */
    }

    /**
     * 优雅的设计线程停止的方法 --
     * - 使用interrupt标志位和自定义的boolean标志位
     * - 其他方案 - 主动抛异常
     *
     * 这种通过标志位停止线程的方式使得线程在停止时有机会去清理资源
     */
    static class ShutdownThreadGracefully{
        static class Runner implements Runnable {
            private long i;
            private volatile boolean on = true;
            @Override
            public void run() {
                while (on && !Thread.currentThread().isInterrupted()){
                    i++;
                }
                System.out.println("Count i = " + i);
            }
            public void cancel() {
                on = false;
            }
        }
        public static void main(String[] args) throws InterruptedException {
            Runner one = new Runner();
            Thread countThread = new Thread(one, "CountThread");
            countThread.start();
            // 睡眠1秒，main线程对CountThread进行中断，使CountThread能够感知中断而结束
            TimeUnit.SECONDS.sleep(1);
            countThread.interrupt();
            Runner two = new Runner();
            countThread = new Thread(two, "CountThread");
            countThread.start();
            // 睡眠1秒，main线程对Runner two进行取消，使CountThread能够感知on为false而结束
            TimeUnit.SECONDS.sleep(1);
            two.cancel();
        }
    }

    /**
     * Java线程中的等待/通知机制的演示
     */
    static class WaitNotifyDemonstration{
        static boolean flag = true;
        static Object lock = new Object();
        static class Wait implements Runnable {
            public void run() {
                // 加锁，拥有lock的Monitor
                synchronized (lock) {
                    // 当条件不满足时，继续wait，同时释放了lock的锁
                    while (flag) {
                        try {
                            System.out.println(Thread.currentThread() + " flag is true. wait@ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                            lock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    // 条件满足时，完成工作
                    System.out.println(Thread.currentThread() + " flag is false. running@ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                }
            }
        }
        static class Notify implements Runnable {
            public void run() {
                // 加锁，拥有lock的Monitor
                synchronized (lock) {
                    // 获取lock的锁，然后进行通知，通知时不会释放lock的锁，
                    // 直到当前线程释放了lock后，WaitThread才能从wait方法中返回
                    System.out.println(Thread.currentThread() + " hold lock. notify @ " +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    lock.notifyAll();
                    flag = false;
                    Chapter04Utils.toSleep(5);
                }
                // 再次加锁
                synchronized (lock) {
                    System.out.println(Thread.currentThread() + " hold lock again. sleep@ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    Chapter04Utils.toSleep(5);
                }
            }
        }

        public static void main(String[] args) throws InterruptedException {
            Thread waitThread = new Thread(new Wait(), "WaitThread");
            waitThread.start();
            TimeUnit.SECONDS.sleep(1);
            Thread notifyThread = new Thread(new Notify(), "NotifyThread");
            notifyThread.start();
        }

        /* notify后wait的线程要拿到锁才能运行
         * Thread[WaitThread,5,main] flag is true. wait@ 18:02:02
         * Thread[NotifyThread,5,main] hold lock. notify @ 18:02:03
         * Thread[NotifyThread,5,main] hold lock again. sleep@ 18:02:08
         * Thread[WaitThread,5,main] flag is false. running@ 18:02:13
         */
    }

    /**
     * 使用管道输入/输出流演示java线程间的通信
     */
    static class PipedInThreads{
        public static void main(String[] args)throws Exception {
            PipedWriter out = new PipedWriter();
            PipedReader in = new PipedReader();
            // 将输出流和输入流进行连接，否则在使用时会抛出IOException
            out.connect(in);
            Thread printThread = new Thread(new PrintRunnable(in), "PrintThread");
            printThread.start();
            int receive = 0;
            try {
                while ((receive = System.in.read()) != -1) {
                    out.write(receive);
                }
            } finally {
                out.close();
            }
        }
        static class PrintRunnable implements Runnable{
            private PipedReader in;
            public PrintRunnable(PipedReader in) {
                this.in = in;
            }
            @Override
            public void run() {
                int receive = 0;
                try {
                    while ((receive = in.read()) != -1){
                        System.out.println((char) receive);
                    }
                }catch (Exception e){

                }
            }
        }
        /* 原样打印一遍
         * java
         * j
         * a
         * v
         * a
         */
    }

    /**
     * 使用join方法演示一个多米诺效应：线程一个接一个停止
     */
    static class ShowThreadJoinUsage{
        public static void main(String[] args) throws InterruptedException {
            Thread previous = Thread.currentThread();
            for (int i = 0; i < 10; i++) {
                // 每个线程拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回
                Thread thread = new Thread(new Domino(previous), String.valueOf(i));
                thread.start();
                previous = thread;
            }
            TimeUnit.SECONDS.sleep(5);
            System.out.println("main Thread terminate.");
        }
        static class Domino implements Runnable{
            private Thread thread;
            public Domino(Thread thread) {
                this.thread = thread;
            }
            @Override
            public void run() {
                try {
                    thread.join();
                    System.out.println(thread.getName()+" terminated.");
                }catch (Exception e){}
                System.out.println(Thread.currentThread().getName()+" terminated.");
            }
        }
        /*
         * main线程处理完线程初始化退出时产生雪崩式的线程停止
         */
    }

    /**
     * 不使用ExecutorService的API实现一个数据库连接线程池
     * 数据库连接池模拟，使用LinkedList存储连接
     * 待默写：手写数据库连接池
     */
    static class DatabaseConnectionPoolSimulation{
        /*
         * 拦截并伪装Connection的API，模拟数据库操作
         */
        static class ConnectionDriver{
            static class ConnectionHandler implements InvocationHandler{
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("commit")){
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                    return null;
                }
            }
            public static final Connection createConnection(){
                Object conn = Proxy.newProxyInstance(
                        ConnectionDriver.class.getClassLoader(),
                        new Class[]{Connection.class} ,
                        new ConnectionHandler());//第二个参数的选择？？ConnectionDriver.class.getInterfaces()
                return (Connection) conn;
            }
        }
        static class ConnectionPool{
            private LinkedList<Connection> pool = new LinkedList<>();
            //连接池的定义：通过构造函数初始化连接的最大上限，通过一个双向队列维护连接
            public ConnectionPool(int initialSize){
                if (initialSize > 0){
                    for (int i = 0; i < initialSize; i++) {
                        pool.add(ConnectionDriver.createConnection());
                    }
                }
            }
            public void releaseConnection(Connection connection){
                if (connection != null){
                    synchronized (pool){
                        //连接释放后需要进行通知，这样其他消费者能够感知到连接池中已经归还了一个连接
                        pool.addLast(connection);
                        pool.notifyAll();
                    }
                }
            }
            //带有等待超时的获取连接
            public Connection getConnection(long millis) throws InterruptedException {
                synchronized (pool){
                    if (millis <= 0){
                        while (pool.isEmpty()){
                            pool.wait();
                        }
                        return pool.removeFirst();
                    }else {
                        long future = System.currentTimeMillis()+millis;
                        long remaining = millis;
                        while (pool.isEmpty() && remaining > 0){
                            pool.wait(remaining);
                            remaining = future - System.currentTimeMillis();
                        }
                        Connection result = null;
                        //再给一次机会看看，连接池中是否有连接
                        if (!pool.isEmpty()){
                            result = pool.removeFirst();
                        }
                        return result;
                    }
                }
            }
        }
        static class ConnectionTest{
            //10个连接
            static ConnectionPool pool = new ConnectionPool(5);
            //CountDownLatch做倒计数用
            static CountDownLatch start = new CountDownLatch(1);
            static CountDownLatch end;
            /*
             * 测试方案：10（threadCount）个线程并发分别进行20（count）次循环取连接commit执行
             * 用new CountDownLatch(1)确保10个线程真的同时启动，即线程启动前latch.await,由main线程作为裁判员发令latch.countDown
             * main线程作为裁判员还会等待各线程执行完毕，即end.await, 各线程跑完后触发一下end.countDown
             *
             */
            public static void main(String[] args) throws InterruptedException {
                int threadCount = 10;
                end = new CountDownLatch(threadCount);
                int count = 20;
                AtomicInteger got = new AtomicInteger();
                AtomicInteger notGot = new AtomicInteger();
                for (int i = 0; i < threadCount; i++) {
                    Thread thread = new Thread(
                            new ConnectionRunner(count,got,notGot),
                            "ConnectionRunnerThread");
                    thread.start();
                }
                start.countDown();
                end.await();
                System.out.println("total invoke: "+ (threadCount * count));
                System.out.println("got connection:  " + got);
                System.out.println("not got connection " + notGot);
            }
            static class ConnectionRunner implements Runnable{
                int count;
                AtomicInteger got;
                AtomicInteger notGot;
                public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot){
                    this.count = count;
                    this.got = got;
                    this.notGot = notGot;
                }
                @Override
                public void run() {
                    try {
                        start.await();
                    } catch (Exception ex) { }
                    while (count > 0){
                        try {
                            Connection connection = pool.getConnection(1000);
                            if (connection != null){
                                try {
                                    connection.createStatement();
                                    connection.commit();
                                }finally {
                                    pool.releaseConnection(connection);
                                    got.incrementAndGet();
                                }
                            }else {
                                notGot.getAndIncrement();
                            }
                        }catch (Exception e){
                        }finally {
                            count --;
                        }
                    }
                    end.countDown();
                }
            }
            /*
             * 10个线程抢5个连接会有获取不到的(等待超时设置1秒)，10个抢9个 notGot = 0
             * total invoke: 200
             * got connection:  193
             * not got connection 7
             */
        }
    }

    /**
     * 简易线程池 - 通用线程池
     * 线程池的简单实现 - 任务执行线程池
     * 泛型的使用很讲究
     */
    interface ThreadPool<Job extends Runnable>{
        // 执行一个Job，这个Job需要实现Runnable
        void execute(Job job);
        // 关闭线程池
        void shutdown();
        // 增加工作者线程
        void addWorkers(int num);
        // 减少工作者线程
        void removeWorker(int num);
        // 得到正在等待执行的任务数量
        int getJobSize();
    }
    //implements ThreadPool<Job> 这个Job泛型声明不能去掉，否则认为实现类的Job与接口定义的Job不是一个类，方法execute会编译出错
    static class DefaultThreadPoolImpl<Job extends Runnable> implements ThreadPool<Job>{
        private static final int MAX_WORKER_NUMBERS = 10;
        private static final int DEFAULT_WORKER_NUMBERS = 5;
        private static final int MIN_WORKER_NUMBERS = 1;
        //工作者列表
        private final LinkedList<Job> jobs = new LinkedList<Job>();
        private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
        private int workerNum = DEFAULT_WORKER_NUMBERS;
        private AtomicLong threadNum = new AtomicLong();
        public DefaultThreadPoolImpl(){
            initializeWorkers(DEFAULT_WORKER_NUMBERS);
        }
        public DefaultThreadPoolImpl(int num){
            workerNum = (num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num) < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
            initializeWorkers(workerNum);
        }
        //创建num个Worker线程并启动
        private void initializeWorkers(int num){
            for (int i = 0; i < num; i++) {
                Worker worker = new Worker();
                workers.add(worker);
                Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
                thread.start();
            }
        }
        //此内部类不可以是static的，否则泛型语法不能使用，外部类DefaultThreadPoolImpl声明的泛型Job在static中无法访问，因为要通过this
        //如果非要加static，在Worker就要声明<Job extends Runnable>, 但job=jobs.removeFirst()会提示此Job非彼Job，内外类均声明了Job extends Runnable,这是两个Job类型，Worker类使用外部类的成员变量List<Job>时进行赋值还需要强转，很不值得
        class Worker implements Runnable{
            private volatile boolean running = true;
            @Override
            public void run() {
                while (running){
                    Job job = null;
                    synchronized (jobs) {
                        // 如果工作者列表是空的，那么就wait
                        while (jobs.isEmpty()) {
                            try {
                                jobs.wait();
                            } catch (InterruptedException ex) {
                                // 感知到外部对WorkerThread的中断操作，因InterruptedException会清空中断位，这里设置后返回
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        // 取出一个Job
                        job = jobs.removeFirst();
                    }
                    if (job != null) {
                        try {
                            job.run();
                        } catch (Exception ex) {
                            // 忽略Job执行中的Exception
                        }
                    }
                }
            }
            public void shutdown(){
                running = false;
            }
        }
        @Override
        public void execute(Job job) {
            if (job != null){
                //添加一个job，然后进行通知
                synchronized (jobs){
                    jobs.addLast(job);
                    jobs.notify();//使用notify()方法将会比notifyAll()方法获得更小的开销（避免将等待队列中的线程全部移动到阻塞队列中）
                }
            }
        }
        @Override
        public void shutdown() {
            for (Worker worker : workers) {
                worker.shutdown();//此shutdown方法不是那个弃用的，进行了标记位设置
            }
        }
        @Override
        public void addWorkers(int num) {
            synchronized (jobs){
                //限制新增的Worker数量不能超过最大数量
                if (num + this.workerNum > MAX_WORKER_NUMBERS){
                    num = MAX_WORKER_NUMBERS - this.workerNum;
                }
                initializeWorkers(num);
                this.workerNum += num;
            }
        }
        @Override
        public void removeWorker(int num) {
            synchronized (jobs) {
                if (num >= this.workerNum) {
                    throw new IllegalArgumentException("beyond workNum");
                }
                // 按照给定的数量停止Worker
                int count = 0;
                while (count < num) {
                    //没法使用removeFirst, Collections.synchronizedList返回的是List
                    Worker worker = workers.get(count);//get(int index)
                    if (workers.remove(worker)) { //remove(obj)返回boolean,remove(int index)返回obj
                        worker.shutdown();
                        count++;
                    }
                }
                this.workerNum -= count;
            }
        }
        @Override
        public int getJobSize() {
            return jobs.size();
        }
    }
    static class SimpleThreadPoolTester{
        public static void main(String[] args) throws InterruptedException {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //try {
                    //    TimeUnit.SECONDS.sleep(5);
                    //} catch (InterruptedException e) {
                    //    e.printStackTrace();
                    //}
                    System.out.println("线程执行任务："+ Thread.currentThread().getName());
                }
            };
            DefaultThreadPoolImpl defaultThreadPool = new DefaultThreadPoolImpl(5);
            defaultThreadPool.execute(new Thread(runnable,"IO1"));//这里设置名字不能用
            defaultThreadPool.execute(new Thread(runnable,"IO2"));
            defaultThreadPool.execute(new Thread(runnable,"IO3"));
            defaultThreadPool.execute(new Thread(runnable,"IO4"));
            defaultThreadPool.execute(new Thread(runnable,"IO5"));
            defaultThreadPool.execute(new Thread(runnable,"IO6"));
            defaultThreadPool.shutdown();
            TimeUnit.SECONDS.sleep(2);
            defaultThreadPool.execute(new Thread(runnable,"IO3"));
        }
        /*
         * Job与Worker都是 extends Runnable 的
         * 发现一个现象：
         * 1. defaultThreadPool.shutdown();之前execute的任务数量如果没有达到new DefaultThreadPoolImpl时传入的数量，则在之后execute提交的任务还会被执行，因为还有Worker停在while(running)中的wait方法处
         * 2. defaultThreadPool.shutdown();之前execute提交的任务数量 >= new DefaultThreadPoolImpl时传入的数量,则之后提交的job不会执行，shutdown修改running为false的速度似乎非常快，可以赶在任意Worker下一轮while循环之前生效，这估计是volatile的效果
         */
    }

    /**
     * 基于线程池技术实现一个简单的Web服务器
     *
     */
    static class SimpleHttpServer{
        //处理HttpRequest的线程池
        static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPoolImpl(1);
        // SimpleHttpServer的根路径
        static String basePath;
        static ServerSocket serverSocket;
        // 服务监听端口
        static int port = 8080;
        public static void setPort(int port) {
            if (port > 0) {
                SimpleHttpServer.port = port;
            }
        }
        public static void setBasePath(String basePath){
            if (basePath != null && new File(basePath).exists() && new File(basePath).isDirectory()){
                SimpleHttpServer.basePath = basePath;
            }
        }
        // 启动SimpleHttpServer
        public static void start() throws Exception {
            serverSocket = new ServerSocket(port);
            Socket socket = null;
            while ((socket = serverSocket.accept()) != null) {
                // 接收一个客户端Socket，生成一个HttpRequestHandler，放入线程池执行
                threadPool.execute(new HttpRequestHandler(socket));
            }
            serverSocket.close();
        }

        static class HttpRequestHandler implements Runnable{
            private Socket socket;
            public HttpRequestHandler(Socket socket) {
                this.socket = socket;
            }
            @Override
            public void run() {
                String line = null;
                BufferedReader br = null;
                BufferedReader reader = null;
                PrintWriter out = null;
                InputStream in = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String header = reader.readLine();
                    String filePath = basePath + header.split(" ")[1];
                    out = new PrintWriter(socket.getOutputStream());
                    // 如果请求资源的后缀为jpg或者ico，则读取资源并输出
                    if (filePath.endsWith("jpg") || filePath.endsWith("png")) {
                        in = new FileInputStream(filePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int i = 0;
                        while ((i = in.read()) != -1) {
                            baos.write(i);
                        }
                        byte[] array = baos.toByteArray();
                        out.println("HTTP/1.1 200 OK");
                        out.println("Server: Molly");
                        out.println("Content-Type: image/jpeg");
                        out.println("Content-Length: " + array.length);
                        out.println("");
                        socket.getOutputStream().write(array, 0, array.length);
                    }else {
                        br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                        out = new PrintWriter(socket.getOutputStream());
                        out.println("HTTP/1.1 200 OK");
                        out.println("Server: Molly");
                        out.println("Content-Type: text/html; charset=UTF-8");
                        out.println("");
                        while ((line = br.readLine()) != null) {
                            out.println(line);
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    out.println("HTTP/1.1 500");
                    out.println("");
                    out.flush();
                }finally {
                    close(br, in, reader, out, socket);
                }
            }
            private static void close(Closeable... closeables){
                if (closeables != null) {
                    for (Closeable closeable : closeables) {
                        try {
                            closeable.close();
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }

        public static void main(String[] args) throws Exception {
            String basePath = "~/concurrency/src/main/resources/javaconcurrencyart/_04simplehttpserver";
            SimpleHttpServer.setBasePath(basePath);
            SimpleHttpServer.start();
        }
        /**
         * 访问 127.0.0.1:8080/index.html可以看到文字，但图片提示server返回的response错误无法加载
         * 。。。待研究
         */
    }


}
