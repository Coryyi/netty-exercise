package com.carol.netty.c1;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TestByteBufferAllocate {

    public static void main(String[] args) {
        // ByteBuffer.allocate(16);// ���ܶ�̬����
        System.out.println(ByteBuffer.allocate(16).getClass()); // ��ӡ����
        System.out.println(ByteBuffer.allocateDirect(16).getClass()); // ��ӡ����
    }
}
