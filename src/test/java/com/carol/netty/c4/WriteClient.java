package com.carol.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",8080));
        int count = 0;
        // 接收数据
        while(true){
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            count += socketChannel.read(buffer);
            System.out.println(count+"\n");
            buffer.clear();
        }
    }

}
