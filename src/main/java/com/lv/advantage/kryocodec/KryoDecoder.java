package com.lv.advantage.kryocodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 11:52
 * @Description: 消息的解码框架 ，实现了POJO的反序列化
 */
public class KryoDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //转为为实体
        Object obj = KryoSerializer.deserialize(byteBuf);
        list.add(obj);
    }
}
