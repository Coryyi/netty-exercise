package com.carol.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.carol.netty.c1.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        // ʹ�� NIO ���������ģʽ �����̴߳���
        // 0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1.����������
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 2.�󶨼����˿�
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // 3.���Ӽ���
        List<SocketChannel> channels = new ArrayList<>();
        while (true){
            // 4.������ͻ��˵����� accept()
            log.debug("connecting...");
            // ���������������߳�ֹͣ���� û�пͻ��˷����������� ���ͻ������ӽ����Ժ�Ż�ָ�����
            SocketChannel socketChannel = serverSocketChannel.accept(); // ���ںͿͻ���֮��Ķ�д���� ͨ��


            log.debug("connected...");
            channels.add(socketChannel);
            for (SocketChannel channel : channels) {
                // 5. ���տͻ��˷��͵�����
                log.debug("before,read... {}",channel);
                channel.read(buffer);// �������� �߳�ֹͣ����
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read... {}",channel);
            }

        }
    }
}
