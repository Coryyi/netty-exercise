package com.carol.nio.c1;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.carol.nio.c1.ByteBufferUtil.debugAll;

public class TestByteBufferString {
    public static void main(String[] args) {
        // 1. �ַ���תΪ ByteBuffer �����Ϸ������ݲ���ֱ�ӷ����ַ������ǽ��ַ���תΪByteBuffer �ٽ�ByteBufferд��Channel
        // ����һ
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());// Ĭ�ϲ���ϵͳ���ַ���
        debugAll(buffer);

        // ������ Chatset
        //��׼�ַ���
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer1);

        // ������ wrap NIO�ṩ�Ĺ����� �ֽ���bytebuffer�е�ת��
        ByteBuffer buffer2 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer2);


        System.out.println(StandardCharsets.UTF_8.decode(buffer2).toString());
    }
}
