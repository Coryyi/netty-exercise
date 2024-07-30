package com.carol.netty.c3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;

public class TestCompositeByteBuf {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});
        System.out.println(ByteBufUtil.prettyHexDump(buf1));
        System.out.println(ByteBufUtil.prettyHexDump(buf2));

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(buf1).writeBytes(buf2);
        System.out.println(buf);

        CompositeByteBuf buf4 = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf4.addComponents(true,buf1,buf2);//自动增长写指针
    }
}
