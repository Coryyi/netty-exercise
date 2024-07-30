package com.carol.protocol;

import com.carol.config.Config;
import com.carol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
/**
 * 必须和 LengthFieldBasedDecoder一起使用 确保接收到的byteBuf消息是完整的 无需记录状态
 */
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();
        // 1.魔数 约定五个字节
        byteBuf.writeBytes("CAROL".getBytes());
        //2.版本  字节版本 1
        byteBuf.writeByte(1);//版本1
        //3.序列化方式 0 代表jdk 1代表json 总之一个字节代表序列化方式
        byteBuf.writeByte(Config.getSerializerAlgorithm().ordinal());// 枚举对象有顺序
        // 4. 指令类型 聊天消息还是登录消息...
        byteBuf.writeByte(message.getMessageType());
        //5.指令序号 暂时不考虑双工 4个字节
        byteBuf.writeInt(message.getSequenceId());


        //7.消息正文
        /*ByteArrayOutputStream bos= new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(message);// 将对象序列化 写入对象输出流 bos 中
        byte[] bytes = bos.toByteArray();*/

        byte[] bytes = Config.getSerializerAlgorithm().serialize(message);//序列化

        //6.正文长度
        byteBuf.writeInt(bytes.length);

        // 8.写入内容
        byteBuf.writeBytes(bytes);
        //传给下一个出站处理器
        list.add(byteBuf);
    }

    @Override // 上一个处理器是粘包半包处理器，处理完后一定是完整的消息
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
       //int magicNum = byteBuf.readInt();
        byte[] magicNum = new byte[5];
        byteBuf.readBytes(magicNum, 0, 5);

        byte version = byteBuf.readByte();
        byte serializerAlgorithm = byteBuf.readByte();// 获取序列化算法
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();

        int length = byteBuf.readInt();//长度
        byte[] bytes = new byte[length];

        byteBuf.readBytes(bytes, 0, length);

        /*ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message)ois.readObject();*/


        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];//获取到序列化算法
        //确定具体消息类型
        //Message message = Serializer.Algorithm.values()[serializerAlgorithm].deserialize(Message.class, bytes);
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);
        //log.debug("{},{},{},{},{},{}", new String(magicNum, Charset.defaultCharset()),version,serializerType,messageType,sequenceId,length);
        //log.debug("{}",message);
        list.add(message);
    }//认为没有半包粘包状态
}
