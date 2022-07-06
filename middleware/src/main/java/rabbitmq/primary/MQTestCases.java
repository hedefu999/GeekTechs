package rabbitmq.primary;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import org.junit.Test;

import java.io.IOException;


public class MQTestCases {
    /**
     * 生产者端Demo
     */
    @Test
    public void testProducer() throws Exception{
        Connection connection = RabbitMQConnFactory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();

        //创建exchage - 使用完整参数
        channel.exchangeDeclare("exchangeName","direct",true,false,null);
        //创建一个队列 - 持久化、 非排他、非自动删除的
        channel.queueDeclare("queueName",true,false,false,null);
        //绑定上面的exchange与queue
        channel.queueBind("queueName","exchangeName","routingKey");

        //发送一条消息
        channel.basicPublish("exchangeName","routingKey",
                MessageProperties.TEXT_PLAIN, "hello world".getBytes());

        //关闭资源
        // channel.close();
        // connection.close();
    }

    /**
     * 消费者端 Demo
     */
    @Test
    public void testConsumerDemo() throws Exception {
        Address[] addresses = new Address[]{
          new Address("127.0.0.1",5672)
        };
        Connection connection = RabbitMQConnFactory.newConnection();
        final Channel channel = connection.createChannel();
        //设置客户端最多接收未被ack的消息个数
        channel.basicQos(64);
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println("收到消息 - "+message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume("test.queue.0",consumer);

        // channel.close();
        // connection.close();
    }
}
