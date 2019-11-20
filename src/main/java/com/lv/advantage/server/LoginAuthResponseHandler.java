package com.lv.advantage.server;

import com.lv.advantage.vo.MessageType;
import com.lv.advantage.vo.MyHeader;
import com.lv.advantage.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 14:04
 * @Description: 登录检查
 */
public class LoginAuthResponseHandler extends ChannelInboundHandlerAdapter {

    private final static Log LOG = LogFactory.getLog(LoginAuthResponseHandler.class);

    /**
     * 用以检查用户是否重复登录的缓存
     */
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();


    /**
     * 用户登录的白名单
     */
    private String[] whitekList = {"127.0.0.1"};


    /**
     * 读取到用户请求
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        MyMessage message = (MyMessage) msg;

        //如果是握手请求，消息类型是登录请求，则处理，否则 消息传递
        if (message.getMyHeader() != null && message.getMyHeader().getType() == MessageType.LOGIN_REQ.value()) {

            String nodeIndex = ctx.channel().remoteAddress().toString();
            MyMessage loginResponse;
            //重复登陆，拒绝
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResponse = buildResponse((byte) -1);
            } else {

                //检查用户是否在白名单中，在则允许登录，并写入缓存
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();

                boolean isOk = false;
                for (String whiteIp : whitekList) {
                    if (whiteIp.equals(ip)) {
                        isOk = true;
                        break;
                    }
                }

                if (isOk) {
                    //在则允许登录，并写入缓存
                    loginResponse = buildResponse((byte) 0);
                    nodeCheck.put(nodeIndex, true);
                } else {
                    loginResponse = buildResponse((byte) -1);
                }

            }
            LOG.info("The login response is : " + loginResponse + " body [" + loginResponse.getBody() + "]");
            ctx.writeAndFlush(loginResponse);
            ReferenceCountUtil.release(msg);

        } else {
            //传递消息，注释后，可演示消息不往下传递的情况
            ctx.fireChannelRead(msg);
        }


    }

    private MyMessage buildResponse(byte result) {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.LOGIN_RESP.value());
        message.setMyHeader(myHeader);
        message.setBody(result);
        return message;
    }

    /**
     * 客户端突然断线,清除本地缓冲
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 删除缓存
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
