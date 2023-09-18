package com.msgqueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByRandom;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RocketMQJunior {
    public static final Logger logger = LoggerFactory.getLogger(RocketMQJunior.class);
    public static final String PRODUCER_GROUP_TEST = "";

    static DefaultMQProducer createProducer() throws Exception{
        //创建消息生产者，并设置消息生产者组
        //无论生产者还是消费者端都必须保证GroupName的唯一性，主要用于分布式事务消息
        DefaultMQProducer mqProducer = new DefaultMQProducer(PRODUCER_GROUP_TEST);
        //指定NameServer地址
        mqProducer.setNamesrvAddr("localhost:9876");
        //初始化Producer，在整个生命周期中只需要初始化一次
        mqProducer.start();
        return mqProducer;
    }

    static void sendMessage(String topic, String tag, String msg) throws Exception{
        DefaultMQProducer producer = createProducer();
        Message message = new Message(topic, tag, msg.getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult result = producer.send(message);
        logger.info("{}", result);
    }

    //通过自定义QueueSelector，可以实现同一featureId的消息发送到固定的队列中
    static void sendMsgWithQueueSelector(String topic, String tag, String msg, Long featureId) throws Exception{
        DefaultMQProducer producer = createProducer();
        Message message = new Message(topic, tag, msg.getBytes(RemotingHelper.DEFAULT_CHARSET));
        //发送延时消息！！！(传入的数字是延迟级别)
        message.setDelayTimeLevel(3);
        MessageQueueSelector queueSelector = new SelectMessageQueueByRandom();
        SendResult send = producer.send(message, queueSelector, featureId);
        logger.info("{}", send);
    }

    static DefaultMQPushConsumer createConsumer(String consumerGroup, String topic) throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        try {
            consumer.setNamesrvAddr("localhost:9876");
            //设置消费者从哪里开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.subscribe(topic, "*");
            //注册消息监听器
            consumer.registerMessageListener(new MessageListenerOrderly() {
                @Override
                public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                    if (list != null){
                        for (MessageExt msgExt : list) {
                            String msgBody = new String(msgExt.getBody());
                            logger.info("receive msg: {}", msgBody);
                        }
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                }
            });
            //设置消费者采用广播模式消费
            consumer.setMessageModel(MessageModel.BROADCASTING);
            return consumer;
        }finally {
            //消费者在使用前必须调用start方法进行初始化
            consumer.start();
        }
    }

    /**
     TransactionMQProducer与普通Producer的区别在于：需要额外调用setTransactionListener方法，并传入TransactionListener实现类
     通过消息Tag的区别对待 模拟事务的提交和回滚
     */
    static TransactionMQProducer getTransactionProducer(String producerGroup) throws Exception{
        TransactionMQProducer txMQProducer = new TransactionMQProducer(producerGroup);
        txMQProducer.setNamesrvAddr("localhost:9876");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2048), new ThreadFactory() {
            AtomicLong counter = new AtomicLong(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("tx-rocketmq-" + counter.getAndIncrement());
                return thread;
            }
        });
        txMQProducer.setExecutorService(threadPoolExecutor);
        txMQProducer.setTransactionListener(new TransactionListener() {
            @Override //执行本地事务，通常是与DB相关的操作
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                logger.info("本地事务执行：消息标签：{}, 消息内容：{}", msg.getTags(), new String(msg.getBody()));
                String tags = msg.getTags();
                if (StringUtils.equals(tags, "Transaction1")){
                    logger.info("模拟本地事务失败的场景");
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override //提供给RocketMQ用于回查本地事务执行结果
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                logger.info("MQ服务调用消息回查接口：消息标签:{},消息内容:{}", msg.getTags(), new String(msg.getBody()));
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        //region RocketMQ容错参数设置

        //消息发送失败的重试次数
        txMQProducer.setRetryTimesWhenSendFailed(3);
        //当消息没有存储成功时，转发到其他broker中
        txMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
        //endregion

        txMQProducer.start();
        return txMQProducer;
    }

    static void tryRocketMQTransactionProducer() throws Exception{
        Message message0 = new Message("TopicTransaction", "Transaction0", "first message0".getBytes());
        Message message1 = new Message("TopicTransaction", "Transaction1", "second message0".getBytes());
        TransactionMQProducer txMQProducer = getTransactionProducer("transaction_producer_group");
        TransactionSendResult sendResult = txMQProducer.sendMessageInTransaction(message0, null);
        TransactionSendResult sendResult1 = txMQProducer.sendMessageInTransaction(message1, null);
        Thread.sleep(20000);
        txMQProducer.shutdown();
    }

    static void tryRocketMQTransactionConsumer() throws Exception{
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer("transaction_consumer_group");
        mqPushConsumer.setNamesrvAddr("localhost:9876");
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe("TopicTransaction","*");
        mqPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            private Random random = new Random();
            @Override //默认消息列表 msgs 中只有一条消息
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    logger.info("接收到消息：tag = {}, 消息内容：{}", msg.getTags(), new String(msg.getBody()));
                    if (msg.getReconsumeTimes() >= 3){
                        //业务上记录重试次数超过限值的消息
                        //saveTryOutMessage(msg);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }else {
                        try {
                            //模拟业务处理
                            TimeUnit.SECONDS.sleep(random.nextInt(5));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            //业务处理消息异常触发重试
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        mqPushConsumer.start();
        System.out.println("消费者已启动");
    }

    //批量发送消息可以提升小消息的发送性能，但一次发送的消息不能超过1MB
    static class BatchMessageSplitter implements Iterator<List<Message>>{
        public final int SIZE_LIMIT = 1000*1000;
        private final List<Message> messages;
        private int currIndex;

        public BatchMessageSplitter(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public boolean hasNext() {
            return currIndex < messages.size();
        }

        @Override
        public List<Message> next() {
            int nextIndex = this.currIndex;
            int totalSize = 0;
            for (Message message : this.messages) {
                Message currMsg = this.messages.get(nextIndex);
                int tmpSize = message.getTopic().length() + message.getBody().length;
                Map<String, String> properties = message.getProperties();
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    tmpSize += entry.getKey().length() + entry.getValue().length();
                }
                tmpSize = tmpSize + 20;
                if (tmpSize > SIZE_LIMIT){
                    if (nextIndex == currIndex){
                        nextIndex ++;
                    }
                    break;
                }
                if (tmpSize + totalSize > SIZE_LIMIT){
                    break;
                }else {
                    totalSize += tmpSize;
                }
            }
            List<Message> subMessages = this.messages.subList(currIndex, nextIndex);
            currIndex = nextIndex;
            return subMessages;
        }

        public static void main(String[] args) throws Exception{
            DefaultMQProducer producer = new DefaultMQProducer("");
            producer.start();
            List<Message> messages = new ArrayList<Message>() {{
                add(new Message("", "", "", "".getBytes()));
                add(new Message("", "", "", "".getBytes()));
                add(new Message("", "", "", "".getBytes()));
            }};
            BatchMessageSplitter msgSplitter = new BatchMessageSplitter(messages);
            while (msgSplitter.hasNext()){
                List<Message> next = msgSplitter.next();
                producer.send(next);
            }
            producer.shutdown();
        }
    }


}
