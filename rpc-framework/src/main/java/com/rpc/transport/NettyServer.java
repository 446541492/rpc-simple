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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglei
 * @date create in 13:36 2018/7/5
 */
public class NettyServer implements ApplicationContextAware, InitializingBean {
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
        EventLoopGroup bossGroup = new NioEventLoopGroup();//线程组：用来处理网络事件处理（接受客户端连接）
        EventLoopGroup workerGroup = new NioEventLoopGroup();//线程组：用来进行网络通讯读写
        try {
            //ServerBootstrap是一个帮助类，用于设置服务器。
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)////注册服务端channel，指定使用NioServerSocketChannel类来实例化。
                    /**
                     * BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
                     * 用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，将使用默认值50。
                     * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
                     * 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小

                     */
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //设置日志
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() //ChannelInitializer是一个特殊的处理程序，旨在帮助用户配置新的Channel
                    {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    //upstream event是被Upstream Handler们自底向上逐个处理，Inbound
                                    // downstream event是被Downstream Handler们自顶向下逐个处理， Outbound
                                    // 这里的上下关系就是向ChannelPipeline里添加Handler的先后顺序关系。
                                    // 简单的理 解，upstream event是处理来自外部的请求的过程，而downstream event是处理向外发送请求的过程。
                                    //编解码操作,要传输对象，必须编解码
                                    .addLast(new RpcDecoder()) // 将 RPC 请求进行解码（为了处理请求）
                                    .addLast(new RpcEncoder()) // 将 RPC 响应进行编码（为了返回响应）
                                    .addLast(new ReadTimeoutHandler(5))//超时handler 5s没有交互，就会关闭channel
                                    .addLast(new RpcReqHandler(rpcService)); // 业务处理类
                        }
                    });
            int port = Integer.valueOf(ConfigProperties.getRpcPort());
            //ChannelFuture表示尚未发生的I/O操作。这意味着任何请求的操作可能尚未执行，因为所有操作在Netty中都是异步的。
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("netty start port:" + ConfigProperties.getRpcPort());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
