package com.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

@Slf4j
public class ConcurrencyPractice {
    static void sleep(int millis){
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        }catch (InterruptedException e){log.error(e.getMessage());}
    }

    /**
     * 生产者消费者多线程实现案例
     * 带有容量限制，不断提交生产任务，当容量满时阻塞提交者。
     * 不断消费消息，当消息消费后如果有提交者被阻塞，将会立刻生产消息
     * 当消息消费完时会阻塞消费者，等待生产消息
     */
    static class ProducerConsumer {
        //使用原生wait-notify实现
        static class MessageRepository {
            private int capacity;    // 可容纳消息总数
            private int size;        // 实际消息数量
            public MessageRepository(int capacity) {
                this.capacity = capacity;
                this.size = 0;
            }
            public synchronized void produce(int orderedCount) {
                try {
                    int remainingCount = orderedCount;
                    while (remainingCount > 0) {
                        // 库存已满，等待消费者进行消费
                        while (size >= capacity) this.wait();
                        // 获取“实际生产的数量”(即库存中新增的数量)
                        int actualCount = (size + remainingCount) > capacity ? (capacity - size) : remainingCount;
                        size += actualCount;
                        remainingCount -= actualCount;
                        log.info("{} - produce {} in total, {} remained, {} actually, {} in store", Thread.currentThread().getName(), orderedCount, remainingCount, actualCount, size);
                        this.notifyAll();// 通知消费者可以消费了
                    }
                } catch (InterruptedException e) {}
            }
            public synchronized void consume(int orderedCount) {
                try {
                    int remainingCount = orderedCount;
                    while (remainingCount > 0) {
                        while (size <= 0) this.wait();
                        int actualConsumedCount = Math.min(size, remainingCount);
                        size -= actualConsumedCount;
                        remainingCount -= actualConsumedCount;
                        log.info("{} - consume {} in total, {} remained, {} actually, {} in store", Thread.currentThread().getName(), orderedCount, remainingCount, actualConsumedCount, size);
                        this.notifyAll();//通知生产者可以生产了
                    }
                } catch (InterruptedException e) {}
            }
            public String toString() {
                return "capacity:"+capacity+", actual size:"+size;
            }
        }
        //MessageRepository可以通过JUC锁再实现一个
        static class MessageRepoByJUC {
            private int capacity = 100;
            private int size = 0;
            private Lock lock = new ReentrantLock();
            private Condition fullLock = lock.newCondition();
            private Condition emptyLock = lock.newCondition();
            public void produce(int orderedCount){
                lock.lock();
                int remainingCount = orderedCount;
                try {
                    while (remainingCount > 0){
                        if (size >= capacity) fullLock.await();
                        size += remainingCount;
                        remainingCount = size > capacity ? size-capacity : 0;
                        size -= remainingCount;
                        log.info("生产线程{}超负荷{}待生产，当前库存{}",Thread.currentThread().getName(),remainingCount,size);
                        emptyLock.signal();
                    }
                }catch (InterruptedException e){}finally {
                    lock.unlock();
                }
            }
            public void consume(int orderedCount){
                lock.lock();
                int remainingCount = orderedCount;
                try {
                    while (remainingCount > 0){
                        if (size <= 0) emptyLock.await();
                        if (remainingCount < size){
                            remainingCount = 0;
                            size -= remainingCount;
                        }else {
                            remainingCount -= size;
                            size = 0;
                        }
                        log.info("消费线程{}超采{}待处理，当前库存{}", Thread.currentThread().getName(),remainingCount,size);
                        fullLock.signal();
                    }
                }catch (InterruptedException e){}finally {
                    lock.unlock();
                }
            }
        }
        static class Producer {
            private MessageRepository repo;
            public Producer(MessageRepository repo) {
                this.repo = repo;
            }
            // 消费产品：新建一个线程向仓库中生产产品。
            public void produce(final int count) {
                new Thread(() -> repo.produce(count),"producer").start();
            }
        }
        static class Customer {
            private MessageRepository respo;
            public Customer(MessageRepository repo) {
                this.respo = repo;
            }
            // 消费产品：新建一个线程从仓库中消费产品。
            public void consume(final int count) {
                new Thread(() -> respo.consume(count),"consumer").start();
            }
        }
        public static void main(String[] args) {
            MessageRepository repo = new MessageRepository(100);
            Producer producer = new Producer(repo);
            Customer customer = new Customer(repo);
            producer.produce(60);
            sleep(3000);
            log.info("-------------------");
            producer.produce(120);
            sleep(3000);
            log.info("-------------------");
            customer.consume(90);
            sleep(3000);
            log.info("-------------------");
            customer.consume(150);
            sleep(3000);
            log.info("-------------------");
            producer.produce(110);
        }
    }

    static class JUCLockPractice{
        static class LockSupportPractice{
            static void waitNotify1(){
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            log.info("before notify");
                            sleep(3000);
                            notify();
                            log.info("after notify");
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                synchronized (thread){
                    try {
                        thread.start();
                        //Thread.sleep(3000);
                        log.info("before wait");
                        /**
                         * 阻塞main还是thread,要看synchronized拿了谁的锁，又对调用了谁的wait/notify（前后不一致会导致illegalMonitorStateException）
                         *
                         */
                        thread.wait();
                        log.info("after wait");
                    }catch (InterruptedException e){
                        log.error(e.getMessage());
                    }
                }/*
                如果wait notify调用时序颠倒了，notify没有任何效果，还会wait后一直阻塞调用线程
                */
            }
            static void parkUnpark(){
                Thread thread = Thread.currentThread();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        log.info("before unpark");
                        try {
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            log.error(e.getMessage());}
                        log.info("blocker = {}", LockSupport.getBlocker(thread));
                        LockSupport.unpark(thread);
                        try {
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            log.error(e.getMessage());}
                        log.info("blocker = {}", LockSupport.getBlocker(thread));
                        log.info("after unpark");
                    }
                };
                Thread childThread = new Thread(runnable);
                childThread.start();
                log.info("before park");
                LockSupport.park("StringBlocker:id=3,txId=4");
                log.info("after park");
            }
            static void unparkPark(){
                Thread thread = Thread.currentThread();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        log.info("before unpark");
                        LockSupport.unpark(thread);
                        log.info("after unpark");
                    }
                };
                Thread childThread = new Thread(runnable);
                childThread.start();
                sleep(3);
                log.info("before park");
                LockSupport.park("StringBlocker");
                log.info("after park");
            }
            public static void main(String[] args) throws Exception {
                waitNotify1();
                //parkUnpark();
                //Semaphore semaphore = new Semaphore(2);
//                semaphore.acquire(1);
//                semaphore.release();
//                semaphore.release();
//                semaphore.acquire(3);
//                System.out.println("ddd");
            }
        }
        static class ReentrantLockPrimary{
            static class TwoThreadPlus implements Runnable{
                static /*volatile*/ int a = 0;//volatile不能保证++操作成为原子性的
                static Lock lock = new ReentrantLock();
                @Override
                public void run() {
                    for (int i = 0 ; i < 10000; i++) {
                        // lock.lock();
                        a++;
                        // lock.unlock();
                    }
                    log.info("result = {}", a);
                }
                public static void main(String[] args) {
                    TwoThreadPlus twoThreadPlus = new TwoThreadPlus();
                    new Thread(twoThreadPlus).start();
                    new Thread(twoThreadPlus).start();
                }
            }
        }
        static class PhaserPrimary{
            static class PhaserBatchProcessorDemo{
                private final List<String> data;

                /**
                 * 一次处理多少数据
                 */
                private final int batchSize;

                /**
                 * 处理的线程数
                 */
                private final int threadCount;
                private final Phaser phaser;
                private final List<String> processedData;

                public PhaserBatchProcessorDemo(List<String> data, int batchSize, int threadCount) {
                    this.data = data;
                    this.batchSize = batchSize;
                    this.threadCount = threadCount;
                    this.phaser = new Phaser(1);
                    this.processedData = new ArrayList<>();
                }

                public void process() {
                    for (int i = 0; i < threadCount; i++) {

                        phaser.register();
                        new Thread(new BatchProcessor(i)).start();
                    }

                    phaser.arriveAndDeregister();
                }

                private class BatchProcessor implements Runnable {

                    private final int threadIndex;

                    public BatchProcessor(int threadIndex) {
                        this.threadIndex = threadIndex;
                    }

                    @Override
                    public void run() {
                        int index = 0;
                        while (true) {
                            // 所有线程都到达这个点之前会阻塞
                            phaser.arriveAndAwaitAdvance();

                            // 从未处理数据中找到一个可以处理的批次
                            List<String> batch = new ArrayList<>();
                            synchronized (data) {
                                while (index < data.size() && batch.size() < batchSize) {
                                    String d = data.get(index);
                                    if (!processedData.contains(d)) {
                                        batch.add(d);
                                        processedData.add(d);
                                    }
                                    index++;
                                }
                            }

                            // 处理数据
                            for (String d : batch) {
                                System.out.println("线程" + threadIndex + "处理数据" + d);
                            }

                            // 所有线程都处理完当前批次之前会阻塞
                            phaser.arriveAndAwaitAdvance();

                            // 所有线程都处理完当前批次并且未处理数据已经处理完之前会阻塞
                            if (batch.isEmpty() || index >= data.size()) {
                                phaser.arriveAndDeregister();
                                break;
                            }
                        }
                    }
                }

                public static void main(String[] args) {
                    //数据准备
                    List<String> data = new ArrayList<>();
                    for (int i = 1; i <= 15; i++) {
                        data.add(String.valueOf(i));
                    }

                    int batchSize = 4;
                    int threadCount = 3;
                    PhaserBatchProcessorDemo processor = new PhaserBatchProcessorDemo(data, batchSize, threadCount);
                    //处理数据
                    processor.process();
                }
            }
            //用公司团建来延时Phaser的用法
            static class OfficeActivityDemo{
                public static void main(String[] args) {
                    final Phaser phaser = new Phaser() {
                        //重写该方法来增加阶段到达动作
                        @Override
                        protected boolean onAdvance(int phase, int registeredParties) {
                            // 参与者数量，去除主线程
                            int staffs = registeredParties - 1;
                            switch (phase) {
                                case 0:
                                    System.out.println("大家都到公司了，出发去公园，人数：" + staffs);
                                    break;
                                case 1:
                                    System.out.println("大家都到公园门口了，出发去餐厅，人数：" + staffs);
                                    break;
                                case 2:
                                    System.out.println("大家都到餐厅了，开始用餐，人数：" + staffs);
                                    break;

                            }
                            // 判断是否只剩下主线程（一个参与者），如果是，则返回true，代表终止
                            return registeredParties == 1;
                        }
                    };

                    // 注册主线程 ———— 让主线程全程参与
                    phaser.register();
                    final StaffTask staffTask = new StaffTask();

                    // 3个全程参与团建的员工
                    for (int i = 0; i < 3; i++) {
                        // 添加任务数
                        phaser.register();
                        new Thread(() -> {
                            try {
                                staffTask.step1Task();
                                //到达后等待其他任务到达
                                phaser.arriveAndAwaitAdvance();

                                staffTask.step2Task();
                                phaser.arriveAndAwaitAdvance();

                                staffTask.step3Task();
                                phaser.arriveAndAwaitAdvance();

                                staffTask.step4Task();
                                // 完成了，注销离开
                                phaser.arriveAndDeregister();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    // 两个不聚餐的员工加入
                    for (int i = 0; i < 2; i++) {
                        phaser.register();
                        new Thread(() -> {
                            try {
                                staffTask.step1Task();
                                phaser.arriveAndAwaitAdvance();

                                staffTask.step2Task();
                                System.out.println("员工【" + Thread.currentThread().getName() + "】回家了");
                                // 完成了，注销离开
                                phaser.arriveAndDeregister();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    while (!phaser.isTerminated()) {
                        int phase = phaser.arriveAndAwaitAdvance();
                        if (phase == 2) {
                            // 到了去餐厅的阶段，又新增4人，参加晚上的聚餐
                            for (int i = 0; i < 4; i++) {
                                phaser.register();
                                new Thread(() -> {
                                    try {
                                        staffTask.step3Task();
                                        phaser.arriveAndAwaitAdvance();

                                        staffTask.step4Task();
                                        // 完成了，注销离开
                                        phaser.arriveAndDeregister();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            }
                        }
                    }
                }

                static final Random random = new Random();

                static class StaffTask {
                    public void step1Task() throws InterruptedException {
                        // 第一阶段：来公司集合
                        String staff = "员工【" + Thread.currentThread().getName() + "】";
                        System.out.println(staff + "从家出发了……");
                        Thread.sleep(random.nextInt(5000));
                        System.out.println(staff + "到达公司");
                    }

                    public void step2Task() throws InterruptedException {
                        // 第二阶段：出发去公园
                        String staff = "员工【" + Thread.currentThread().getName() + "】";
                        System.out.println(staff + "出发去公园玩");
                        Thread.sleep(random.nextInt(5000));
                        System.out.println(staff + "到达公园门口集合");

                    }

                    public void step3Task() throws InterruptedException {
                        // 第三阶段：去餐厅
                        String staff = "员工【" + Thread.currentThread().getName() + "】";
                        System.out.println(staff + "出发去餐厅");
                        Thread.sleep(random.nextInt(5000));
                        System.out.println(staff + "到达餐厅");

                    }

                    public void step4Task() throws InterruptedException {
                        // 第四阶段：就餐
                        String staff = "员工【" + Thread.currentThread().getName() + "】";
                        System.out.println(staff + "开始用餐");
                        Thread.sleep(random.nextInt(5000));
                        System.out.println(staff + "用餐结束，回家");
                    }
                }
            }
        }
        static class ExchangerPrimary{
            static class Producer extends Thread {
                private Exchanger<Integer> exchanger;
                private static int data = 0;
                Producer(String name, Exchanger<Integer> exchanger) {
                    super("Producer-" + name);
                    this.exchanger = exchanger;
                }
                @Override
                public void run() {
                    for (int i=1; i<5; i++) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            data = i;
                            System.out.println(getName()+" 交换前:" + data);
                            data = exchanger.exchange(data);
                            System.out.println(getName()+" 交换后:" + data);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            static class Consumer extends Thread {
                private Exchanger<Integer> exchanger;
                private static int data = 0;
                Consumer(String name, Exchanger<Integer> exchanger) {
                    super("Consumer-" + name);
                    this.exchanger = exchanger;
                }
                @Override
                public void run() {
                    while (true) {
                        data = 0;//consumer一直在拿0跟producer交换
                        System.out.println(getName()+" 交换前:" + data);
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            data = exchanger.exchange(data);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(getName()+" 交换后:" + data);
                    }
                }
            }

            public static void main(String[] args) throws InterruptedException {
                Exchanger<Integer> exchanger = new Exchanger<Integer>();
                new Producer("", exchanger).start();
                new Consumer("", exchanger).start();
                TimeUnit.SECONDS.sleep(7);
                System.exit(-1);
            }
        }
    }

    static class JUCAtomicPractice{

        static void testUpdater(){

        }
        public static void main(String[] args) {
            testUpdater();
        }
    }

}
