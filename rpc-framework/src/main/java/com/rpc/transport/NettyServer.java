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
        EventLoopGroup bossGroup = new NioEventLoopGroup();//事件轮询线程组：用来处理网络事件处理（接受客户端连接）
        EventLoopGroup workerGroup = new NioEventLoopGroup();//事件轮询线程组：用来进行网络通讯读写
        try {
            //ServerBootstrap是一个帮助类，用于设置服务器。
            //一个 ServerBootstrap 可以认为有2个 Channel 集合，第一个集合包含一个单例 ServerChannel，代表持有一个绑定了本地端口的 socket；
            // 第二集合包含所有创建的 Channel，处理服务器所接收到的客户端进来的连接socket。
            //与 ServerChannel 相关 EventLoopGroup 分配一个 EventLoop 是 负责创建 Channels 用于传入的连接请求。一旦连接接受，第二个EventLoopGroup 分配一个 EventLoop 给它的 Channel。
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
                    //当一个新的连接被接受，一个新的子 Channel 将被创建， ChannelInitializer 会添加我们Handler 的实例到 Channel 的 ChannelPipeline。
                    // 正如我们如前所述，如果有入站信息，这个处理器将被通知。
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()//pipeline 是 handler的容器
                                    //Netty 中有两个方向的数据流，入站(ChannelInboundHandler)和出站(ChannelOutboundHandler)之间有一个明显的区别：
                                    // 若数据是从用户应用程序到远程主机则是“出站(outbound)”，相反若数据时从远程主机到用户应用程序则是“入站(inbound)”。
                                    //为了使数据从一端到达另一端，一个或多个 ChannelHandler 将以某种方式操作数据。这些 ChannelHandler 会在程序的“引导”阶段被添加ChannelPipeline中，
                                    // 并且被添加的顺序将决定处理数据的顺序。
                                    .addLast(new RpcDecoder()) // 入站消息将从字节转为一个Java对象;也就是说，“解码”
                                    .addLast(new RpcEncoder()) // 出站相反会发生：“编码”，从一个Java对象转为字节。其原因是简单的：网络数据是一系列字节，因此需要从那类型进行转换。

                                    .addLast(new RpcReqHandler(rpcService)); // 业务处理类
                        }
                    });
            int port = Integer.valueOf(ConfigProperties.getRpcPort());

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
