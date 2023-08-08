package com.netty.primary;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyServer {
    public static void main(String[] args) {
        //创建两个线程组bossGroup和workerGroup,含有的子线程NioEventLoop的个数默认为cpu核心数的两倍
        //bossGroup只处理连接请求，workerGroup处理真正的客户端业务
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            //创建服务器端的启动对象
                //设置两个线程组
            bootstrap.group(bossGroup,workerGroup)
                    //使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的，同一时间只能处理一个客户端连接
                    //多个客户端同时连接时，服务端将不能处理的客户端连接请求放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //对workerGroup的SocketChannel设置处理器，自定义的Handler需要继承netty规定好的某个HandlerAdapter
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("netty server start...");
            //绑定一个端口并且同步，生成一个ChannelFuture异步对象，通过isDone()方法判断异步事件的执行情况
            //启动服务器（并绑定端口），bind是异步操作，sync方法是等待异步操作执行完毕
            //bind中有一个关键方法调用 initAndRegister ReflectiveChannelFactory
            ChannelFuture channelFuture = bootstrap.bind(9000).sync();
            //向ChannelFuture中注册监听器
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()){
                        System.out.println("监听9000端口成功");
                    }else {
                        System.out.println("监听9000端口失败");
                    }
                }
            });
            //对通道关闭并进行监听，closeFuture是异步操作，监听通道的关闭。通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            System.out.println("发生异常：" + e.getCause());
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static class NettyServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("客户端连接");
        }

        /**
         * 读取客户端发送的数据
         * @param ctx 上下文对象，含通道channel、管道pipeline
         * @param msg 客户端发送的数据
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("服务器读取线程：" + Thread.currentThread().getName());
            Channel channel = ctx.channel();
            ChannelPipeline pipeline = ctx.pipeline();//本质是一个双向链表，出栈入栈
            //将msg转成一个ByteBuf，类似NIO的ByteBuffer
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("客户端发送消息是："+ buf.toString(CharsetUtil.UTF_8));
        }
        //数据读取完毕的处理方法
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ByteBuf buf = Unpooled.copiedBuffer("HelloClient",CharsetUtil.UTF_8);
            ctx.writeAndFlush(buf);
        }
        //异常发生时清理现场，一般是关闭通道
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
            //ctx.channel().close();
        }
    }
}
