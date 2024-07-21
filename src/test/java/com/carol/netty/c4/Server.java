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
        // ʹ�� NIO ������
        // 0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1.����������
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //���÷�����ģʽ ��Ӱ��accept����
        serverSocketChannel.configureBlocking(false);

        // 2.�󶨼����˿�
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // 3.���Ӽ���
        List<SocketChannel> channels = new ArrayList<>();
        while (true){
            // 4.������ͻ��˵����� accept()
            //log.debug("connecting...");
            // ʹ��configureBlocking����Ϊ������ �̻߳����ִ�� ���acceptû�����ӽ��� ���صľ���nullֵ
            // ��while true�оͻ�һֱѭ��ִ��
            SocketChannel socketChannel = serverSocketChannel.accept(); // ���ںͿͻ���֮��Ķ�д���� ͨ��
            if (socketChannel!=null){
                log.debug("connected... {}",socketChannel);
                // ʹSocketChannelΪ������
                socketChannel.configureBlocking(false); // ʹreadΪ������
                channels.add(socketChannel);
            }
            for (SocketChannel channel : channels) {
                // 5. ���տͻ��˷��͵�����
                //log.debug("before,read... {}",channel);
                int read = channel.read(buffer);// �������� û�ж������ݻ᷵��0
                if (read>0) { //��ȡ�����ֽ�������0
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read... {}",channel);
                }

            }
        }
    }
    public static void main1(String[] args) throws IOException {
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
