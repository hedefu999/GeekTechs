package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AIOResearch {
    static class AIOServer{
        /**
         1613791991333 current thread outer name = main
         1613791995791 current thread = Thread-17
         1613791995791 client remote address = /127.0.0.1:61247
         1613791995796 current thread name = Thread-17
         首长好
         main线程开启新线程Thread-17后会退出，需要让它Sleep等client测试做完再退出
         */
        public static void main(String[] args) throws IOException, InterruptedException {
            final AsynchronousServerSocketChannel serverSocketChannel =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(9000));
            serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override //客户端连接之前，completed方法不会走进来
                public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                    try {
                        System.out.println(System.currentTimeMillis() + "current thread = " + Thread.currentThread().getName());
                        serverSocketChannel.accept(attachment,this);//在此接受客户端连接，缺少此行客户端会连不上
                        System.out.println(System.currentTimeMillis() + "client remote address = "+ socketChannel.getRemoteAddress());
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                System.out.println(System.currentTimeMillis() + "current thread name = " + Thread.currentThread().getName());
                                buffer.flip();//将Buffer从写模式切换到读模式
                                System.out.println(new String(buffer.array(), 0, result));
                                socketChannel.write(ByteBuffer.wrap("同志们辛苦了".getBytes()));
                            }
                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {
                                exc.printStackTrace();
                            }
                        });
                    }catch (IOException e){e.printStackTrace();}
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
            System.out.println(System.currentTimeMillis() + "current thread outer name = "+ Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(20);
        }
    }

    static class AIOClient{
        /**
         1613791995789client connect and send
         1613791995797client receive response from server: 同志们辛苦了
         */
        public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
            System.out.println(System.currentTimeMillis() + "client connect and send");
            socketChannel.connect(new InetSocketAddress("127.0.0.1",9000)).get();
            socketChannel.write(ByteBuffer.wrap("首长好".getBytes()));
            ByteBuffer buffer = ByteBuffer.allocate(512);
            Integer len = socketChannel.read(buffer).get();
            if (len != -1) {
                System.out.println(System.currentTimeMillis() + "client receive response from server: "+ new String(buffer.array(), 0, len));
            }
        }
    }
}
