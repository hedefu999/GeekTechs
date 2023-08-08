package com.concurrency.javaconcurrencyart;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class _06ConcurrentFramework {

    static class DeadCycleHashMap{

        public static void main(String[] args) throws InterruptedException {
            final HashMap<String, String> map = new HashMap<>(2);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int j = 0; j < 10; j++) {
                                    map.put(UUID.randomUUID().toString(), ""+j);
                                }
                            }
                        }, "ftf" + i).start();
                    }
                }
            }, "ftf");
            thread.start();
            thread.join();
        }
    }

    /**
     * ForkJoin的简单应用: 计算 1+2+3+...+100
     * 带有结果的任务，需要使用RecursiveTask
     */
    static class ForkJoinCountTask{
        static class CountTask extends RecursiveTask<Integer> {
            private static final int THRESHOLD = 10;//阈值
            private int start;
            private int end;
            public CountTask(int start, int end){
                this.start = start;
                this.end = end;
            }
            @Override
            protected Integer compute() {
                int sum = 0;
                //任务足够小就开始计算任务
                boolean canCompute = (end - start) <= THRESHOLD;
                if (canCompute){
                    for (int i = start; i <= end; i++) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {}
                        sum += i;
                    }
                } else {
                    //如果任务大于阈值，就分裂成两个子任务
                    int middle = (start + end)/2;
                    CountTask leftTask = new CountTask(start, middle);
                    CountTask rightTask = new CountTask(middle + 1, end);
                    //执行子任务
                    leftTask.fork();
                    rightTask.fork();
                    //等待子任务执行完，并得到结果
                    int leftResult = leftTask.join();//这里会有等待
                    int rigthResult = rightTask.join();
                    //合并子任务
                    sum = leftResult + rigthResult;
                }
                return sum;
            }
        }
        public static void main(String[] args) {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            //生成一个
            CountTask task = new CountTask(1,100);
            //执行一个任务
            Future<Integer> result = forkJoinPool.submit(task);
            try {
                System.out.println(System.currentTimeMillis());
                System.out.println(result.get());
                System.out.println(System.currentTimeMillis());
            }catch (Exception e){}
            /**
             * 上述两个时间戳之间相差略大于1秒， 100ms * 10
             */
        }
    }

    /**
     * 十三原子类试验
     */
    @Data
    @AllArgsConstructor
    static class User{
        private String name;
        //AtomicIntegerFieldUpdater类将更新的字段需要声明为 public volatile 的
        public volatile Integer age;
    }
    @Test
    public void test34(){
        int[] value = new int[]{1,2,3,4,6};
        AtomicIntegerArray integerArray = new AtomicIntegerArray(value);
        integerArray.getAndSet(0,5);//index为0的元素设置为5
        integerArray.addAndGet(2,3);//index为2的元素加3
        System.out.println(Arrays.toString(value));
        System.out.println(integerArray);
        /*
         * 注意interArray的操作不会改变原数组integerArray
         * [1, 2, 3, 4, 6]
         * [5, 2, 6, 4, 6]
         */
        AtomicReference<Properties> atomicReference = new AtomicReference<>();
        Properties props = new Properties();props.setProperty("user","root");
        atomicReference.set(props);
        Properties props2 = new Properties();props.setProperty("user","dev01");
        atomicReference.compareAndSet(props, props2);

        //整型字段更新
        AtomicIntegerFieldUpdater<User> aUser = AtomicIntegerFieldUpdater.newUpdater(User.class, "age");
        User user = new User("jack",12);
        System.out.println(aUser.getAndIncrement(user));
        System.out.println(aUser.get(user));
    }

    static class CountDownLatchDemo{
        static class PreDemo{
            public static void main(String[] args) throws InterruptedException {
                Thread parser1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                Thread parser2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("parser2 finish");
                    }
                });
                parser1.start();
                parser2.start();
                parser1.join();
                parser2.join();
                System.out.println("all parser finish");
            }
        }
        static class PractDemo{
            static CountDownLatch cdl = new CountDownLatch(2);

            public static void main(String[] args) throws InterruptedException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(1);
                        cdl.countDown();
                        System.out.println(2);
                        cdl.countDown();
                    }
                }).start();
                cdl.await();
                System.out.println("3");
            }
        }
    }

    static class CyclicBarrierDemo{
        static  class CyclicBarrierTest {
            static CyclicBarrier c = new CyclicBarrier(2);
            public static void main(String[] args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            c.await();
                        } catch (Exception e) {
                        }
                        System.out.println(1);
                    }
                }).start();
                try {
                    c.await();
                } catch (Exception e) {
                }
                System.out.println(2);
            }
            /*
             * 输出 1 2 或 2 1
             */
        }
        static class ComplicateCBTest{
            //CyclicBarrier在所有线程到达屏障时优先执行barrierAction
            static CyclicBarrier c = new CyclicBarrier(2, new A());
            public static void main(String[] args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            c.await();
                        } catch (Exception e) {
                        }
                        System.out.println(1);
                    }
                }).start();
                try {
                    c.await();
                } catch (Exception e) {
                }
                System.out.println(2);
            }
            static class A implements Runnable {
                @Override
                public void run() {
                    System.out.println(3);
                }
            }
        }

        /**
         * CyclicBarrier的一个应用场景：
         * 多线程计算数据，最后合并计算结果
         * 一个excel保存了用户所有银行流水，每个sheet保存账户近一年的每笔银行流水。统计用户的日均银行流水
         */
        static class CyclicBarrierPractDemo{
            static class BankWaterService implements Runnable {
                /**
                 * 创建4个屏障，处理完之后执行当前类的run方法
                 */
                private CyclicBarrier c = new CyclicBarrier(4, this);
                /**
                 * 假设只有4个sheet，所以只启动4个线程
                 */
                private Executor executor = Executors.newFixedThreadPool(4);
                /**
                 * 保存每个sheet计算出的银流结果
                 */
                private ConcurrentHashMap<String, Integer> sheetBankWaterCount = new ConcurrentHashMap<>();

                private void count() {
                    for (int i = 0; i < 4; i++) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                // 计算当前sheet的银流数据，计算代码省略
                                sheetBankWaterCount.put(Thread.currentThread().getName(), 1);
                                // 银流计算完成，插入一个屏障
                                try {
                                    c.await();//c.reset();
                                } catch (InterruptedException |
                                        BrokenBarrierException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                @Override
                public void run() {
                    int result = 0;
                    // 汇总每个sheet计算出的结果
                    for (Map.Entry<String, Integer> sheet : sheetBankWaterCount.entrySet()) {
                        result += sheet.getValue();
                    }
                    // 将结果输出
                    sheetBankWaterCount.put("result", result);
                    System.out.println(result);
                }

                public static void main(String[] args) {
                    BankWaterService bankWaterCount = new BankWaterService();
                    bankWaterCount.count();
                }
            }
        }

        static class CyclicBarrierExtendTest{
            static CyclicBarrier c = new CyclicBarrier(2);
            public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            c.await();
                        } catch (Exception e) {
                        }
                    }
                });
                thread.start();
                thread.interrupt();
                try {
                    c.await();
                } catch (Exception e) {
                    System.out.println(c.isBroken());
                }
            }
        }

        /**
         * 某个线程在await期间被设置中断位，将导致CyclicBarrier broken
         */
        static class ShowCyclicBarrierIsBrokenUsage{
            static CyclicBarrier c = new CyclicBarrier(2);
            public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            c.await();
                        } catch (Exception e) { }
                    }
                });
                thread.start();
                thread.interrupt();
                try {
                    c.await();
                } catch (Exception e) {
                    System.out.println(c.isBroken());//true
                }
            }
        }
    }

    static class SemaphoreDemo{
        static class SemaphoreSimpleDemo{
            private static final int THREAD_COUNT = 5;
            private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
            private static Semaphore s = new Semaphore(3);
            public static void main(String[] args) {
                for (int i = 0; i< THREAD_COUNT; i++) {
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                s.acquire();
                                System.out.println("save data");
                                s.release();
                            } catch (InterruptedException e) {
                            }
                        }
                    });
                }
                //int s.availablePermits(); 信号量中当前可用的许可证数
                //int s.getQueueLength(); 返回正在等待获取许可证的线程数
                //boolean s.hasQueuedThreads(); 是否有线程正在等待获取许可证
                //下面两个是Semaphore的proected方法，调不了
                //void s.reducePremits(xxx);
                //Collection s.getQueuedThreads();
                threadPool.shutdown();
            }
        }
    }

    static class ExchangerDemo{
        static class ExchangerTest{
            private static final Exchanger<String> exgr = new Exchanger<String>();
            private static ExecutorService threadPool = Executors.newFixedThreadPool(3);
            public static void main(String[] args) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String A = "银行流水A";// A录入银行流水数据
                            String other = exgr.exchange(A);
                            System.out.println("A - 其他人录入的银行流水是：" + other);
                        } catch (InterruptedException e) { }
                    }
                });
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String B = "银行流水B";// B录入银行流水数据
                            String other = exgr.exchange(B);
                            System.out.println("B - 其他人录入的内容是：" + other);
                        } catch (InterruptedException e) { }
                    }
                });
                //exchager貌似要成对的线程使用，threadPool如果execute 3个线程会无限等待
                //使用带超时参数的 exchage(T data, long timeout, TimeUnit unit)可以防止死掉，不过会抛出异常
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String C = "银行流水C";// B录入银行流水数据
                            //String other = exgr.exchange(C);
                            String other = exgr.exchange(C, 2, TimeUnit.SECONDS);
                            System.out.println("C - 其他人录入的内容是：" + other);
                        } catch (InterruptedException e) { } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                });
                //threadPool.execute(new Runnable() {
                //    @Override
                //    public void run() {
                //        try {
                //            String D = "银行流水D";// B录入银行流水数据
                //            String other = exgr.exchange(D);
                //            System.out.println("D - 其他人录入的内容是：" + other);
                //        } catch (InterruptedException e) { }
                //    }
                //});
                threadPool.shutdown();
            }
        }
    }

}
