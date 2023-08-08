package com.msgqueue.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class RabbitMQPrimary {
    private static final CountDownLatch latch = new CountDownLatch(1);
    public static final String USER_NAME = "guest";
    public static final String PASSWORD = "guest";
    public static final String HOST = "localhost";
    public static final String VIRTUAL_HOST = "/";

    public static final String CHANNEL = "";
    public static final String EXCHANGE_NAME = "hello-exchange";
    public static final String ROUTING_KEY = "testRK";

    static class First{
        static void producer() throws Exception{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(USER_NAME);
            factory.setPassword(PASSWORD);
            factory.setHost(HOST);
            factory.setVirtualHost(VIRTUAL_HOST);
            //建立到Broker的连接
            Connection connection = factory.newConnection();
            //Channel是与RabbitMQ打交道的最重要接口，大部分业务操作都是在Channel中完成的，包括定义queue、定义exchange、queue与exchange的绑定、发布消息等
            Channel channel = connection.createChannel();
            //声明exchange(名称、类型、是否持久化 持久化的在重启时可以恢复)
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            byte[] msgBodyBytes = "message".getBytes();
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, msgBodyBytes);
            channel.close();
            connection.close();
        }
        static void consumer() throws Exception{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(USER_NAME);
            factory.setPassword(PASSWORD);
            factory.setHost(HOST);
            factory.setVirtualHost(VIRTUAL_HOST);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            //声明队列
            String queue = channel.queueDeclare().getQueue();
            //绑定队列
            channel.queueBind(queue, EXCHANGE_NAME, ROUTING_KEY);

            //消费消息
            channel.basicConsume(queue, false, "", new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String routingKey = envelope.getRoutingKey();
                    String contentType = properties.getContentType();
                    System.out.printf("消息的路由键：%s,内容类型：%s\n", routingKey, contentType);
                    System.out.printf("消息内容：%s\n", new String(body));
                    long deliveryTag = envelope.getDeliveryTag();
                    //确认消息
                    channel.basicAck(deliveryTag, false);
                }
            });
            latch.await();
        }
    }
}
