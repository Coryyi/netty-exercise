package com.carol.nio.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.carol.nio.c1.ByteBufferUtil.debugAll;

@Slf4j
public class AioFileChannel {
    public static void main(String[] args) {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            //����һ byteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(16);
            //������ ��ȡ����ʼλ��

            //������ ����
            // ������ �ص�����
            log.debug("read begin...");
            channel.read(buffer,0,buffer,new CompletionHandler<Integer,ByteBuffer>(){
                // read�ɹ�
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    // ʵ���ֽ���  ����
                    log.debug("read completed...");
                    attachment.flip();
                    debugAll(attachment);
                }
                // readʧ��
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.debug("read end...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
