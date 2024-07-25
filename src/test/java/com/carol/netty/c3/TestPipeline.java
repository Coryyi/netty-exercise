package com.carol.netty.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 添加handler步骤
                        // 1. 通过channel 拿到pipeline 通过创建连接后 拿到的channel拿到pipeline
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        //2.添加处理器到流水线  第一个参数为 名称 head -> h1 -> h2 -> h3 -> h4 -> h5 -> h6 -> tail 双向链表！！！
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                log.debug("1");
                                ByteBuf buf = (ByteBuf) msg;
                                String s = buf.toString();
                                super.channelRead(ctx,s);// 内部调用 ctx.fireChannel 也就是调用下一个handler 并且将处理结果传递给h2
                                //需要靠此方法传递，否则不会执行下一个handler 如果不调用 调用链会断
                            }
                        });//加入到最后 但注意 建立流水线时netty自动添加两个handler head以及tail 头和尾 这里指tail之前
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 获取上一个handler处理的结果
                                // 比如说可以在进行处理 转为对象等等...
                                log.debug("2");
                               // ctx.fireChannelRead(msg); // 2
                                super.channelRead(ctx,msg);
                            }
                        });//加入到最后 但注意 建立流水线时netty自动添加两个handler head以及tail 头和尾 这里指tail之前
                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                log.debug("3");
                                // super.channelRead(ctx,msg);// 这里read唤醒入站处理器，但是后面没有入站处理器了，没有意义
                                nioSocketChannel.writeAndFlush(ctx.alloc().buffer().writeBytes("server4...".getBytes()));

                            }

                        });//加入到最后 但注意 建立流水线时netty自动添加两个handler head以及tail 头和尾 这里指tail之前

                        pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h5",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h6",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("6");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
