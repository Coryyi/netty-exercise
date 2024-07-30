package com.carol.client;

import com.carol.message.*;
import com.carol.protocol.MessageCodecSharable;
import com.carol.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        // 1.EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        // Handler Log
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        // codec
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // 倒计时锁
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);//减为0就继续向下运行

        AtomicBoolean LOGIN = new AtomicBoolean(false);
        // 启动程序
        Bootstrap bootstrap = new Bootstrap();
        try{
            ChannelFuture channelFuture = bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new ProtocolFrameDecoder());
                            pipeline.addLast(LOGGING_HANDLER);
                            pipeline.addLast(MESSAGE_CODEC);
                            // 3s内没有向服务器写数据，就触发写空闲事件 IdleState
                            pipeline.addLast(new IdleStateHandler(0,3,0));
                            pipeline.addLast(new ChannelDuplexHandler(){
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    //触发写空闲事件
                                    if(event.state() == IdleState.WRITER_IDLE){
                                        //log.debug("3s没发送数据");
                                        // 发送心跳包
                                        ctx.writeAndFlush(new PingMessage());
                                    }
                                }
                            });

                            pipeline.addLast("client handler",new ChannelInboundHandlerAdapter(){
                                // 接收服务器消息

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("msg{}",msg);
                                    if (msg instanceof LoginResponseMessage) {
                                        LoginResponseMessage responseMessage =
                                        (LoginResponseMessage) msg;
                                        if (responseMessage.isSuccess()) {
                                            // 如果登陆成功
                                            LOGIN.set(true);
                                        }
                                        //唤醒system.in线程
                                        WAIT_FOR_LOGIN.countDown();//计数 减 1 就会继续向下运行
                                    }
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 连接建立后触发
                                    new Thread(()->{
                                        // 接收控制台输入
                                        Scanner scanner = new Scanner(System.in);
                                        System.out.println("请输入用户名：");
                                        String username = scanner.nextLine();
                                        System.out.println("请输入密码：");
                                        String pwd = scanner.nextLine();
                                        // 构造消息对象
                                        LoginRequestMessage message = new LoginRequestMessage(username,pwd);
                                        ctx.writeAndFlush(message);
                                        // 入站处理器，写入内容就会触发出站操作，就会当前handler想上找 首先messageCodex转换 然后日志 然后发送出去

                                        System.out.println("等待后续操作...");
                                        try {
                                            WAIT_FOR_LOGIN.await();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }

                                        if (!LOGIN.get()){
                                            // 登陆失败
                                            ctx.channel().close();
                                            return;
                                        }

                                        while (true) {
                                            System.out.println("==================================");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gsend [group name] [content]");
                                            System.out.println("gcreate [group name] [m1,m2,m3...]");
                                            System.out.println("gmembers [group name]");
                                            System.out.println("gjoin [group name]");
                                            System.out.println("gquit [group name]");
                                            System.out.println("quit");
                                            System.out.println("==================================");
                                            String command = scanner.nextLine();
                                            String[] s = command.split(" ");
                                            switch (s[0]){
                                                case "send":
                                                    ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                                    break;
                                                case "gsend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                                    break;
                                                case "gcreate":
                                                    Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                                    set.add(username); // 加入自己
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                                    break;
                                                case "gmembers":
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                                    break;
                                                case "gjoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                                    break;
                                                case "gquit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    return;
                                            }
                                        }

                                    },"system.in").start();
                                    super.channelActive(ctx);
                                }
                            });
                            /*pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf) msg;
                                    log.debug(buf.toString());
                                }
                            });*/
                        }
                    }).connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();//阻塞直到通道关闭

        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
