package com.concurrency.javaconcurrencyart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class _8ConcurrentPractice {

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
        private ConcurrentHashMap<String, Integer> sheetBankWaterCount = new ConcurrentHashMap<String, Integer>();
        private void count() {
            for (int i = 0; i< 4; i++) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 计算当前sheet的银流数据，计算代码省略
                        sheetBankWaterCount.put(Thread.currentThread().getName(), 1);
                        // 银流计算完成，插入一个屏障
                        try {
                            c.await();
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

    /**
     * 生产者模式和消费者模式
     * 由于生产者和消费者处理能力的不匹配，才存在这种模式
     */
    static class Article{}
    static class TechBlogExtractorV1{
        public List<Article> extractEmail(){return new ArrayList<>();}
        public void addArticleOrComment(Article article){}
        public void cleanEmail(){}
        public void extract(){
            //抽取邮件
            List<Article> articles = extractEmail();
            //添加文章
            for (Article article : articles){
                addArticleOrComment(article);
            }
            //清空邮件
            cleanEmail();
        }
    }
    //多线程地处理博客邮件插入到influence wiki中
    static class TechEmailExtractorV2{
        static class ExchangeEmailShallowDto{}
        static class ArticleBlockingQueue<T> extends ArrayBlockingQueue<T>{
            public ArticleBlockingQueue() {
                super(16);
            }
        }


        public abstract class AbstractExtractor{}

        public class QuickEmailToWikiExtractor extends AbstractExtractor{
            private final Logger log = LoggerFactory.getLogger(QuickEmailToWikiExtractor.class);

            private ThreadPoolExecutor threadPoolExecutor;
            private ArticleBlockingQueue<ExchangeEmailShallowDto> emailQueue;
            public QuickEmailToWikiExtractor(){
                emailQueue = new ArticleBlockingQueue<>();
                int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
                threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                                                            corePoolSize,
                                                            101,
                                                            TimeUnit.SECONDS,
                                                            new LinkedBlockingQueue<>(2000));
            }
            public void extract() throws InterruptedException {
                long start = System.currentTimeMillis();
                //把所有邮件放到队列里
                new ExtractEmailTask().start();
                //将队列里的文章插入到wiki
                insertToWiki();
            }

            private void insertToWiki() throws InterruptedException {
                //登录confluence wiki，每间隔一段时间就需要登录一次
                while (true){
                    ExchangeEmailShallowDto email = emailQueue.poll(2, TimeUnit.SECONDS);
                    if (email == null) break;//两秒取不到队列中的元素就退出
                    //否则就放到线程里交给线程池执行
                    threadPoolExecutor.submit(new InsertIntoWikiTask(email));
                }
            }

            protected List<Article> extractEmail(){
                List<ExchangeEmailShallowDto> allEmails = new ArrayList<>();//getEmailService().queryAllEmails();
                if (allEmails == null){
                    return null;
                }
                for (ExchangeEmailShallowDto shallowDto : allEmails){
                    emailQueue.offer(shallowDto);
                }
                return null;
            }
            public class ExtractEmailTask extends Thread{
                public void run(){
                    extractEmail();
                }
            }
            public class InsertIntoWikiTask extends Thread{
                private ExchangeEmailShallowDto shallowDto;
                public InsertIntoWikiTask(ExchangeEmailShallowDto shallowDto){
                    this.shallowDto = shallowDto;
                }
                public void run() {

                }
            }
        }
    }

    /**
     * 多级生产者消费者
     * 书本代码框架，无法运行
     */

    static class MultiLevelProducerAndConsumer{
        static class Message{}
        //生产者
        public class MsgQueueManager{
            private final BlockingQueue<Message> messageQueue;
            private MsgQueueManager(){
                messageQueue = new LinkedTransferQueue<>();
            }
            public void put(Message msg){
                try {
                    messageQueue.put(msg);
                }catch (Exception e){Thread.currentThread().interrupt();}
            }
            public Message take(){
                try {
                    return messageQueue.take();
                }catch (Exception e){Thread.currentThread().interrupt();}
                return null;
            }
        }
        static class MsgQueueFactory{
            private static LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue();
            public static LinkedBlockingQueue<Message> getMessageQueue(){
                return queue;
            }
        }
        static class SomeInstance{
            private List<LinkedBlockingQueue<Message>> subMsgQueues = new ArrayList<>();
            public BlockingQueue<Message> getSubQueue(){
                int errorCount = 0;
                for (;;) {
                    if (subMsgQueues.isEmpty()) {
                        return null;
                    }
                    //选择哪一个子队列获取消息进行处理所使用的hash算法
                    int index = (int) (System.nanoTime() % subMsgQueues.size());
                    try {
                        return subMsgQueues.get(index);
                    } catch (Exception e) {
                        // 出现错误表示，在获取队列大小之后，队列进行了一次删除操作
                        if ((++errorCount) < 3) {
                            continue;
                        }
                    }
                }
            }
        }

        //消息分发线程，从总队列中取消息分发到子队列
        static class DispatchMessageTask implements Runnable{
            private SomeInstance getInstance(){
                return new SomeInstance();
            }
            @Override
            public void run() {
                BlockingQueue<Message> subQueue;
                for (;;){
                    //如果没有数据就阻塞在此处
                    Message msg = null;
                    try {
                        msg = MsgQueueFactory.getMessageQueue().take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //如果msg为空,则表示没有session机器连接，需要等待
                    while ((subQueue = getInstance().getSubQueue()) == null){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    // 把消息放到小队列里
                    try {
                        subQueue.put(msg);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public static void main(String[] args) {
            LinkedBlockingQueue<Message> messageQueue = MsgQueueFactory.getMessageQueue();
            //向队列里添加消息
        }

    }



}
