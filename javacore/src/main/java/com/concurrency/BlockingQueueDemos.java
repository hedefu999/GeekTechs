package com.concurrency;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BlockingQueueDemos {
    static class DelayedMessageQueue{
        //延时消息
        static class DelayMessage implements Delayed {
            //延迟截止时间
            long deadline = System.currentTimeMillis();
            @Getter
            @Setter
            private String message;
            //构造函数初始化 设置延迟执行时间
            public DelayMessage(long delayTime, String message){
                this.deadline = (this.deadline + delayTime);
                this.message = message;
            }
            @Override
            public long getDelay(TimeUnit unit) {
                return unit.convert(deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }
            //队列里元素的排序依据
            @Override
            public int compareTo(Delayed o) {
                if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)){
                    return 1;
                }else if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)){
                    return -1;
                }else {
                    return 0;
                }
            }
            @Override
            public String toString() {
                return this.message;
            }
        }
        static class DelayMessageQueueServer{
            private DelayQueue delayQueue = new DelayQueue();
            public void produce(){
                delayQueue.offer(new DelayMessage(2000,"你好，世界"));
                delayQueue.offer(new DelayMessage(0,"开天辟地"));
            }
            public void consume() throws Exception{
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                log.info("开始时间：{}", dateFormat.format(new Date()));
                while (delayQueue.size() > 0){
                    Delayed message = delayQueue.take();//如果使用poll方法...
                    log.info("消费到消息：{}", message);
                }
                log.info("消费者退出");
            }
        }
        public static void main(String[] args) throws Exception {
            DelayMessageQueueServer server = new DelayMessageQueueServer();
            server.produce();
            server.consume();
        }
    }
}
