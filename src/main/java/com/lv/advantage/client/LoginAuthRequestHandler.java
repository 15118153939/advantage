package com.lv.advantage.client;

import com.lv.advantage.vo.MessageType;
import com.lv.advantage.vo.MyHeader;
import com.lv.advantage.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 15:20
 * @Description:
 */
public class LoginAuthRequestHandler extends ChannelInboundHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(LoginAuthRequestHandler.class);

    /**
     * 建立连接后，发出登录请求
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.info(" 建立连接成功!，发出登录请求");
        ctx.writeAndFlush(buildLoginRequest());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        MyMessage message = (MyMessage) msg;

        //如果是登录/业务握手应答消息，需要判断是否认证成功
        if (message.getMyHeader() != null && message.getMyHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0) {
                // 握手失败，关闭连接,则不会发起心跳请求
                System.out.println("握手失败，关闭连接,则不会发起心跳请求");
                ctx.close();
            } else {
                LOG.info("Login is ok : " + message);
                //验证登录成功，发起心跳请求
                ctx.fireChannelRead(msg);
            }
        } else {
            System.out.println("其它业务：转发消息");
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    private MyMessage buildLoginRequest() {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.LOGIN_REQ.value());
        message.setMyHeader(myHeader);
        return message;
    }
}
