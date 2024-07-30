package com.carol.netty.n2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

public class TestRedis {

    /*
    set name carol
    星号+数组元素个数
    *3
    $3  三个字节
    set
    $4  四个字节
    name
    $5 五个字节
    carol

    等同aof文件日志
     */
    public static void main(String[] args) throws InterruptedException {
        // 换行
        final byte[] LINE = {13,10};//回车 换行

        NioEventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();
                    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                    pipeline.addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buf = ctx.alloc().buffer();
                            buf.writeBytes("auth 123456".getBytes());
                            buf.writeBytes(LINE);

                            buf.writeBytes("*3".getBytes()); //数组元素个数
                            buf.writeBytes(LINE);

                            buf.writeBytes("$3".getBytes());
                            buf.writeBytes(LINE);

                            buf.writeBytes("set".getBytes());
                            buf.writeBytes(LINE);

                            buf.writeBytes("$4".getBytes());
                            buf.writeBytes(LINE);

                            buf.writeBytes("name".getBytes());
                            buf.writeBytes(LINE);

                            buf.writeBytes("$5".getBytes());
                            buf.writeBytes(LINE);

                            buf.writeBytes("carol".getBytes());
                            buf.writeBytes(LINE);
                            ctx.writeAndFlush(buf);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println("received response...");
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println(buf.toString(Charset.defaultCharset()));
                            super.channelRead(ctx, msg);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost",5253).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }





    }
}
