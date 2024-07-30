package com.carol.server.handler;

import com.carol.message.LoginRequestMessage;
import com.carol.message.LoginResponseMessage;
import com.carol.server.service.UserServiceFactory;
import com.carol.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class LoginRequestMessageSimpleChannelInboundHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage loginRequestMessage) throws Exception {
        // 读取的已经是对象了
        String username = loginRequestMessage.getUsername();
        String password = loginRequestMessage.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage loginResponseMessage = null;

        if (login) {
            // 保存登录用户名称以及绑定的channel
            SessionFactory.getSession().bind(channelHandlerContext.channel(), username);
            loginResponseMessage = new LoginResponseMessage(true, "登陆成功...");


        } else {
            loginResponseMessage = new LoginResponseMessage(false, "登陆失败，用户名或密码不正确...");
        }
        channelHandlerContext.writeAndFlush(loginResponseMessage);
    }
}
