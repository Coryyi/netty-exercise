package com.carol.netty.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.carol.netty.c1.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);// �ر�����ģʽ

        // ����Selector
        Selector boss = Selector.open();
        // �����channelע��
        SelectionKey bossKey = ssc.register(boss, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        //1. �����̶�������worker ��ʼ��
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];// ��ȡ���Կ��ú�����
        //Worker worker = new Worker("worker-0");
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-"+i);//���齫�߳�������Ϊcpu������
        }

        AtomicInteger index = new AtomicInteger();

        while (true){
            // �����¼� û���¼�������
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    // ����˼����� accept �¼�
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    log.debug("connected..{}", ssc.getLocalAddress());
                    // 2.���� �����channel��д�¼�����worker
                    // round robin ��ѯ
                    workers[index.getAndIncrement()% workers.length].register(socketChannel);
                    //worker.register(socketChannel); //��boss�̵߳��� ���register�����ڵĴ��붼��boss�߳���ִ��

                }
            }
        }
    }

    static class Worker implements Runnable{
        // �Լ��������߳�
        private Thread thread;
        private Selector selector;
        private String name;
        //�����߳�֮�䴫�����ݾͿ����ö�����Ϊ����ͨ��
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        private volatile boolean start = false;//δ��ʼ��

        public Worker(String name){
            this.name = name;
        }

        /**
         * ��ʼ���̺߳�selector
         * ����ϣ��һ��worker��Ӧһ���߳�
         */
        public void register(SocketChannel socketChannel) throws IOException {
            if(!start){
                thread = new Thread(this,name);//��������ȥ��runnable���ڶ���������ȥ��run()����
                selector = Selector.open();
                start = true;
                thread.start();
            }
            // ����������һ������,���������û������ִ��
            queue.add(()->{
                try {
                    socketChannel.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup();// ���������select����


        }

        @Override
        public void run() {
            while (true){
                try {
                    // �����������ѣ�ʹ��wakeup
                    selector.select();// ������ø÷�����û���¼�������������� register�����ĵ���Ҳ�ᱻ����

                    Runnable task = queue.poll();//�������û���� ����Ϊ��
                    if (task!=null){
                        task.run();
                    }

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        // ֻ�����д�¼�
                        if(key.isReadable()){
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel sc = (SocketChannel) key.channel();
                            log.debug("read...{}",sc.getLocalAddress());
                            sc.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
