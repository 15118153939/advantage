package com.lv.advantage;

import com.lv.advantage.server.ServerInit;
import com.lv.advantage.vo.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 11:33
 * @Description: 服务端的主入口
 */
public class NettyServer {

    private static final Log LOG = LogFactory.getLog(NettyServer.class);

    public void bind() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option((ChannelOption.SO_BACKLOG), 1024);
        serverBootstrap.childHandler(new ServerInit());

        // 绑定端口，同步等待成功
        serverBootstrap.bind(NettyConstant.REMOTE_PORT).sync();
        LOG.info("Netty server start : " + (NettyConstant.REMOTE_IP + " : " + NettyConstant.REMOTE_PORT));
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().bind();

    }

}
