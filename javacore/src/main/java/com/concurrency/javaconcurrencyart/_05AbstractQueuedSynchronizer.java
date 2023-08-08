package com.concurrency.javaconcurrencyart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * 队列同步器的原理及实现
 */
public class _05AbstractQueuedSynchronizer {
    private final static Logger log = LoggerFactory.getLogger(_05AbstractQueuedSynchronizer.class);

    /**
     * 设计一个同步工具：该工具在同一时刻只允许至多两个线程同时访问，超过两个线程访问将被阻塞
     * 分析：同一时刻支持多个线程访问，表明是共享式访问，就要求必须实现AQS的tryAcquireShared和tryReleaseShared方法
     * 线程数量的控制可以使用状态字段status实现，设置范围为 0 1 2，0表示当前已有两个线程获取了同步资源，此时再有其他线程获取同步状态会被阻塞
     * 同步状态的变更需要使用compareAndSet方法做原子性保障
     */
    static class TwinsLock implements Lock{
        private final Sync sync = new Sync(2);
        private static final class Sync extends AbstractQueuedSynchronizer{
            Sync(int count){
                if (count <= 0){
                    throw new IllegalArgumentException("count must gt zero");
                }
                setState(count);
            }

            @Override
            public int tryAcquireShared(int reduceCount) {
                for (;;){
                    int current = getState();
                    int newCount = current - reduceCount;
                    if (newCount < 0 || compareAndSetState(current, newCount)){
                        return newCount;
                    }
                }
            }

