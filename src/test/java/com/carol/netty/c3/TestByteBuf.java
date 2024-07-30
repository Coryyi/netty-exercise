package com.carol.netty.c3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

@Slf4j
public class TestByteBuf {
    //测试channel
    public static void main(String[] args) {
        // 创建ByteBuf
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();//默认256 也可以指定 注意容量可以动态扩容
        //log.debug(buf.toString());
        System.out.println(buf.getClass());
        log(buf);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            stringBuilder.append("a");
        }
        buf.writeBytes(stringBuilder.toString().getBytes());
        log(buf);

    }
    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
