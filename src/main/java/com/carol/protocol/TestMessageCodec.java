package com.carol.protocol;

import com.carol.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoggingHandler LOGIN_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        EmbeddedChannel channel= new EmbeddedChannel(
                // 需要最大长度 长度字段偏移量
                new ProtocolFrameDecoder(),
                LOGIN_HANDLER,
                MESSAGE_CODEC);

        LoginRequestMessage message = new LoginRequestMessage("carol", "123");
        channel.writeOutbound(message);//出站


        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,byteBuf);
        //channel.writeInbound(byteBuf);//入站


        ByteBuf s1 = byteBuf.slice(0, 100);
        ByteBuf s2 = byteBuf.slice(100, byteBuf.readableBytes() - 100);

        byteBuf.retain();//引用计数+1
        channel.writeOutbound(s1);//会自动调用release
        channel.writeOutbound(s2);

    }

}
