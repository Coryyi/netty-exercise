package com.carol.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put((byte) 0x61);//'a'
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{0x62,0x63,0x64});
        ByteBufferUtil.debugAll(buffer);
        // ��ʱ����positionָ���ʱ��һ������д�����ݵĿյ�ַ����ȡ��ȻΪ�գ���Ҫ����flip
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);

        if (buffer.hasRemaining()){
            System.out.println((char)buffer.get());
        }

        buffer.compact();
        ByteBufferUtil.debugAll(buffer);

        buffer.put(new byte[]{0x65,0x66,0x67});
        ByteBufferUtil.debugAll(buffer);
        /*buffer.flip();
        while (buffer.hasRemaining()){
            System.out.println((char)buffer.get());
        }*/


    }
}
