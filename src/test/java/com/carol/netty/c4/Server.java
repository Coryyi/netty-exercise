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
     * selector模式
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // 1.创建Selector 可以管理多个 channel
        Selector selector = Selector.open();


        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        // 2.建立Selector和channel的联系 将channel注册到selector
        // selectionKey就是事件发生后，通过它得到是哪个channel发生的事件
        // 注册后 selector就可以监听事件的发送，监听到某个channel事件的发生，就会放到selectionKey中
        SelectionKey sscKey = serverSocketChannel.register(selector, 0, null);// serverSocketChannel的key
        // 指定监听事件 key只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}",sscKey);

        serverSocketChannel.bind(new InetSocketAddress(8080));
        while (true){
            // 3.selector的select方法 调用该方法，如果没有事件发生则线程阻塞，有才会恢复运行
            // select在事件未处理时不会阻塞
            selector.select();//没有事件发生就会让线程阻塞 只有四种事件其中一个发生了才会让线程继续
            // 4.遍历事件集合来处理事件 selectedKeys 拿到一个事件集 内部包含了所有发生的事件 返回一个Set集合 注意是发生的事件！！！
            // 要在集合中删除元素的遍历要使用迭代器进行循环 不要用 增强for
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                 // 拿到一个发生的事件
                SelectionKey key = iterator.next();// 如果事件不处理则会一直保存在selectedKeys集合中
                ServerSocketChannel channel =(ServerSocketChannel) key.channel();
                SocketChannel socketChannel = channel.accept();// accept事件处理
                log.debug("socketChannel {}",socketChannel);

                // 事件取消
                key.cancel();

            }
        }
    }

    public static void main2(String[] args) throws IOException {
        // 使用 NIO 非阻塞
        // 0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1.创建服务器
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置非阻塞模式 会影响accept方法
        serverSocketChannel.configureBlocking(false);

        // 2.绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // 3.连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true){
            // 4.建立与客户端的连接 accept()
            //log.debug("connecting...");
            // 使用configureBlocking设置为非阻塞 线程会继续执行 如果accept没有连接建立 返回的就是null值
            // 在while true中就会一直循环执行
            SocketChannel socketChannel = serverSocketChannel.accept(); // 用于和客户端之间的读写操作 通信
            if (socketChannel!=null){
                log.debug("connected... {}",socketChannel);
                // 使SocketChannel为非阻塞
                socketChannel.configureBlocking(false); // 使read为非阻塞
                channels.add(socketChannel);
            }
            for (SocketChannel channel : channels) {
                // 5. 接收客户端发送的数据
                //log.debug("before,read... {}",channel);
                int read = channel.read(buffer);// 非阻塞下 没有读到数据会返回0
                if (read>0) { //读取到的字节数大于0
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read... {}",channel);
                }

            }
        }
    }
    public static void main1(String[] args) throws IOException {
        // 使用 NIO 类理解阻塞模式 ，单线程处理
        // 0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1.创建服务器
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 2.绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // 3.连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true){
            // 4.建立与客户端的连接 accept()
            log.debug("connecting...");
            // 阻塞方法！！！线程停止运行 没有客户端发送连接请求 当客户端连接建立以后才会恢复运行
            SocketChannel socketChannel = serverSocketChannel.accept(); // 用于和客户端之间的读写操作 通信


            log.debug("connected...");
            channels.add(socketChannel);
            for (SocketChannel channel : channels) {
                // 5. 接收客户端发送的数据
                log.debug("before,read... {}",channel);
                channel.read(buffer);// 阻塞方法 线程停止运行
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read... {}",channel);
            }

        }
    }
}
