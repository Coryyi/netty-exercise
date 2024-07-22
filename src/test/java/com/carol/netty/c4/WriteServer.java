package com.carol.netty.c4;

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

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 3000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer byteBuffer = Charset.defaultCharset().encode(sb.toString());
                    while (byteBuffer.hasRemaining()){
                        int write = socketChannel.write(byteBuffer);// 返回实际写入的字节数 不一定能一次写完
                        System.out.println("实际写入数"+write);
                    }
                }
            }
        }
    }

}
