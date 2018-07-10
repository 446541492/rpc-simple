package com.rpc.transport;

import com.rpc.annotation.RpcService;
import com.rpc.properties.ConfigProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author wanglei
 * @date create in 13:36 2018/7/5
 */
public class NettyServer implements ApplicationContextAware,InitializingBean{
    private Map rpcService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       Map serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);

        if (!serviceBeanMap.isEmpty()) {
            rpcService = new HashMap();
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                rpcService.put(interfaceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder()) // 将 RPC 请求进行解码（为了处理请求）
                                    .addLast(new RpcEncoder()) // 将 RPC 响应进行编码（为了返回响应）
                                    .addLast(new RpcReqHandler(rpcService)); // 处理 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            int port = Integer.valueOf(ConfigProperties.getRpcPort());
            ChannelFuture future = bootstrap.bind(port).sync();// netty每个I/O操作都有Future且是异步非阻塞，如果需要同步给结果则需ChannelFuture.sync()
            System.out.println("netty start port:"+ ConfigProperties.getRpcPort());
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
