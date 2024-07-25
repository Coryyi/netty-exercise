package com.carol.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {

    public static void main(String[] args) {
        // 1.服务器端启动器 将下面的组件组装在一起形成服务端并启动 Bootstrap启动
        new ServerBootstrap()
                //2. bossEventLoop WorkerEventLoop(selector,thread) 一个线程+一个selector就是 一个EventLoop loop循环
                .group(new NioEventLoopGroup()) //group组 简单理解 EventLoop包含线程和选择器
                // 3.选择 ServerSocketChannel实现 基于原生封装
                .channel(NioServerSocketChannel.class)
                // 4.与Worker一个意思 将来处理事件分工 boss负责处理连接 worker指这里的child负责读写等具体逻辑 决定了worker（child）将来做哪些操作（handler）
                .childHandler(
                        // 5. channel 代表和客户端进行数据读写的通道 Initializer 初始化 负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //6.添加具体handler
                        nioSocketChannel.pipeline().addLast(new StringDecoder());// 解码 数据传过来是字节形式 将byteBuf转为字符串
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){// 自定义handler
                            @Override // 处理读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //打印上一步转换好的字符串
                                System.out.println(msg);
                            }
                        });

                    }
                })
                .bind(8080);
    }
}
