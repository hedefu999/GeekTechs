package com.netty.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * netty的设计里使用了Doug Lea在《Scalable IO in Java》中提出的Reactor模式
 */
public class ReactorPattern {
    //单线程设计模式的Reactor
    static class Reactor implements Runnable{
        final Selector selector;
        final ServerSocketChannel serverSocketChannel;
        public Reactor(int port) throws IOException {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            SelectionKey selectionKey = serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);//注册accept事件
            selectionKey.attach(new Acceptor());//注册AcceptHandler为回调
        }
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){//循环
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        dispatch(iterator.next());//分发事件
                    }
                    selectionKeys.clear();
                }
            }catch (IOException e){e.printStackTrace();}
        }
        void dispatch(SelectionKey next){
            //获取SelectionKey绑定的调用对象
            Runnable attachment = (Runnable) next.attachment();
            if (attachment != null){
                attachment.run();//这个会执行 AcceptHandler#run
            }
        }
        class Acceptor implements Runnable{
            @Override
            public void run() {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = serverSocketChannel.accept();
                    if (socketChannel != null){
                        new AcceptorHandler(selector, socketChannel);
                    }
                } catch (IOException e) {
                    //Lombok中有个@SneakyThrow，放在run方法上就可以不必捕获异常了，会被强转成RuntimeException
                    e.printStackTrace();
                }
            }
        }
        static int MAXIN = 128, MAXOUT = 128;
        static final int READING = 0, SENDING = 1;
        int state = READING;
        class AcceptorHandler implements Runnable{
            final SocketChannel socketChannel;
            final SelectionKey selectionKey;
            ByteBuffer input = ByteBuffer.allocate(MAXIN);
            ByteBuffer output = ByteBuffer.allocate(MAXOUT);
            AcceptorHandler(Selector selector, SocketChannel soktChannel) throws IOException{
                socketChannel = soktChannel;
                socketChannel.configureBlocking(false);
                selectionKey = socketChannel.register(selector, 0);
                selectionKey.attach(this);//将Handler绑定到SelectionKey上
                selectionKey.interestOps(SelectionKey.OP_READ);
                selector.wakeup();
            }
            boolean inputIsComplete(){return true;}
            boolean outputIsComplete(){return true;}
            void process(){}
            void read() throws IOException {
                socketChannel.read(input);
                if (inputIsComplete()){
                    process();
                    state = SENDING;
                    selectionKey.interestOps(SelectionKey.OP_WRITE);//读写的顺序不固定
                }
            }
            void send() throws IOException {
                socketChannel.write(output);
                if (outputIsComplete()){
                    selectionKey.cancel();
                }
            }
            @Override
            public void run() {
                try {
                    switch (state){
                        case READING:
                            read();
                            break;
                        case SENDING:
                            send();
                            break;
                        default:break;
                    }
                }catch (IOException e){e.printStackTrace();}
            }
        }
        //AcceptorHandler可以进一步优化
        //1. 基于GoF状态模式的优化
        // （不再使用if else，而是创建每个状态对应的接口接口实现类）
        //2. 多线程设计模式的优化
        //
        class AcceptorHandlerMultiThreadVersion extends AcceptorHandler{
            ExecutorService poolExecutor = Executors.newFixedThreadPool(3);
            static final int PROCESSING = 3;
            AcceptorHandlerMultiThreadVersion(Selector selector, SocketChannel soktChannel) throws IOException {
                super(selector,soktChannel);
            }
            @Override
            synchronized void read() throws IOException {
                socketChannel.read(input);
                if (inputIsComplete()){
                    state = PROCESSING;
                    poolExecutor.execute(new Processor());
                }
            }
            synchronized void processAndHandOff(){
                process();
                state = SENDING;
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            }
            class Processor implements Runnable{
                @Override
                public void run() {
                    processAndHandOff();
                }
            }
        }
        //3. 多线程模式再加上多个Reactor
        Selector[] selectors;//每个Selector对应一个subReactor线程
    }



}
