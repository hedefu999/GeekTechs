package com.netty.primary;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();//创建一个事件循环组
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            System.out.println("netty client start: ");
            //启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            //对未来的关闭通道操作进行监听
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            System.out.println("client发生异常：" + e.getCause());
        }finally {
            group.shutdownGracefully();//清理现场时使用事件循环组进行优雅的关闭
        }
    }
    static class NettyClientHandler extends ChannelInboundHandlerAdapter {
        //客户端链接服务器完成时触发此方法
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf buf = Unpooled.copiedBuffer("HelloServer", CharsetUtil.UTF_8);
            ctx.writeAndFlush(buf);
        }

        //通道channel中有读取事件时会触发，client读，也就是server向client发送数据
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("收到服务端的消息：" + buf.toString(CharsetUtil.UTF_8));
            System.out.println("服务端地址：" + ctx.channel().remoteAddress());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("发生异常：" + cause.getCause());
            ctx.close();
        }
    }
}
