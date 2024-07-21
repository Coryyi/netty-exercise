package com.carol.netty.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannel {
    public static void main(String[] args) {
        // Ч�ʱ��Լ����ļ������������
        try (FileChannel from = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel()
        ) {
            // ��ʼλ�� ��С Ŀ��
            // Ч�ʱ��Լ����ļ������������ �ײ���ò���ϵͳ���㿽���Ż�
            // һ����ഫ 2g������ �������2g��ֻ�ᴫ��2g������ ���Զ�δ���

            //�Ľ�
            long size = from.size();
            // left ʣ������ֽ�
            for (long left = size; left > 0; ){
                left -= from.transferTo((size-left),from.size(),to);// ����ʣ��
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
