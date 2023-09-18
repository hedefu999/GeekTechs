package com.concurrency.javaconcurrencyart;

import com.concurrency.JOLUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 并发的底层原理
 */
public class _02ConcurrencyPrinciple {
    /**
     * jol展示jvm中object header特性
     * refto https://blog.csdn.net/baidu_28523317/article/details/104453927
     */
    static class JavaObjectLayout {
        static class Student {
            private String name;
            private Integer age;
            private Boolean married;

            public Student(String name, Integer age, boolean sex) {
                this.name = name;
                this.age = age;
                this.married = sex;
            }
        }
        static class Student2 {
            private String name;
            private int age;
            private boolean married;

            public Student2(String name, Integer age, boolean sex) {
                this.name = name;
                this.age = age;
                this.married = sex;
            }
        }

        public static void main(String[] args) {
            Student stu1 = new Student("jack is a good man",12,false);
            JOLUtils.printObjectHeader(stu1);
            System.out.println(Integer.toBinaryString(stu1.hashCode()));
            System.out.println(Integer.toHexString(stu1.hashCode()));

            Student2 stu2 = new Student2("name",12,false);
            JOLUtils.printObjectHeader(stu2);


            Object plainObject = new Object();
            JOLUtils.printObjectHeader(plainObject);
            System.out.println(Integer.toHexString(plainObject.hashCode()));
        }

    }
    /**
     * MarkWord在四种锁状态下的内容变化
     */
    static class MarkWordInFourLevel{
        /**
         * 无锁状态下的跳过，0000 0001
         */

        /**
         * 偏向锁情况下，偏向锁是延迟加载的，所有对象初始是无锁状态，延时后进入可偏向状态
         */
        static class LockObj { }
        static class BiasedLockLevel0{
            public static void main(String[] args) throws InterruptedException {
                LockObj a = new LockObj();
                JOLUtils.printObjectHeader(a);
            }
            /*
             * 无锁
             * 00000001 00000000 00000000 00000000
             * 00000000 00000000 00000000 00000000
             * 10000001 11000010 00000000 11111000
             *
             * 如果 VM Options 添加 -XX:BiasedLockingStartupDelay=0 : 偏向锁
             * 00000101 00000000 00000000 00000000
             * 00000000 00000000 00000000 00000000
             * 10000001 11000010 00000000 11111000
             */
        }
        static class BiasedLockLevel0Wait{
            public static void main(String[] args) throws InterruptedException {
                Thread.sleep(5000);
                LockObj a = new LockObj();
                JOLUtils.printObjectHeader(a);
                //System.out.println(ClassLayout.parseInstance(a).toPrintable());
            }
            /*
             * 偏向锁 0000 0101
             * 00000101 00000000 00000000 00000000
             * 00000000 00000000 00000000 00000000
             * 01000011 11000010 00000000 11111000
             *
             * 如果VM Options配置 -XX:-UseBiasedLocking : 无锁 00000001
             * 00000001 00000000 00000000 00000000
             * 00000000 00000000 00000000 00000000
             * 01000011 11000010 00000000 11111000
             */
        }
        /**
         * 上述两种偏向锁的ThreadID和epoch（共56位）都是0，此时并没有偏向任何线程，仅表明锁对象处于可偏向的状态
         * 进入sync同步代码块后才写入偏向线程ID
         */
        static class BiasedLockLevel{
            public static void main(String[] args) throws InterruptedException {
                Thread.sleep(5000);
                LockObj a = new LockObj();
                synchronized (a){
                    JOLUtils.printObjectHeader(a);

                    System.out.println(Thread.currentThread().getId());
                    System.out.println(Long.toBinaryString(Thread.currentThread().getId()));
                }
            }
            /*
             * 偏向锁 00000101，当前只有一个线程竞争锁
             * 00000101 00101000 10000000 00010100
             * 10100101 01111111 00000000 00000000
             * 00000101 11010110 00000000 11111000
             *
             * 如果VM Options配置 -XX:-UseBiasedLocking : 轻量级锁 11110000
             * 11110000 00001000 10101101 00001100
             * 00000000 01110000 00000000 00000000
             * 00000101 11010110 00000000 11111000
             */
        }
        /**
         * 踩坑：hashCode方法会导致偏向锁无法进入！因为31位被占用拿来填充hashCode了，偏向锁的ThreadID没地方放了
         * 无法进入偏向锁，此时进入的是轻量级锁
         */
        static class BiasedLockLevel2{
            public static void main(String[] args) throws InterruptedException {
                Thread.sleep(5000);
                Object lock = new Object();
                //System.identityHashCode(lock); //A          100111101111000010011000010110
                System.out.println(Integer.toBinaryString(lock.hashCode())); //B 100111101111000010011000010110
                System.out.println(Integer.toHexString(lock.hashCode()));
                JOLUtils.printObjectHeader(lock);
                synchronized (lock){
                    JOLUtils.printObjectHeader(lock);
                }
            }/*
             * 偏向锁
             * 00000101 00000000 00000000 00000000
             * 00000000 00000000 00000000 00000000
             * 11100101 00000001 00000000 11111000
             * 偏向锁
             * 00000101 01010000 00000000 01001011
             * 10000111 01111111 00000000 00000000
             * 11100101 00000001 00000000 11111000
             *
             * 打开上述代码中的A或B行
             * 无锁
             * 00000001 00010110 00100110 10111100
             * 00100111 00000000 00000000 00000000
             * 11100101 00000001 00000000 11111000
             *
             * 轻量级锁
             * 11110000 10101000 11010000 00000011
             * 00000000 01110000 00000000 00000000
             * 11100101 00000001 00000000 11111000
             */
        }

