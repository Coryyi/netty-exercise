package com.carol.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 1.创建启动器
        new Bootstrap()
                // 2.添加组件
                .group(new NioEventLoopGroup())
                // 3.选择一个客户端的channel实现
                .channel(NioSocketChannel.class)
                //4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override //在连接建立后被调用
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 5.编码器
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //5.连接到服务器
                .connect(new InetSocketAddress("localhost",8080))
                .sync()
                .channel()
                //6.向服务器发送数据
                .writeAndFlush("hello world!");
    }
}
