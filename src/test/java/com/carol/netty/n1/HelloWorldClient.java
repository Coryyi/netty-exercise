package com.carol.netty.n1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloWorldClient {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            send();
        }
        System.out.println("finish");

    }

    private static void send() {
        Bootstrap bootstrap = new Bootstrap();

        NioEventLoopGroup group = new NioEventLoopGroup();

        try{
            ChannelFuture channelFuture = bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            //pipeline.addLast(new FixedLengthFrameDecoder(10));//定长处理器
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                // 在channel连接建立好时触发
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                    /*for (int i = 0; i < 10; i++) {
                                        ByteBuf buf = ctx.alloc().buffer(16);// 容量16
                                        buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                        ctx.writeAndFlush(buf);

                                    }*/

                                    ByteBuf buf = ctx.alloc().buffer(16);// 容量16
                                    buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,16,17,18});
                                    ctx.writeAndFlush(buf);

                                    // 短链接 发送一次就断开连接
                                    ctx.channel().close();
                                }


                            });
                        }
                    })
                    .connect("localhost", 8080);
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){

        }finally {
            group.shutdownGracefully();
        }
    }
}
