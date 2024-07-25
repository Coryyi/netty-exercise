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
        // 1.����������
        ChannelFuture channelFuture = new Bootstrap()
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
                //5.���ӵ������� connect �첽������ ���õ��̲߳����Ľ��������һ���߳�ȥ��ȡ��������������Ӳ���������һ���߳�
                // ����connect�����������̣߳����߳�ָ����һ���߳� ����� nio �̣߳����߳̿��Լ���ִ������Ĵ���
                .connect(new InetSocketAddress("localhost", 8080));
        //2.1
        /*channelFuture.sync();// ����Future ��Promise ���ʺ��첽��������ʹ�� ���������� ����syncʱ���������߳���ֱͣ��nio�߳����Ӻ�

        Channel channel = channelFuture.channel();
        log.debug(channel.toString());
        log.debug("");
                //6.���������������
         channel.writeAndFlush("hello world!");*/

         //2.2 ʹ��addListener(�ص�����) ���������� �����첽�������
        channelFuture.addListener(new ChannelFutureListener() {
            @Override //���ص����󴫵ݸ�nio�߳� ���Ӵ������˾ͻ���ø÷���
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                // �˴�channelFutute����ͬ����� ����ʱ�������̶߳���nip�߳�
                Channel channel = channelFuture.channel();;
                log.debug("{}",channel);
                channel.writeAndFlush("hello world");
            }
        });
    }
}
