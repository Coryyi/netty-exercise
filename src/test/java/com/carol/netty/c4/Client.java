package com.carol.netty.c4;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8089));
        sc.write(Charset.defaultCharset().encode("12345678901234567777\n"));
        System.in.read();
        System.out.println("wating...");
    }
}
