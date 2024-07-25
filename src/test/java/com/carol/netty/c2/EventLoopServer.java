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
        // ϸ��2 �����ʱ�ϳ��Ĳ���  ����һ��������EventLoopGroup
        EventLoopGroup group = new DefaultEventLoopGroup();// ֻ�ܴ�����ͨ�Ͷ�ʱ����
        new ServerBootstrap()
                // EventLoop�ֹ�ϸ�� ������EventLoop Boss�� Worker ���� һ���ִ���Accept ʣ�µ�read���¼�
                // ϸ��1�� boss ���� ServerSocketChannel �� accept�¼�       worker ֻ���� SocketChannel �ϵĶ�д
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // ���ӵ��ú���
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // �����ʱ�ϳ����¼� ����һʱEventLoopGroup ������������ ֮����read�ľ��Ǵ�group������worker
                        nioSocketChannel.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter(){
                            @Override                                         // ByteBuff
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                // ByteBufת�ַ���ֱ�� toString ���ô�chatSet��
                                log.debug(buf.toString(Charset.defaultCharset()));
                                // handler1 �������˺�Ҫ����handler2����
                                ctx.fireChannelRead(msg);// ����Ϣ���ݸ���һ�� handler  ���������ֱ����handler1ֹͣ�����ᴫ�ݸ�handler2
                            }
                        }).addLast(group, "handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                // ByteBufת�ַ���ֱ�� toString ���ô�chatSet��
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });

                    }
                })
                .bind(8080);
    }
}
