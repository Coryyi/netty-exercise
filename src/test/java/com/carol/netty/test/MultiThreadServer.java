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
        ssc.configureBlocking(false);// 关闭阻塞模式

        // 创建Selector
        Selector boss = Selector.open();
        // 服务端channel注册
        SelectionKey bossKey = ssc.register(boss, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        //1. 创建固定数量的worker 初始化
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];// 获取电脑可用核心数
        //Worker worker = new Worker("worker-0");
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-"+i);//建议将线程数设置为cpu核心数
        }

        AtomicInteger index = new AtomicInteger();

        while (true){
            // 监听事件 没有事件则阻塞
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    // 服务端监听到 accept 事件
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    log.debug("connected..{}", ssc.getLocalAddress());
                    // 2.关联 将这个channel读写事件交给worker
                    // round robin 轮询
                    workers[index.getAndIncrement()% workers.length].register(socketChannel);
                    //worker.register(socketChannel); //被boss线程调用 因此register方法内的代码都在boss线程中执行

                }
            }
        }
    }

    static class Worker implements Runnable{
        // 自己独立的线程
        private Thread thread;
        private Selector selector;
        private String name;
        //两个线程之间传递数据就可以用队列作为数据通道
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        private volatile boolean start = false;//未初始化

        public Worker(String name){
            this.name = name;
        }

        /**
         * 初始化线程和selector
         * 这里希望一个worker对应一个线程
         */
        public void register(SocketChannel socketChannel) throws IOException {
            if(!start){
                thread = new Thread(this,name);//将本身传过去，runnable会在对象本身里面去找run()方法
                selector = Selector.open();
                start = true;
                thread.start();
            }
            // 向队列中添加一个任务,但这个任务没有立刻执行
            queue.add(()->{
                try {
                    socketChannel.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup();// 唤醒下面的select方法


        }

        @Override
        public void run() {
            while (true){
                try {
                    // 可以主动唤醒，使用wakeup
                    selector.select();// 如果调用该方法，没有事件则会阻塞，包括 register方法的调用也会被阻塞

                    Runnable task = queue.poll();//如果队列没东西 返回为空
                    if (task!=null){
                        task.run();
                    }

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        // 只在意读写事件
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
