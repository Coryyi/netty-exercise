package com.carol.netty.test;

import com.carol.message.LoginRequestMessage;
import com.carol.message.LoginResponseMessage;
import com.carol.protocol.MessageCodecSharable;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING,CODEC,LOGGING);

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);
    }
}
