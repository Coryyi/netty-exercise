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
                            pipeline.addLast(new HttpServerCodec()); //打印解码后的结果实际上是解码成两部分
                            pipeline.addLast(new SimpleChannelInboundHandler<HttpRequest>() {//更具消息类型加以区分 选择处理
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
                                    log.debug(httpRequest.uri());//httpRequest代表请求行和请求头 这里uri请求行

                                    //返回响应  需要添加响应头长度
                                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion()/*协议版本*/, HttpResponseStatus.OK);

                                    byte[] bytes = "<h1>Hello World!</h1>".getBytes();
                                    response.headers().setInt(CONTENT_LENGTH,bytes.length);

                                    response.content().writeBytes("<h1>Hello World!</h1>".getBytes());

                                    //写入channel
                                    ctx.writeAndFlush(response);// 写入出站处理器后 向上调用传到 Codec的出站处理器进行编码
                                }//只关心一种事件

                            });

                            //第一部分是HttpRequest  第二部分是HttpContent 请求体get请求有请求体但没有内容
                            // 自定义处理
                            /*pipeline.addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                    // 打印处理结果是什么类型
                                    log.debug("{}",msg.getClass());
                                    if(msg instanceof HttpRequest){// 请求行 请求体

                                    }else if(msg instanceof HttpContent){

                                    }
                                    //但这种实现不太方便 还需要自己判断类型


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
