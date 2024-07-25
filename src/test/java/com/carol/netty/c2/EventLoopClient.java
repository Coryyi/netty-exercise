package com.carol.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 1.����������
        Channel channel = new Bootstrap()
                // 2.������
                .group(new NioEventLoopGroup())
                // 3.ѡ��һ���ͻ��˵�channelʵ��
                .channel(NioSocketChannel.class)
                //4.��Ӵ�����
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override //�����ӽ����󱻵���
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 5.������
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //5.���ӵ�������
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();
        log.debug(channel.toString());
        log.debug("");
                //6.���������������
                //.writeAndFlush("hello world!");
    }
}
