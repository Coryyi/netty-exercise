package com.carol.server;

import com.carol.message.GroupMembersRequestMessage;
import com.carol.protocol.MessageCodecSharable;
import com.carol.protocol.ProtocolFrameDecoder;
import com.carol.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoginRequestMessageSimpleChannelInboundHandler LOGIN_HANDLER = new LoginRequestMessageSimpleChannelInboundHandler();


        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        GroupMembersRequestMessageHandler GROUP_MEMBERS_REQUEST_HANDLER = new GroupMembersRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();


        ServerBootstrap bootstrap = new ServerBootstrap();

        try{
            ChannelFuture channelFuture = bootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new ProtocolFrameDecoder());
                            pipeline.addLast(LOGGING_HANDLER);
                            pipeline.addLast(MESSAGE_CODEC);
                            // 用于判断 读空闲时间过长 或 写空闲时间过长 读空闲5s 其他不关注写0
                            // 5秒内没有收到channel的数据就会触发该事件
                            pipeline.addLast(new IdleStateHandler(5,0,0));

                            //可以同时作为入站和出站事件
                            pipeline.addLast(new ChannelDuplexHandler(){
                                @Override //用户触发特殊事件
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    //读空闲
                                    if(event.state()== IdleState.READER_IDLE){
                                        //log.debug("读空闲超过 5s");
                                        ctx.channel().close();
                                    }
                                }
                            });


                            pipeline.addLast(LOGIN_HANDLER);
                            pipeline.addLast(GROUP_CHAT_HANDLER);
                            pipeline.addLast(GROUP_CREATE_HANDLER);
                            pipeline.addLast(GROUP_JOIN_HANDLER);
                            pipeline.addLast(GROUP_QUIT_HANDLER);
                            pipeline.addLast(GROUP_MEMBERS_REQUEST_HANDLER);

                            pipeline.addLast(QUIT_HANDLER);
                        }
                    }).bind(8080);
            //第一个sync是因为bind是异步操作返回一个future 阻塞直到绑定完成，完成后才会能获取channel
            //第二个是针对连接关闭的阻塞 在连接关闭前一直等待 直到服务器关闭
            channelFuture.sync().channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }


}
