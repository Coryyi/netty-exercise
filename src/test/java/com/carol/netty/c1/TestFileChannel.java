package com.carol.netty.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannel {
    public static void main(String[] args) {
        // 效率比自己用文件输入输出流高
        try (FileChannel from = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel()
        ) {
            // 起始位置 大小 目标
            // 效率比自己用文件输入输出流高 底层会用操作系统的零拷贝优化
            // 一次最多传 2g的数据 如果超过2g，只会传输2g的内容 可以多次传输

            //改进
            long size = from.size();
            // left 剩余多少字节
            for (long left = size; left > 0; ){
                left -= from.transferTo((size-left),from.size(),to);// 传递剩余
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
