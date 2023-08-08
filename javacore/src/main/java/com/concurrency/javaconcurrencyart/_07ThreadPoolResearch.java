package com.concurrency.javaconcurrencyart;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class _07ThreadPoolResearch {

    /**
     * 使用FutureTask实现一个多线程场景：
     * 多个线程试图执行同一个同一时刻只能由一个线程执行，并且只能执行一次的任务，其他未执行过的线程需要等待正在执行此任务的线程
     */
    static class OneFutureTaskExecutedByMultiThreads{
        private static final Logger log = LoggerFactory.getLogger(OneFutureTaskExecutedByMultiThreads.class);

        static FutureTask<String> futureTask = new FutureTask<>(new Callable<String>(){
            @Override
            public String call() throws Exception {
                log.info("{}调用call方法",Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(2);
                return Math.random()*10+"";
            }
        });
        static Runnable runnable = new Runnable() {
            @Override
            public void run() {
                futureTask.run();
                try {
                    log.info("{}想获取执行结果@{}", Thread.currentThread().getName(), System.currentTimeMillis());
                    String s = futureTask.get();
                    log.info("{}获取到执行结果:{} @ {}",Thread.currentThread().getName(), s, System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        public static void main(String[] args) {
            new Thread(runnable).start();
            new Thread(runnable).start();
            new Thread(runnable).start();
            new Thread(runnable).start();
        }
        /*
         * Thread-3想获取执行结果@1596253400306
         * Thread-1想获取执行结果@1596253400306
         * Thread-2想获取执行结果@1596253400306
         * Thread-0调用call方法
         * Thread-0想获取执行结果@1596253402316
         * Thread-2获取到执行结果:6.842852512046525 @ 1596253402316
         * Thread-3获取到执行结果:6.842852512046525 @ 1596253402316
         * Thread-0获取到执行结果:6.842852512046525 @ 1596253402316
         * Thread-1获取到执行结果:6.842852512046525 @ 1596253402316
         *
         * 总结：多个线程争抢执行FutureTask时存在竞争关系，抢到的任务的要耗时执行，没抢到任务的会跳过run任务继续向下走，但会被阻塞在get上
         * 总而言之，多个线程执行一个FutureTask的run get，每个线程的耗时相同，而这个耗时取决于抢到任务的那个线程执行任务所花时间。
         *
         * 疑问：总是Thread-0抢到任务，但进入call方法的速度并不比其他线程进入get方法前的日志快，很奇怪
         */
    }

    /**
     * 创建线程池需要传入哪些参数？
     * new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, milliseconds, runnableTaskQueue, handler)
     */
    static class ThreadPoolPrimary{
        public static void main1() {
            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("XX-task-%d").build();

            ExecutorService zeroCoreOneMinuteThreadLiveSyncQueue = Executors.newCachedThreadPool(threadFactory);
            ExecutorService specifiedSameCoreAndMaxCountLinkBlockQueue = Executors.newFixedThreadPool(4, threadFactory);
            ExecutorService oneCoreAndMaxCountLinkBQueue = Executors.newSingleThreadExecutor(threadFactory);
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4, threadFactory);
        }

        public static void main(String[] args) throws InterruptedException {
            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("MyThreadPool - thread - %d").build();
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
                    2,
                    0,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(2),
                    threadFactory,
                    new ThreadPoolExecutor.DiscardPolicy());
            Runnable runable = new Runnable(){
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName());
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadPoolExecutor.execute(new Thread(runable));
            threadPoolExecutor.execute(new Thread(runable));
            System.out.println(System.currentTimeMillis());
            TimeUnit.SECONDS.sleep(2);
            threadPoolExecutor.execute(new Thread(runable));
            threadPoolExecutor.execute(new Thread(runable));
            System.out.println(System.currentTimeMillis());
            threadPoolExecutor.execute(new Thread(runable));
            System.out.println(System.currentTimeMillis());
            System.out.println(Runtime.getRuntime().availableProcessors());
            /**
             * AbortPolicy的效果：直接抛出异常，有一个提交的任务没有执行
             *
             * MyThreadPool - thread - 0
             * MyThreadPool - thread - 1
             * Exception in thread "main" java.util.concurrent.RejectedExecutionException: Task Thread[Thread-4,5,main] rejected from java.util.concurrent.ThreadPoolExecutor@66a29884[Running, pool size = 2, active threads = 2, queued tasks = 2, completed tasks = 0]
             * 	at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2063)
             * 	at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:830)
             * 	at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1379)
             * 	at com.concurrency.javaconcurrencyart._07ThreadPoolResearch$ThreadPoolPrimary.main(_07ThreadPoolResearch.java:105)
             * MyThreadPool - thread - 0
             * MyThreadPool - thread - 1
             *
             * CallerRunsPolicy的效果：谁提交了任务谁执行，则main方法要在自己执行完向线程池提交的一个被拒绝的任务才能退出
             * DiscardPolicy的效果，最后的submite/execute就像没执行一样
             *
             */
        }

    }

}
