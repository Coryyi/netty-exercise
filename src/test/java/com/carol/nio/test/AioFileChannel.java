package com.carol.nio.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.carol.nio.c1.ByteBufferUtil.debugAll;

@Slf4j
public class AioFileChannel {
    public static void main(String[] args) {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            //参数一 byteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(16);
            //参数二 读取的起始位置

            //参数三 附件
            // 参数四 回调函数
            log.debug("read begin...");
            channel.read(buffer,0,buffer,new CompletionHandler<Integer,ByteBuffer>(){
                // read成功
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    // 实际字节数  附件
                    log.debug("read completed...");
                    attachment.flip();
                    debugAll(attachment);
                }
                // read失败
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.debug("read end...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