        //在偏向锁，轻量锁，重量锁，hashcode会被转移到Monitor中。
        /**
         * =-=-=-=-=-=- 下面是竞争环境下锁的情况 -=-=-=-=-=-=-=
         * 轻量级锁情况下
         */
        static class LightWeightLockLevel{
            public static void main(String[] args) throws InterruptedException {
                Thread.sleep(5000);
                Object lock = new Object();
                Thread thread1 = new Thread(){
                    @Override
                    public void run() {
                        synchronized (lock){
                            //JOLUtils.printObjectHeader(lock);//A 00000101
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) { }
                        }
                    }
                };
                Thread thread2 = new Thread(){
                    @Override
                    public void run() {
                        synchronized (lock){
                            //JOLUtils.printObjectHeader(lock);//B 11101010
                        }
                    }
                };
                thread1.start();
                Thread.sleep(1000);//C
                thread2.start();
            }
            /*
             * A处是偏向锁(101)，B处是轻量级锁(00)，偏向锁出现非偏向线程竞争，膨胀成轻量级锁
             * C处时间改为2000，A处是偏向锁101，B处是重量级锁10
             *
             * 配置VM启动参数 -XX:+PrintGCApplicationStoppedTime -XX:+PrintSafepointStatistics -XX:PrintSafepointStatisticsCount=1
             * 可得到下述日志
             *
         vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
1.027: no vm operation                  [      19          0              0    ]      [     0     0     0     0     0    ]  0
Total time for which application threads were stopped: 0.0000678 seconds, Stopping threads took: 0.0000144 seconds
         vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
1.212: Deoptimize                       [      19          0              0    ]      [     0     0     0     0     0    ]  0
Total time for which application threads were stopped: 0.0000719 seconds, Stopping threads took: 0.0000105 seconds
         vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
2.216: no vm operation                  [      19          0              0    ]      [     0     0     0     0     0    ]  0
Total time for which application threads were stopped: 0.0001054 seconds, Stopping threads took: 0.0000216 seconds
         vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
4.179: EnableBiasedLocking              [      19          0              0    ]      [     0     0     0     0     0    ]  0
Total time for which application threads were stopped: 0.0001845 seconds, Stopping threads took: 0.0000231 seconds
         vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
6.157: RevokeBias                       [      21          0              0    ]      [     0     0     0     0     0    ]  0
Total time for which application threads were stopped: 0.0001044 seconds, Stopping threads took: 0.0000533 seconds
         vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
7.155: RevokeBias                       [      21          0              0    ]      [     0     0     0     0     0    ]  0
Total time for which application threads were stopped: 0.0000711 seconds, Stopping threads took: 0.0000257 seconds
         vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
7.155: no vm operation                  [      18          0              1    ]      [     0     0     0     0   351    ]  0

Polling page always armed
Deoptimize                         1
EnableBiasedLocking                1
RevokeBias                         2
    1 VM operations coalesced during safepoint
Maximum sync time      0 ms
Maximum vm operation time (except for Exit VM operation)      0 ms

                日志中展示了偏向锁在4秒后开启，在6秒时thread2进入进行竞争，偏向锁撤销。7秒再次撤销？？？
                如果在拿到锁时存在其他操作，会出现大量revokeBias操作，所以高并发应用应直接关闭偏向锁
             */
        }
        static class LightWeightLockLevel2{
            public static void main(String[] args) throws InterruptedException {
                Thread.sleep(5000);
                Object lock = new Object();
                Thread thread1 = new Thread(){
                    @Override
                    public void run() {
                        synchronized (lock){
                            JOLUtils.printObjectHeader(lock);//A
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) { }
                    }
                };
                Thread thread2 = new Thread(){
                    @Override
                    public void run() {
                        synchronized (lock){
                            JOLUtils.printObjectHeader(lock);//B
                        }
                    }
                };
                thread1.start();
                Thread.sleep(1000);//C
                thread2.start();
            }
            /*
             * 将thread1同步代码块执行时间缩短，sleep移出sync代码块，让锁处于无竞争状态下足够长的时间
             * A处是偏向锁101，B处是轻量级锁00
             */
        }
        /**
         * 直接竞争
         */
        static class WightLock{
            public static void main(String[] args) throws InterruptedException {
                Thread.sleep(5000);
                Object lock = new Object();
                Thread thread1 = new Thread(){
                    @Override
                    public void run() {
                        synchronized (lock){
                            JOLUtils.printObjectHeader(lock);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) { }
                        }
                        JOLUtils.printObjectHeader(lock);
                    }
                };
                Thread thread2 = new Thread(){
                    @Override
                    public void run() {
                        synchronized (lock){
                            JOLUtils.printObjectHeader(lock);
                            //try {
                            //    Thread.sleep(2000);
                            //} catch (InterruptedException e) { }
                        }
                        JOLUtils.printObjectHeader(lock);
                    }
                };
                thread1.start();
                thread2.start();
                Thread.sleep(3000);
                JOLUtils.printObjectHeader(lock);//A
            }
            /**
             * A处是无锁的状态01，其他所有JOLUtils调用处都是重量级锁10
             */
        }

    }

    static class CASSimulation{
        private AtomicInteger atomicI = new AtomicInteger(0);
        private int i = 0;
        public static void main(String[] args) {
            final CASSimulation cas = new CASSimulation();
            List<Thread> ts = new ArrayList<Thread>(600);
            long start = System.currentTimeMillis();
            for (int j = 0; j < 100; j++) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10000; i++) {
                            cas.count();
                            cas.safeCount();
                        }
                    }
                });
                ts.add(t);
            }
            for (Thread t : ts) {
                t.start();
            }
            // 等待所有线程执行完成
            for (Thread t : ts) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(cas.i);//996414
            System.out.println(cas.atomicI.get());//100 0000
            System.out.println(System.currentTimeMillis() - start);
        }
        /**        * 使用CAS实现线程安全计数器        */
        private void safeCount() {
            for (;;) {
                int i = atomicI.get();
                boolean suc = atomicI.compareAndSet(i, ++i);
                if (suc) {
                    break;
                }
            }
        }
        /**
         * 非线程安全计数器
         */
        private void count() {
            i++;
        }
    }

    /**
     * 锁消除性能验证
     */
    static class LockEliminateTest{
        static class SynchronizedTest02 {
            public static void main(String[] args) {
                SynchronizedTest02 test02 = new SynchronizedTest02();
                //启动预热
                for (int i = 0; i < 10000; i++) {
                    i++;
                }
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000000; i++) {
                    test02.append("abc", "def");
                }
                System.out.println("Time=" + (System.currentTimeMillis() - start));
            }
            public void append(String str1, String str2) {
                StringBuffer sb = new StringBuffer();
                sb.append(str1).append(str2);
            }
            /**
             * 为消除其他影响，这里禁用了偏向锁
             * 分别开启和禁用锁消除优化，耗时相差比较大
             * -server -XX:+DoEscapeAnalysis -XX:+EliminateLocks -XX:-UseBiasedLocking 1890
             * -server -XX:+DoEscapeAnalysis -XX:-EliminateLocks -XX:-UseBiasedLocking 4150
             */
        }
    }

    /**
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=自旋锁的几种锁形式-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * (不使用系统的mutex，如何自己实现互斥锁) TicketLock、CLHLock、MCSLock
     */
    // 使用volatile+while循环
    static class NativeLock{
        volatile boolean flag = false;
        void lock(){
            while (!casBoolean(flag,false,true)){
                //循环尝试
            }
        }
        void unlock(){
            flag = false;
        }
        boolean casBoolean(boolean var, boolean source, boolean target){
            if (var == source){
                var = target;
                return true;
            }
            return false;
        }
        //不支持公平锁，可能存在线程一直在循环等待
    }

    //TicketLock 线程想要竞争某个锁，需要先领一张ticket，然后监听flag，发现flag被更新为手上的ticket的值了，才能去占领锁
    static class TicketLock{
        AtomicInteger ticket = new AtomicInteger(0);
        volatile int flag = 0;
        void lock(){
            int my_ticket = ticket.getAndIncrement();//发ticket必须是一个原子操作，不能多个线程拿到同一个ticket
            while (my_ticket != flag){
            }
        }
        void unlock(){
            flag++;
        }//支持公平锁，但不支持重入锁
    }

    static class CLHLock{
        @Data @AllArgsConstructor
        static class Node{
            volatile boolean locked;
            /*
             * 线程监听前一个node的flag，也就是node.prev.flag = false, 表示前一个结点对应的线程已释放了锁
             * 本线程此时就可以获得锁，将node.flag修改为false
             */
            Node prev;
            public static final Node DUMMY = new Node(false,null);
        }
        static class CLHLocker{
            volatile Node head, tail;
            public CLHLocker(){
                head = tail = Node.DUMMY;
            }
            public Node lock(){
                Node node = new Node(true,null);
                Node oldTail = tail;
                /*
                 * 比较现在的tail是否还是上一步的oldTail
                 * 如果不是，重新赋值刷新一次局部变量oldTail
                 * 如果是，改成node，这样就安全地把新结点放到了链表尾部，而且不会因线程竞争导致尾部结点加锁状态混乱
                 */
                while (!cas(tail,oldTail,node)){
                    oldTail = tail;
                }
                node.setPrev(oldTail);
                /*
                 * 结点CAS添加操作成功后才会进行真正的prev状态监听
                 */
                while (node.getPrev().isLocked()){
                    //监听前驱结点的locked变量，进入此处表示prev释放了锁，当前线程可以拿锁了
                }
                return node;
            }

            private boolean cas(Node tail, Node oldTail, Node node) {
                if (node == oldTail){
                    tail = node;
                    return true;
                }
                return false;
            }

            public void unlock(Node node){
                node.setLocked(false);
            }
        }
    }

    /**
     * CLH锁每个线程都在前驱结点的locked字段上自旋，而在NUMA体系中，有可能多个线程工作在多个不同的socket上的core里。
     * 如果前驱节点的内存跟监听线程的core距离过远，会有性能问题。
     * MCS是人名的简写 - John M. Mellor-Crummey and Michael L. Scott
     *
     * MCS与CLH最大的不同在于：CLH是在前驱节点的locked域上自旋，MCS是在自己节点上的locked域上自旋。
     */
    static class MCSLock{
        @Data @AllArgsConstructor
        static class Node{
            volatile boolean locked;
            Node next;//后继结点
        }
        static class MCSLocker{
            volatile Node head, tail;
            public MCSLocker(){
                head = tail = null;
            }
            public Node lock(){
                Node node = new Node(true,null);
                Node oldTail = tail;
                while (!cas(tail,oldTail,node)){
                    oldTail = tail;
                }
                if (null == oldTail){
                    return node;
                }
                oldTail.setNext(node);//CLH的是setPrev,但两者都是在链表结尾挂结点
                while (node.isLocked()){
                    //线程只监听自己的Node的locked字段，自己的字段却要监听，自己的字段自己改了会不知道？
                    //之所以这样是因为每个线程会改next.locked
                }
                return node;
            }
            public void unlock(Node node){
                if (node.getNext() == null){//node.next为null表示队列空了
                    if (cas(tail,node,null)){
                        //即使当前结点的后缀为null，也要用cas检查
                        return;
                    }
                    while (node.getNext() != null){
                        //cas失败
                    }
                }
                //直接修改next的状态，告诉下一个等待的线程可以拿锁了
                node.getNext().setLocked(false);
            }
            private boolean cas(Node tail, Node oldTail, Node node) {
                //省略
                return false;
            }
        }

    }


    /**
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=synchronized代码块与方法的-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * (不使用系统的mutex，如何自己实现互斥锁) TicketLock、CLHLock、MCSLock
     */

}
