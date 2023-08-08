package com.msgqueue;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.stomp.StompConnection;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class ActiveMQPrimary {
    public static final String USER_NAME = ActiveMQConnection.DEFAULT_USER;
    public static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    public static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    public static final String QUEUE_TEST = "activemq-queue-test";
    public static final String TOPIC_TEST = "activemq-topic-test";
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * ActiveMQ基于JMS规范的编码风格
     */
    static class ShowClassicJMSFlavorAPI{
        //队列模式
        static void producer(){
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            try {
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                Queue testQueue = session.createQueue(QUEUE_TEST);
                MessageProducer producer = session.createProducer(testQueue);
                TextMessage message = session.createTextMessage("测试点对点消息");
                producer.send(message);
                //提交事务
                session.commit();
                session.close();
                connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        static void consumer(){
            ActiveMQConnectionFactory connFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            try {
                Connection connection = connFactory.createConnection();
                connection.start();
                Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                Queue queue = session.createQueue(QUEUE_TEST);
                MessageConsumer consumer = session.createConsumer(queue);
                consumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        TextMessage textMessage = (TextMessage) message;
                        try {
                            System.out.println(textMessage.getText());
                            session.commit();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                });
                countDownLatch.await();
                session.close();
                connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //主题模式
        static void topicPublisher(){
            ActiveMQConnectionFactory connFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            try {
                Connection connection = connFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                //创建一个主题
                Topic topic = session.createTopic(TOPIC_TEST);
                MessageProducer producer = session.createProducer(topic);
                for (int i = 0; i < 3; i++) {
                    TextMessage message = session.createTextMessage("发送消息" + i);
                    producer.send(topic, message);
                }
                //关闭资源
                session.close();
                connection.close();
            }catch (Exception e){

            }
        }
        static void topicSubscriber() throws Exception{
            ActiveMQConnectionFactory connFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            Connection connection = connFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_TEST);
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.printf("消费者接收到1条消息:%s\n", ((TextMessage)message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            //main线程不能太快退出
            countDownLatch.await();
            //关闭资源
            session.close();
            connection.close();
        }

    }
    //如果使用spring框架接入 ActiveMQ 还需要 引入 spring-jms activemq-pool依赖

    //ActiveMQ服务端发送消息，浏览器端可以通过stomp.js接收消息
    static class ServerSendMsg{
        static void stompProducer() throws Exception{
            StompConnection stompConn = new StompConnection();
            stompConn.open("localhost", 61613);
            stompConn.connect(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD);
            String message = "<a href=\"https://www.baidu.com\" target=\"_black\">购物广告</a>";
            stompConn.send("/topic/shopping-discount", message);

            stompConn.disconnect();
            stompConn.close();
        }
    }

    //ActiveMQ 事务消息
    static class TransactionMQ{
        static void showActiveMQTransactionAPI() throws Exception{
            ActiveMQConnectionFactory connFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            Connection connection = connFactory.createConnection();
            //开启连接
            connection.start();
            //创建会话，开启事务
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Topic topic = session.createTopic(TOPIC_TEST);
            MessageProducer producer = session.createProducer(topic);
            for (int i = 0; i < 10; i++) {
                TextMessage message = session.createTextMessage("发送消息" + i);
                producer.send(topic, message);
                //每发送5次消息提交一次事务
                if (i%5 == 0){
                    session.commit();
                }
            }
            //关闭资源
            session.close();
            connection.close();
        }
    }


    /**
     * JMS 2.0 简化API，接口组成：
     * JMSContext(替换Connection Session)、JMSProducer(替换 MessageProducer)、JMSConsumer(替换 MessageConsumer)
     */
    static class JMS2SimplifiedAPI{
        static void sendJMSMessage11(){
            ConnectionFactory connFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            //connFactory.createContext()搞不定
            //try (JMSContext context = connFactory.createContext();){
            //    context.createProducer().send(queue, text);
            //}catch (Exception e){
            //    e.printStackTrace();
            //}
        }
    }
}
