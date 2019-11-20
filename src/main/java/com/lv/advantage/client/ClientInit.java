package com.lv.advantage.client;

import com.lv.advantage.kryocodec.KryoDecoder;
import com.lv.advantage.kryocodec.KryoEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 14:58
 * @Description:
 */
public class ClientInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        System.out.println("ClientInit:initChannel");
        //剥离接收到的消息的长度字段，拿到实际的消息报文的字节数组
        socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));

        //给发送出去的消息增加长度字段
        socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));

        //反序列化，将字节数组转换为消息实体
        socketChannel.pipeline().addLast(new KryoDecoder());

        //序列化，将消息实体转换为字节数组准备进行网络传输
        socketChannel.pipeline().addLast("MessageEncoder", new KryoEncoder());

        //超时检测
        socketChannel.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(10));

        //发出登录请求
        socketChannel.pipeline().addLast("LoginAuthHandler", new LoginAuthRequestHandler());

        //发出心跳请求
        socketChannel.pipeline().addLast("HeartBeatHandler", new HeartBeatRequestHandler());

    }
}
