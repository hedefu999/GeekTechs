package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOSelectServer {
    /**
     在NIOServer中，提到socketchannel遍历操作在连接非常多的情况下非常低效
     希望只遍历有IO操作的client，来处理其发送的数据
     所以有NIOSelectServer作为改进方案
     */
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);
        //打开selector处理channel，即创建epoll  Selector-多路复用器，内部有一个就绪事件列表rdlist
        Selector selector = Selector.open();
        //把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
        //这样selector就能收集有IO事件的连接了
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功");
        while (true){
            //阻塞等待需要处理的事件发生
            selector.select(); //这一行是一个阻塞方法


            //获取selector中注册的全部事件的SelectionKey实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            //遍历SelectionKey对事件进行处理
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //如果是OP_ACCEPT事件，则进行连接获取和事件注册
                if (key.isAcceptable()){
                    //通过key获取发起连接的服务端channel
                    ServerSocketChannel serverSktChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSktChannel.accept();
                    socketChannel.configureBlocking(false);
                    //客户端连接也需要注册到selector上，注册读事件，这样客户端的读事件就可以被selector感知到并进行收集
                    SelectionKey selKey = socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                }else
                // 如果是OP_READ事件，则进行读取和打印
                if (key.isReadable()){
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = socketChannel.read(byteBuffer);
                    if (len > 0){
                        System.out.println("接收到消息："+ new String(byteBuffer.array()));
                    }else if (len == -1){
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }
                //从事件集合里删除本次处理的key，防止下次select重复处理
                iterator.remove();
            }
        }
    }
}
