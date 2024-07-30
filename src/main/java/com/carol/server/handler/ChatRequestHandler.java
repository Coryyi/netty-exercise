package com.carol.server.handler;

import com.carol.message.ChatRequestMessage;
import com.carol.message.ChatResponseMessage;
import com.carol.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String msgTo = msg.getTo();//消息目的地
        //找到对应channel
        Channel channel = SessionFactory.getSession().getChannel(msgTo);
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false,"用户不在线"));
        }

    }
}
