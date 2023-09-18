package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * NIO non-Blocking IO 非阻塞IO
 */
public class NIOServer {
    static List<SocketChannel> channelList = new ArrayList<>();

    /**
     * NIO可以做到一个线程处理多个连接，而BIO除非是多线程版本才能达到类似效果
     */
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);//NIO支持设置阻塞方式，此处设置为非阻塞
        System.out.println("服务启动成功");
        while (true){ //非阻塞会在while里空转
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null){
                System.out.println("有客户端连接成功");
                socketChannel.configureBlocking(false);
                channelList.add(socketChannel);
            }
            Iterator<SocketChannel> iterator = channelList.iterator();
            while (iterator.hasNext()){ //存在的问题：连接数越多，List越大，此处遍历效率越低下
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                int len = iterator.next().read(byteBuffer);
                if (len > 0){
                    System.out.println("接受到消息："+ new String(byteBuffer.array()));
                }else if (len == -1){
                    iterator.remove();
                    System.out.println("客户端断开连接");
                }
            }
        }
    }
}
