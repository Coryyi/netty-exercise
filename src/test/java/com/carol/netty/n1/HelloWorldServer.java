package com.carol.netty.n1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloWorldServer {
    void start(){

        // 启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // EventLoopGroup
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            ChannelFuture channelFuture = serverBootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    //.option(ChannelOption.SO_RCVBUF,10) // 接收缓冲区 //调整系统的 接收缓冲区窗口
                    //调整 netty 数据缓冲区 ByteBuf  最小16 总是取16整数倍
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            /*pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    //System.out.println("收到消息" + ((ByteBuf) msg).toString());
                                    //ByteBuf buf = (ByteBuf) msg;
                                    //log.debug(buf.toString());
                                     super.channelRead(ctx, msg);
                                }
                            });*/
                        }
                    })
                    .bind(8080);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        HelloWorldServer helloWorldServer = new HelloWorldServer();
        helloWorldServer.start();
    }
}
