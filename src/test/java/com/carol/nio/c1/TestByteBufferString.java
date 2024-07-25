package com.carol.nio.c1;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.carol.nio.c1.ByteBufferUtil.debugAll;

public class TestByteBufferString {
    public static void main(String[] args) {
        // 1. 字符串转为 ByteBuffer 网络上发送数据不是直接发送字符串，是将字符串转为ByteBuffer 再将ByteBuffer写到Channel
        // 方法一
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());// 默认操作系统的字符集
        debugAll(buffer);

        // 方法二 Chatset
        //标准字符集
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer1);

        // 方法三 wrap NIO提供的工具类 字节与bytebuffer中的转化
        ByteBuffer buffer2 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer2);


        System.out.println(StandardCharsets.UTF_8.decode(buffer2).toString());
    }
}
