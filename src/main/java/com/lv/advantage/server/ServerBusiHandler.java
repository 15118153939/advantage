package com.lv.advantage.server;

import com.lv.advantage.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 14:38
 * @Description:
 */
public class ServerBusiHandler extends SimpleChannelInboundHandler<MyMessage> {
    private static final Log LOG = LogFactory.getLog(ServerBusiHandler.class);

    /**
     * 业务处理
     *
     * @param channelHandlerContext
     * @param myMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyMessage myMessage) throws Exception {

        LOG.info("myMessage:" + myMessage);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.info(ctx.channel().remoteAddress() + " 主动断开了连接!");
    }
}
