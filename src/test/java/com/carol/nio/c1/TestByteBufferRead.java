package com.carol.nio.c1;

import java.nio.ByteBuffer;

import static com.carol.nio.c1.ByteBufferUtil.debugAll;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});
        buffer.flip();
        //读取结束后 position指向末尾
        buffer.get(new byte[4]);
        debugAll(buffer);


        // rewind从头开始读
        buffer.rewind();
        debugAll(buffer);


        // mark & reset
        // mark 一个标记，记录 position 位置 reset重置到mark位置 增强revent
        System.out.println((char) buffer.get());// a
        System.out.println((char) buffer.get());// b
        buffer.mark();// 当前position指向c 给c加上标记
        System.out.println((char) buffer.get()); // c
        System.out.println((char) buffer.get()); // d
        buffer.reset();// 重置到 c
        System.out.println((char) buffer.get());// c
        System.out.println((char) buffer.get());// d
        // 注意 rewind和flip 都会清楚mark位置


        // get(i) 不会改变指针
        System.out.println((char) buffer.get(3));// d
        debugAll(buffer);
    }
}