            @Override
            protected boolean tryReleaseShared(int returnCount) {
                for (;;){
                    int current = getState();
                    int newCount = current + returnCount;
                    if (compareAndSetState(current, newCount)){
                        return true;
                    }
                }
            }
        }
        @Override
        public void lock() {
            sync.acquireShared(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void unlock() {
            sync.releaseShared(1);
        }

        @Override
        public Condition newCondition() {
            return null;
        }

        /**
         * 测试TwinsLock
         */
        public static void main(String[] args) throws InterruptedException {
            final Lock lock = new TwinsLock();
            Runnable workerRunnable = new Runnable(){
                @Override
                public void run() {
                    lock.lock();
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        System.out.println(Thread.currentThread().getName());
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                }
            };
            for (int i = 0; i < 5; i++) {
                Thread thread = new Thread(workerRunnable);
                thread.start();
            }
            /**
             * 线程会结对打印名称
             */
        }
    }

    static class ReadWriteLockDemo{
        static Map<String,Object> map = new HashMap<>();
        static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        static Lock rl = rwl.readLock();
        static Lock wl = rwl.writeLock();
        //读取value
        public static final Object get(String key){
            rl.lock();
            try {
                return map.get(key);
            }finally {
                rl.unlock();
            }
        }
        // 设置key对应的value，并返回旧的value
        public static final Object put(String key, Object value) {
            wl.lock();
            try {
                return map.put(key, value);
            } finally {
                wl.unlock();
            }
        }
        // 清空所有的内容
        public static final void clear() {
            wl.lock();
            try {
                map.clear();
            } finally {
                wl.unlock();
            }
        }
    }

    /**
     * 问题1：写锁被占用的情况下，其他线程获取读锁一定是被阻塞的，那读锁被占用的情况下，其他线程获取写锁呢？
     * 问题2：上面是不同线程之间，如果同一个线程获取读锁后获取同一把锁上的写锁呢？顺序反过来呢
     */
    static class ReadWriteLockMutuality{
        static class AReadThread extends Thread{
            private ReentrantReadWriteLock.ReadLock readLock = null;
            public AReadThread(ReentrantReadWriteLock.ReadLock readLock) {
                this.readLock = readLock;
            }
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+"开始获取读锁");
                readLock.lock();
                System.out.println(Thread.currentThread().getName()+"获取到读锁");
                try {
                    TimeUnit.SECONDS.sleep(6);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                readLock.unlock();
                System.out.println(Thread.currentThread().getName()+"释放了读锁");
            }
        }
        static class AWriteThread extends Thread{
            private ReentrantReadWriteLock.WriteLock writeLock = null;
            public AWriteThread(ReentrantReadWriteLock.WriteLock writeLock) {
                this.writeLock = writeLock;
            }
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+"开始获取写锁");
                writeLock.lock();
                System.out.println(Thread.currentThread().getName()+"获取到写锁");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                writeLock.unlock();
                System.out.println(Thread.currentThread().getName()+"释放了写锁");
            }
        }
        //自身先获取写锁不释放的情况下再获取读锁，是典型的锁降级过程
        static class SelfReadWriteLockThread extends Thread{
            private ReentrantReadWriteLock.ReadLock readLock = null;
            private ReentrantReadWriteLock.WriteLock writeLock = null;
            public SelfReadWriteLockThread(ReentrantReadWriteLock.ReadLock readLock, ReentrantReadWriteLock.WriteLock writeLock) {
                this.readLock = readLock;
                this.writeLock = writeLock;
            }
            @Override
            public void run() {
                try {
                    log.info("开始获取读锁");
                    readLock.lock();
                    log.info("获取到读锁");
                    TimeUnit.SECONDS.sleep(3);
                    log.info("开始获取写锁");
                    writeLock.lock();
                    log.info("获取到写锁");
                    TimeUnit.SECONDS.sleep(1);

                    readLock.unlock();
                    log.info("释放读锁");
                    writeLock.unlock();
                    log.info("释放写锁");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void main(String[] args) throws InterruptedException {
            ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
            //-=-=-= test 1
            //AReadThread aReadThread = new AReadThread(readWriteLock.readLock());aReadThread.setName("aReadThread");
            //AWriteThread aWriteThread = new AWriteThread(readWriteLock.writeLock());aWriteThread.setName("aWriteThread");
            //aReadThread.start();
            //TimeUnit.SECONDS.sleep(2);
            //aWriteThread.start();
            /*
             * aReadThread开始获取读锁
             * aReadThread获取到读锁
             * aWriteThread开始获取写锁
             * aReadThread释放了读锁
             * aWriteThread获取到写锁
             * aWriteThread释放了写锁
             *
             * 显然，读锁被获取的情况下其他线程获取写锁会被阻塞到所有读锁被释放
             */

            //-=-=-=-= test 2
            //SelfReadWriteLockThread readWriteLockThread = new SelfReadWriteLockThread(readWriteLock.readLock(), readWriteLock.writeLock());
            //readWriteLockThread.start();
            /*
             * 开始获取读锁
             * 获取到读锁
             * 开始获取写锁
             *
             * 只打印了三行就停住了，显然读写锁进行"锁升级"会把自己阻塞掉
             * 另外，锁降级时读写锁的释放顺序颠倒不会产生阻塞问题
             */
        }
    }

    static class LockSupportDemo{
        static class AThread extends Thread{
            @Override
            public void run() {
                LockSupport.park();
                log.info("发车，跳过了park方法，是否是因为中断：{}", Thread.currentThread().isInterrupted());
            }
        }
        static class BThread extends Thread{
            String locker = "mylocker";
            @Override
            public void run() {
                LockSupport.park(locker);
                //其他park方法
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            }
        }
        public static void main(String[] args) throws InterruptedException {
            AThread aThread = new AThread();
            aThread.start();
            aThread.interrupt();
            TimeUnit.SECONDS.sleep(3);
            log.info("准备开车");
            LockSupport.unpark(aThread);
            /*
             * interrupt操作会颠倒两行日志的打印顺序
             */
            BThread bThread = new BThread();bThread.start();
            /*
             * 线程jstack出的dump信息
             java.lang.Thread.State: WAITING (parking)
             at sun.misc.Unsafe.park(Native Method)
             - parking to wait for  <0x0000000716f35a78> (a java.lang.String)
             at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
             at com.concurrency.javaconcurrencyart._05AbstractQueuedSynchronizer$LockSupportDemo$BThread.run(_05AbstractQueuedSynchronizer.java:268)

             Locked ownable synchronizers:
             - None
             */
            TimeUnit.SECONDS.sleep(3);
            LockSupport.unpark(bThread);
        }
    }

    /**
     * 通过有界队列演示Condition的用法
     * 有界队列是一种特殊的队列，当队列为空时，队列的获取操作将会阻塞获取线程，直到队列中有新增元素，当队列已满时，队列的插入操作将会阻塞插入线程，直到队列出现“空位”
     * todo 代码待默写
     */
    static class BoundedQueue<T> {
        private Object[] items;
        // 添加的下标，删除的下标和数组当前数量
        private int addIndex, removeIndex, count;
        private Lock lock = new ReentrantLock();
        private Condition notEmpty = lock.newCondition();
        private Condition notFull = lock.newCondition();
        public BoundedQueue(int size) {
            items = new Object[size];
        }
        // 添加一个元素，如果数组满，则添加线程进入等待状态，直到有"空位"
        public void add(T t) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length)
                    notFull.await();
                items[addIndex] = t;
                if (++addIndex == items.length)
                    addIndex = 0;
                ++count;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }
        // 由头部删除一个元素，如果数组空，则删除线程进入等待状态，直到有新添加元素
        @SuppressWarnings("unchecked")
        public T remove() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0)
                    notEmpty.await();
                Object x = items[removeIndex];
                if (++removeIndex == items.length)
                    removeIndex = 0;
                --count;
                notFull.signal();
                return (T) x;
            } finally {
                lock.unlock();
            }
        }
    }
}
