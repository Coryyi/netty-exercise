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
                        // ���handler����
                        // 1. ͨ��channel �õ�pipeline ͨ���������Ӻ� �õ���channel�õ�pipeline
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        //2.��Ӵ���������ˮ��  ��һ������Ϊ ���� head -> h1 -> h2 -> h3 -> h4 -> h5 -> h6 -> tail ˫����������
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                log.debug("1");
                                ByteBuf buf = (ByteBuf) msg;
                                String s = buf.toString();
                                super.channelRead(ctx,s);// �ڲ����� ctx.fireChannel Ҳ���ǵ�����һ��handler ���ҽ����������ݸ�h2
                                //��Ҫ���˷������ݣ����򲻻�ִ����һ��handler ��������� ���������
                            }
                        });//���뵽��� ��ע�� ������ˮ��ʱnetty�Զ��������handler head�Լ�tail ͷ��β ����ָtail֮ǰ
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // ��ȡ��һ��handler����Ľ��
                                // ����˵�����ڽ��д��� תΪ����ȵ�...
                                log.debug("2");
                               // ctx.fireChannelRead(msg); // 2
                                super.channelRead(ctx,msg);
                            }
                        });//���뵽��� ��ע�� ������ˮ��ʱnetty�Զ��������handler head�Լ�tail ͷ��β ����ָtail֮ǰ
                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                log.debug("3");
                                // super.channelRead(ctx,msg);// ����read������վ�����������Ǻ���û����վ�������ˣ�û������
                                nioSocketChannel.writeAndFlush(ctx.alloc().buffer().writeBytes("server4...".getBytes()));

                            }

                        });//���뵽��� ��ע�� ������ˮ��ʱnetty�Զ��������handler head�Լ�tail ͷ��β ����ָtail֮ǰ

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
