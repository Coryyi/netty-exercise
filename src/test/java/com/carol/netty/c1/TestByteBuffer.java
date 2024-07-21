package com.carol.netty.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {
    /**
     * 文件读取 使用buffer 与 channel
     */
    public static void main(String[] args) {
        // FileChannel
        //1. 输入输出流 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            //读取内容需要一个缓冲区来处理 allocate() 划分一块内存作缓冲区 传入容量 单位字节
            ByteBuffer buffer = ByteBuffer.allocate(10);
             //从channel读取数据 等于向缓冲区buffer写入
            /*int len = channel.read(buffer);
            // 打印buffer内容
            buffer.flip();//切换到buffer读模式 默认是写模式
            while (buffer.hasRemaining()*//*检查是否还有剩余的未读数据*//*){
                byte b = buffer.get();//无参的get 一次读一个字节
                System.out.println((char) b);
            }*/
            // 修改代码 用一个固定长度的缓冲区循环读取数据 分多次读取
            while(true) {
                int len = channel.read(buffer);
                log.debug("读取到的字节数 {}",len);
                // 当返回结果为 -1 时 没有内容了
                if(len==-1){
                    break;
                }
                // 打印buffer内容
                buffer.flip();//切换到buffer读模式 默认是写模式
                while (buffer.hasRemaining()/*检查是否还有剩余的未读数据*/) {
                    byte b = buffer.get();//无参的get 一次读一个字节
                    log.debug("读取到的字节 {}",(char) b);

                }
                // 读完一次后 将buffer切换为写模式
                buffer.clear();
            }

        } catch (IOException e) {
        }
    }


}
