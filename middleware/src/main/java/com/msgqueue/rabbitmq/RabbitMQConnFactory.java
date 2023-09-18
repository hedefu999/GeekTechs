package com.msgqueue.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnFactory {
    private static ConnectionFactory factory = new ConnectionFactory();
    static {
        factory.setHost("0.0.0.0");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("...");
        factory.setPassword("...999");
    }
    public static Connection newConnection(){
        try {
            return factory.newConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
