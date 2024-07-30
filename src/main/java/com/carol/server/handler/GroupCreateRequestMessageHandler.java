package com.carol.server.handler;

import com.carol.message.GroupCreateRequestMessage;
import com.carol.message.GroupCreateResponseMessage;
import com.carol.server.session.Group;
import com.carol.server.session.GroupSession;
import com.carol.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupCreateRequestMessage message) throws Exception {
        String groupName = message.getGroupName();
        Set<String> members = message.getMembers();

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if(group == null){
            channelHandlerContext.writeAndFlush(new GroupCreateResponseMessage(true,"创建群聊:"+groupName+"成功"));
            //发送 拉群消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            // 遍历消息集合 发送创建群成功消息
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入群聊:"+groupName));
            }
        }else {
            channelHandlerContext.writeAndFlush(new GroupCreateResponseMessage(false,"创建群聊:"+groupName+"失败"));
        }
    }
}
