package com.carol.nio.c1;

import java.nio.ByteBuffer;

import static com.carol.nio.c1.ByteBufferUtil.debugAll;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});
        buffer.flip();
        //��ȡ������ positionָ��ĩβ
        buffer.get(new byte[4]);
        debugAll(buffer);


        // rewind��ͷ��ʼ��
        buffer.rewind();
        debugAll(buffer);


        // mark & reset
        // mark һ����ǣ���¼ position λ�� reset���õ�markλ�� ��ǿrevent
        System.out.println((char) buffer.get());// a
        System.out.println((char) buffer.get());// b
        buffer.mark();// ��ǰpositionָ��c ��c���ϱ��
        System.out.println((char) buffer.get()); // c
        System.out.println((char) buffer.get()); // d
        buffer.reset();// ���õ� c
        System.out.println((char) buffer.get());// c
        System.out.println((char) buffer.get());// d
        // ע�� rewind��flip �������markλ��


        // get(i) ����ı�ָ��
        System.out.println((char) buffer.get(3));// d
        debugAll(buffer);
    }
}
