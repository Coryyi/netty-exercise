package com.carol.netty.c1;

import java.nio.ByteBuffer;

import static com.carol.netty.c1.ByteBufferUtil.debugAll;

/**
 * �������ж������ݷ��͸�����ˣ�����֮��ʹ�� \n ���зָ�
 * ������ĳ��ԭ����Щ�����ڽ���ʱ����������������ϣ�����ԭʼ������3��Ϊ
 *
 * - Hello,world\n
 * - I'm zhangsan\n
 * - How are you?\n
 *
 * �������������� byteBuffer (�������)
 *
 * - Hello,world\nI'm zhangsan\nHo
 * - w are you?\n
 */
public class TestByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);

        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());// ģ���ȡ���ݷ���Bytebuffer��
        // ����� ���
        split(source);
        source.put("w are you?\nhaha!\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer buffer){
        buffer.flip();// �л���ģʽ
        for (int i = 0; i < buffer.limit(); i++) {

            if (buffer.get(i)== '\n') {
                // ��ǰi�ǻ��з�λ�� +1 ��ͷ���˵��ܳ��ȣ�position��ָ��λ�ã�Ҳ���Ǹð��׸��ַ������� Ҳ��ͷ���Ǹð��ֽ��ܳ���
                int len = i+1-buffer.position();
                // �ǻ��з�
                // ��������Ϣ�����µ�ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(buffer.get());
                }
                debugAll(target);
            }
        }
        buffer.compact();// �����������
    }

}
