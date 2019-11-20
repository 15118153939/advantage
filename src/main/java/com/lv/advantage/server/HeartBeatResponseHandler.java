package com.lv.advantage.server;

import com.lv.advantage.vo.MessageType;
import com.lv.advantage.vo.MyHeader;
import com.lv.advantage.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 14:28
 * @Description: 心跳应答
 */
public class HeartBeatResponseHandler extends ChannelInboundHandlerAdapter {
    private static final Log LOG = LogFactory.getLog(HeartBeatResponseHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;

        if (message.getMyHeader() != null && message.getMyHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {

            LOG.info("Receive client heart beat message : ---> " + message);
            //构建一个心跳应答
            MyMessage heartBeat = buildHeatBeat();
            LOG.info("Send heart beat response message to client : ---> " + heartBeat);
            ctx.writeAndFlush(heartBeat);
            ReferenceCountUtil.release(msg);

        }else {
            //如果不是心跳，则传递消息
            ctx.fireChannelRead(msg);
        }


    }


    /**
     * 心跳应答报文
     */
    private MyMessage buildHeatBeat() {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.HEARTBEAT_RESP.value());
        message.setMyHeader(myHeader);
        return message;
    }
}
