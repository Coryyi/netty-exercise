package com.carol.netty.n2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class TestHttp {
    public static void main(String[] args)  {
        NioEventLoopGroup group = new NioEventLoopGroup();


        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast(new HttpServerCodec()); //��ӡ�����Ľ��ʵ�����ǽ����������
                            pipeline.addLast(new SimpleChannelInboundHandler<HttpRequest>() {//������Ϣ���ͼ������� ѡ����
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
                                    log.debug(httpRequest.uri());//httpRequest���������к�����ͷ ����uri������

                                    //������Ӧ  ��Ҫ�����Ӧͷ����
                                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion()/*Э��汾*/, HttpResponseStatus.OK);

                                    byte[] bytes = "<h1>Hello World!</h1>".getBytes();
                                    response.headers().setInt(CONTENT_LENGTH,bytes.length);

                                    response.content().writeBytes("<h1>Hello World!</h1>".getBytes());

                                    //д��channel
                                    ctx.writeAndFlush(response);// д���վ�������� ���ϵ��ô��� Codec�ĳ�վ���������б���
                                }//ֻ����һ���¼�

                            });

                            //��һ������HttpRequest  �ڶ�������HttpContent ������get�����������嵫û������
                            // �Զ��崦��
                            /*pipeline.addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                    // ��ӡ��������ʲô����
                                    log.debug("{}",msg.getClass());
                                    if(msg instanceof HttpRequest){// ������ ������

                                    }else if(msg instanceof HttpContent){

                                    }
                                    //������ʵ�ֲ�̫���� ����Ҫ�Լ��ж�����


                                }
                            });*/
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }

    }
}
