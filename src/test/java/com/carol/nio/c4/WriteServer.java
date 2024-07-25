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



                    // 1.向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 3000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer byteBuffer = Charset.defaultCharset().encode(sb.toString());


                    // 2.返回值代表实际写入字节数
                    int write = socketChannel.write(byteBuffer);// 返回实际写入的字节数 不一定能一次写完
                    System.out.println("实际写入数"+write);
                    //3.判断是否有剩余内容
                    if(byteBuffer.hasRemaining()){
                        // 4.关注可写事件 如果原来关注了事件，则会覆盖原来的事件，如果不想覆盖原来的事件,用下面代码，保存原来事件在加上新的事件
                        selectionKey.interestOps(selectionKey.interestOps() + SelectionKey.OP_WRITE);
                        // 5.将未写完的数据挂到selectionKey
                        selectionKey.attach(byteBuffer);
                    }
                }else if(key.isWritable()){
                    ByteBuffer attachmentBuffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int write = channel.write(attachmentBuffer);
                    System.out.println(write);

                    // 6.数据写完了处理buffer 清理操作
                    if (!attachmentBuffer.hasRemaining()){
                        key.attach(null);
                        // 无需在关注可写事件
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);

                    }
                }
            }
        }
    }

}
