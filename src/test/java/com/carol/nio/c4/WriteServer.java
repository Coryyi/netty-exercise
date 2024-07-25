package com.carol.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));
        while (true){
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey selectionKey = socketChannel.register(selector, 0, null);



                    // 1.��ͻ��˷��ʹ�������
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 3000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer byteBuffer = Charset.defaultCharset().encode(sb.toString());


                    // 2.����ֵ����ʵ��д���ֽ���
                    int write = socketChannel.write(byteBuffer);// ����ʵ��д����ֽ��� ��һ����һ��д��
                    System.out.println("ʵ��д����"+write);
                    //3.�ж��Ƿ���ʣ������
                    if(byteBuffer.hasRemaining()){
                        // 4.��ע��д�¼� ���ԭ����ע���¼�����Ḳ��ԭ�����¼���������븲��ԭ�����¼�,��������룬����ԭ���¼��ڼ����µ��¼�
                        selectionKey.interestOps(selectionKey.interestOps() + SelectionKey.OP_WRITE);
                        // 5.��δд������ݹҵ�selectionKey
                        selectionKey.attach(byteBuffer);
                    }
                }else if(key.isWritable()){
                    ByteBuffer attachmentBuffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int write = channel.write(attachmentBuffer);
                    System.out.println(write);

                    // 6.����д���˴���buffer �������
                    if (!attachmentBuffer.hasRemaining()){
                        key.attach(null);
                        // �����ڹ�ע��д�¼�
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);

                    }
                }
            }
        }
    }

}
