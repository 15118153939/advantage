package com.lv.advantage;

import com.lv.advantage.client.ClientInit;
import com.lv.advantage.vo.MessageType;
import com.lv.advantage.vo.MyHeader;
import com.lv.advantage.vo.MyMessage;
import com.lv.advantage.vo.NettyConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author 吕明亮
 * @Date : 2019/11/19 14:41
 * @Description: Netty客户端的主入口
 */
public class NettyClient implements Runnable {
    private static final Log LOG = LogFactory.getLog(NettyClient.class);

    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("netty-client-schedule-pool-%d").daemon(true).build());


    private Channel channel;
    private EventLoopGroup group = new NioEventLoopGroup();


    /**
     * 是否用户主动关闭连接的标志值
     **/
    private volatile boolean userClose = false;

    /**
     * 连接是否成功关闭的标志值
     **/
    private volatile boolean connected = false;

    private AtomicInteger loginRetryCount;

    public boolean isConnected() {
        return connected;
    }

    /**
     * ------------测试NettyClient--------------------------
     */
    public static void main(String[] args) throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
    }


    /**
     * 业务使用的方法
     *
     * @param message
     */
    public void send(String message) {
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException("和服务器还未未建立起有效连接,请稍后再试!!");
        }
        MyMessage msg = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.SERVICE_REQ.value());
        msg.setMyHeader(myHeader);
        msg.setBody(message);
        channel.writeAndFlush(msg);
    }

    public void close() {
        userClose = true;
        channel.close();
    }


    @Override
    public void run() {
        try {
            connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接服务器
     *
     * @param port
     * @param host
     * @throws Exception
     */
    public void connect(int port, String host) throws Exception {

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ClientInit());

            //发起异步连接操作
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();

            channel = future.sync().channel();
            //连接成功后通知等待线程，连接已经建立
            synchronized (this) {
                this.connected = true;
                this.notifyAll();
            }
            //阻塞方法，客户端不关闭，这个方法不执行
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            LOG.info(e);
            e.printStackTrace();
        } finally {
            //非用户主动关闭，说明发生了网络问题，需要进行重连操作
            if (!userClose) {
                LOG.info("发现异常，可能发生了服务器异常或网络问题，准备进行重连.....");
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            //发起重连操作
                            connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else {
                //用户主动关闭，释放资源
                channel = null;
                group.shutdownGracefully().sync();
                synchronized (this) {
                    this.connected = false;
                    this.notifyAll();
                }
            }
        }


    }
}
