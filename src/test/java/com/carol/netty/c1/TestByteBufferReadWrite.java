package com.carol.netty.c1;

import java.nio.ByteBuffer;

import static com.carol.netty.c1.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put((byte) 0x61);//'a'
        debugAll(buffer);
        buffer.put(new byte[]{0x62,0x63,0x64});
        debugAll(buffer);
        // ��ʱ����positionָ���ʱ��һ������д�����ݵĿյ�ַ����ȡ��ȻΪ�գ���Ҫ����flip
        buffer.flip();
        debugAll(buffer);

        if (buffer.hasRemaining()){
            System.out.println((char)buffer.get());
        }

        buffer.compact();
        debugAll(buffer);

        buffer.put(new byte[]{0x65,0x66,0x67});
        debugAll(buffer);
        /*buffer.flip();
        while (buffer.hasRemaining()){
            System.out.println((char)buffer.get());
        }*/


    }
}
