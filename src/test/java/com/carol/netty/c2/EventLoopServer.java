package com.carol.netty.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        // 细分2 处理耗时较长的操作  创建一个独立的EventLoopGroup
        EventLoopGroup group = new DefaultEventLoopGroup();// 只能处理普通和定时任务
        new ServerBootstrap()
                // EventLoop分工细分 用两组EventLoop Boss和 Worker 两组 一部分处理Accept 剩下的read等事件
                // 细分1： boss 负责 ServerSocketChannel 上 accept事件       worker 只负责 SocketChannel 上的读写
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 连接调用后建立
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 处理耗时较长的事件 参数一时EventLoopGroup 参数二是名字 之后处理read的就是此group而不是worker
                        nioSocketChannel.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter(){
                            @Override                                         // ByteBuff
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                // ByteBuf转字符串直接 toString 调用带chatSet的
                                log.debug(buf.toString(Charset.defaultCharset()));
                                // handler1 处理完了后，要交给handler2处理
                                ctx.fireChannelRead(msg);// 将消息传递给下一个 handler  不调用则会直接在handler1停止，不会传递给handler2
                            }
                        }).addLast(group, "handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                // ByteBuf转字符串直接 toString 调用带chatSet的
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });

                    }
                })
                .bind(8080);
    }
}
