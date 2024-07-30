package com.carol.rpc.client;

import com.carol.message.RpcRequestMessage;
import com.carol.protocol.MessageCodecSharable;
import com.carol.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        //编码器
        MessageCodecSharable CODEC_HANDLER = new MessageCodecSharable();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler();

        try{
            ChannelFuture channelFuture = bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtocolFrameDecoder());
                            pipeline.addLast(CODEC_HANDLER);
                            pipeline.addLast(LOGGING_HANDLER);
                        }
                    }).connect("localhost",8080).sync();
            Channel channel = channelFuture.channel();
            RpcRequestMessage message = new RpcRequestMessage(
                    1,//id
                    "com.carol.server.service.HelloService",//接口
                    "sayHello", // 方法名
                    String.class,//返回类型
                    new Class[]{String.class},//返回类型数组
                    new Object[]{"CAROL"}//参数值
            );

            //writeAndFlush是异步操作，它会将消息写入Channel的出站缓冲区，并刷新缓冲区，触发消息的实际发送，但消息是异步的，会立即返回一个future对象
            ChannelFuture future = channel.writeAndFlush(message);//从下向上找到出站处理器...发送
            future.addListener(promise->{ //添加一个监听器 无论写入成功还是失败都会触发
                if (!promise.isSuccess()) {
                    Throwable cause = promise.cause();
                    log.debug("error",cause);
                }
            });

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            group.shutdownGracefully();
        }


    }



}
