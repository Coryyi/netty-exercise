package com.carol.protocol;

import com.carol.config.Config;
import com.carol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * bytebuf与消息类型转换 泛型填入消息类型
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    /**
     * 编码
     * @param channelHandlerContext
     * @param message
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        // 1.魔数 约定五个字节
        byteBuf.writeBytes("CAROL".getBytes());
        //2.版本  字节版本 1
        byteBuf.writeByte(1);//版本1
        //3.序列化方式 0 代表jdk 1代表json 总之一个字节代表序列化方式
        byteBuf.writeByte(0);
        // 4. 指令类型 聊天消息还是登录消息...
        byteBuf.writeByte(message.getMessageType());
        //5.指令序号 暂时不考虑双工 4个字节
        byteBuf.writeInt(message.getSequenceId());


        //7.消息正文
        /*ByteArrayOutputStream bos= new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(message);// 将对象序列化 写入对象输出流 bos 中
        byte[] bytes = bos.toByteArray();*/

        byte[] bytes = Config.getSerializerAlgorithm().serialize(message);

        //6.正文长度
        byteBuf.writeInt(bytes.length);

        // 8.写入内容
        byteBuf.writeBytes(bytes);
    }

    /**
     * 解码
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //int magicNum = byteBuf.readInt();
        log.debug("执行decode");
        byte[] magicNum = new byte[5];
        byteBuf.readBytes(magicNum, 0, 5);

        byte version = byteBuf.readByte();
        byte serializerAlgorithm = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();

        int length = byteBuf.readInt();//长度
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes, 0, length);

        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        // 确定类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);
        /*ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));*/
        /*Message message = (Message)ois.readObject();*/

        log.debug("{},{},{},{},{},{}", new String(magicNum, Charset.defaultCharset()),version,serializerAlgorithm,messageType,sequenceId,length);
        log.debug("{}",message);
    }
}
