package com.carol.rpc.server;

import com.carol.protocol.MessageCodecSharable;
import com.carol.protocol.ProtocolFrameDecoder;
import com.carol.server.handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RpcServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable CODEC_HANDLER = new MessageCodecSharable();

        RpcRequestMessageHandler RPC_HANDLER = new RpcRequestMessageHandler();

        ServerBootstrap bootstrap = new ServerBootstrap();
        try{
            ChannelFuture channelFuture = bootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtocolFrameDecoder());//处理半包粘包
                            pipeline.addLast(CODEC_HANDLER);
                            pipeline.addLast(LOGGING_HANDLER);
                            pipeline.addLast(RPC_HANDLER);
                        }
                    }).bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }
}
