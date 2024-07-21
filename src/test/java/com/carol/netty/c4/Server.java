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
