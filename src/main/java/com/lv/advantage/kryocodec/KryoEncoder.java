package com.lv.advantage.kryocodec;

import com.lv.advantage.vo.MyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 13:55
 * @Description:   消息的编码框架 ，实现了POJO的序列化
 */
public class KryoEncoder extends MessageToByteEncoder<MyMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyMessage myMessage, ByteBuf byteBuf) throws Exception {
        KryoSerializer.serialize(myMessage, byteBuf);
        channelHandlerContext.flush();
    }
}
