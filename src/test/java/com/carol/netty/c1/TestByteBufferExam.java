package com.carol.netty.c1;

import java.nio.ByteBuffer;

import static com.carol.netty.c1.ByteBufferUtil.debugAll;

/**
 * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
 * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
 *
 * - Hello,world\n
 * - I'm zhangsan\n
 * - How are you?\n
 *
 * 变成了下面的两个 byteBuffer (黏包，半包)
 *
 * - Hello,world\nI'm zhangsan\nHo
 * - w are you?\n
 */
public class TestByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);

        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());// 模拟读取数据放入Bytebuffer中
        // 解决黏包 半包
        split(source);
        source.put("w are you?\nhaha!\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer buffer){
        buffer.flip();// 切换读模式
        for (int i = 0; i < buffer.limit(); i++) {

            if (buffer.get(i)== '\n') {
                // 当前i是换行符位置 +1 开头到此的总长度，position是指针位置，也就是该包首个字符的索引 也是头部非该包字节总长度
                int len = i+1-buffer.position();
                // 是换行符
                // 将完整消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(buffer.get());
                }
                debugAll(target);
            }
        }
        buffer.compact();// 保留半包数据
    }

}
