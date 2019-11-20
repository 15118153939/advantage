package com.lv.advantage.server;

import com.lv.advantage.kryocodec.KryoEncoder;
import com.lv.advantage.kryocodec.KryoDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 11:42
 * @Description:
 */
public class ServerInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {


        //Netty提供的日志打印Handler，可以展示发送接收出去的字节
        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));

        //剥离接收到的消息的长度字段，拿到实际的消息报文的字节数组，解决粘包半包问题
        socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));

        //给发送出去的消息增加长度字段，解决粘包半包问题
        socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));

        //反序列化，将字节数组转换为消息实体
        socketChannel.pipeline().addLast(new KryoDecoder());

        //序列化，将消息实体转换为字节数组准备进行网络传输
        socketChannel.pipeline().addLast("MessageEncoder", new KryoEncoder());

        //超时检测
        socketChannel.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));

        //登录应答
        socketChannel.pipeline().addLast(new LoginAuthResponseHandler());

        //心跳应答
        socketChannel.pipeline().addLast("HeartBeatHandler", new HeartBeatResponseHandler());

        //服务端业务处理
        socketChannel.pipeline().addLast("ServerBusiHandler", new ServerBusiHandler());
    }
}
