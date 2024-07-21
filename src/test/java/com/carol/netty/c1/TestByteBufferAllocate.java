package com.carol.netty.c1;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TestByteBufferAllocate {

    public static void main(String[] args) {
        // ByteBuffer.allocate(16);// 不能动态调整
        System.out.println(ByteBuffer.allocate(16).getClass()); // 打印类型
        System.out.println(ByteBuffer.allocateDirect(16).getClass()); // 打印类型
    }
}
