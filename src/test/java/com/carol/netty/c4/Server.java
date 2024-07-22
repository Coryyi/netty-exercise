package com.carol.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.carol.netty.c1.ByteBufferUtil.debugRead;

@Slf4j
public class Server {

    /**
     * selectorģʽ
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // 1.����Selector ���Թ����� channel
        Selector selector = Selector.open();// �ڲ���һ������ selectionKey

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        // 2.����Selector��channel����ϵ ��channelע�ᵽselector
        // selectionKey�����¼�������ͨ�����õ����ĸ�channel�������¼�
        // ע��� selector�Ϳ��Լ����¼��ķ��ͣ�������ĳ��channel�¼��ķ������ͻ�ŵ�selectionKey��
        SelectionKey sscKey = serverSocketChannel.register(selector, 0, null);// serverSocketChannel��key
        // ָ�������¼� keyֻ��עaccept�¼�
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}",sscKey);
        serverSocketChannel.bind(new InetSocketAddress(8089));
        while (true){
            // 3.selector��select���� ���ø÷��������û���¼��������߳��������вŻ�ָ�����
            // select���¼�δ����ʱ��������
            selector.select();//û���¼������ͻ����߳����� ֻ�������¼�����һ�������˲Ż����̼߳���
            // 4.�����¼������������¼� selectedKeys �õ�һ���¼��� �ڲ����������з������¼� ����һ��Set���� ע���Ƿ������¼�������
            // Ҫ�ڼ�����ɾ��Ԫ�صı���Ҫʹ�õ���������ѭ�� ��Ҫ�� ��ǿfor
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){

                // ��socketChannelע��������õ����ѷ����¼�������accept��reader ���������Ҫ���ݲ�ͬ���¼����Ͳ�ͬ����
                // �õ�һ���������¼�
                SelectionKey key = iterator.next();// ����¼����������һֱ������selectedKeys������

                // selectedKeys�����е�Ԫ�ز����Զ�ɾ������������selectedKeys��Ȼ�����ټ����У�ֻ���¼��������ˣ����������¼��Ƴ���
                // ����ɾ�����ڶ��α��������ȡ����Ԫ�أ����Ҹ�Ԫ�ص��¼��Ѿ����������ȥ����ͻ������ָ���쳣
                // ��ȡ��key��ͽ���ɾ��
                iterator.remove();

                // 5.�����¼�����
                if(key.isAcceptable()){ //�����accept�¼�
                    ServerSocketChannel channel =(ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();// accept�¼�����
                    // �¼�ȡ��
                    /*key.cancel();*/
                    // ����SocketChannel
                    socketChannel.configureBlocking(false);
                    // ע�ᵽselector
                    SelectionKey scKey = socketChannel.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("socketChannel {}",socketChannel);

                }else if(key.isReadable()){
                    try {
                        SocketChannel socketChannel = (SocketChannel) key.channel();// �õ������¼���channel
                        ByteBuffer buffer = ByteBuffer.allocate(16);

                        int read = socketChannel.read(buffer);
                        if (read==-1){
                            key.cancel();
                        }else {
                            buffer.flip();
                            debugRead(buffer);
                        }

                    } catch (IOException e) {
                        // ���ղ����key �޷��������ֻ��cancel
                        // �ͻ����ѶϿ���û��Ҫ�ټ������¼� �Ὣ���¼���ע��Ҳɾȥ
                        // ��Selector ��key����������ɾ��
                        key.cancel();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main2(String[] args) throws IOException {
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
