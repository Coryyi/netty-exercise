package com.carol.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 1.创建启动器
        ChannelFuture channelFuture = new Bootstrap()
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
                //5.连接到服务器 connect 异步非阻塞 调用的线程不关心结果，让另一个线程去获取结果，真正做连接操作的是另一个线程
                // 调用connect方法的是主线程，主线程指派另一个线程 上面的 nio 线程，主线程可以继续执行下面的代码
                .connect(new InetSocketAddress("localhost", 8080));
        //2.1
        /*channelFuture.sync();// 带有Future 和Promise 都适合异步方法配套使用 用来处理结果 调用sync时会阻塞，线程暂停直到nio线程连接好

        Channel channel = channelFuture.channel();
        log.debug(channel.toString());
        log.debug("");
                //6.向服务器发送数据
         channel.writeAndFlush("hello world!");*/

         //2.2 使用addListener(回调对象) 将建立连接 方法异步处理结束
        channelFuture.addListener(new ChannelFutureListener() {
            @Override //将回调对象传递给nio线程 连接创建好了就会调用该方法
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                // 此处channelFutute对象同上面的 调用时就是主线程而是nip线程
                Channel channel = channelFuture.channel();;
                log.debug("{}",channel);
                channel.writeAndFlush("hello world");
            }
        });
    }
}
