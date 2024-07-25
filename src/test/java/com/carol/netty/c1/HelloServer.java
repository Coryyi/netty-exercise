package com.carol.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {

    public static void main(String[] args) {
        // 1.�������������� ������������װ��һ���γɷ���˲����� Bootstrap����
        new ServerBootstrap()
                //2. bossEventLoop WorkerEventLoop(selector,thread) һ���߳�+һ��selector���� һ��EventLoop loopѭ��
                .group(new NioEventLoopGroup()) //group�� ����� EventLoop�����̺߳�ѡ����
                // 3.ѡ�� ServerSocketChannelʵ�� ����ԭ����װ
                .channel(NioServerSocketChannel.class)
                // 4.��Workerһ����˼ ���������¼��ֹ� boss���������� workerָ�����child�����д�Ⱦ����߼� ������worker��child����������Щ������handler��
                .childHandler(
                        // 5. channel ����Ϳͻ��˽������ݶ�д��ͨ�� Initializer ��ʼ�� ������ӱ��handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //6.��Ӿ���handler
                        nioSocketChannel.pipeline().addLast(new StringDecoder());// ���� ���ݴ��������ֽ���ʽ ��byteBufתΪ�ַ���
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){// �Զ���handler
                            @Override // ������¼�
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //��ӡ��һ��ת���õ��ַ���
                                System.out.println(msg);
                            }
                        });

                    }
                })
                .bind(8080);
    }
}
