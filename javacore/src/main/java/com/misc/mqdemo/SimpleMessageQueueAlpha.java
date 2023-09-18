package com.misc.mqdemo;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 最简单的消息队列
 */
public class SimpleMessageQueueAlpha {
    //消息以先进先出的顺序进行消费，适合使用队列实现
    private static Queue<String> queue = new LinkedList<>();

    public static void producer(){
        queue.add("first message");
        queue.add("second message");
        queue.add("third message");
    }
    public static void consumer(){
        while (!queue.isEmpty()){
            System.out.println(queue.poll());//poll 头结点
        }
    }

    public static void main(String[] args) {
        producer();
        consumer();
    }
}
