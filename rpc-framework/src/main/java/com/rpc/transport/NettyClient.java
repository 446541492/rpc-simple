package com.rpc.transport;

import com.demo.service.HelloService;
import com.rpc.cache.ResultMap;
import com.rpc.proxy.JdkProxy;
import com.rpc.utils.ChannelUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 11:31 2018/7/9
 */
public class NettyClient{
    public Channel channel;

    /**
     * 客户端长连接并使用连接池，channel可能复用，且发送数据和接受数据异步，所以请求需要同步
     *
     * @param req
     * @return
     * @throws InterruptedException
     */
    public Response send(Request req) throws Exception {
        //如果管道没有被开启或者被关闭了，那么重连
        if (null == channel || !channel.isActive()) {
            initChannel();
        }
        CountDownLatch latch = new CountDownLatch(1);
        //请求缓存起来
        ChannelUtils.putCallback2DataMap(channel, req.getRequestId(), latch);
        channel.writeAndFlush(req);
        //等待回复
        latch.await();
        //返回结果
        return ResultMap.getMap().remove(req.getRequestId());
    }

    /**
     * 建立一个长连接
     */
    public NettyClient() {
        initChannel();
    }

    private void initChannel() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcEncoder()) // 将 RPC 请求进行编码（为了发送请求）
                                    .addLast(new RpcDecoder()) // 将 RPC 响应进行解码（为了处理响应）
//                                    .addLast(new ReadTimeoutHandler(5))//超时handler 5s没有交互，就会关闭channel
                                    .addLast(new RpcResHandler()); // 使用 RpcClient 发送 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            //当调用 connect() 将会直接是非阻塞的，并且调用在背后完成。异步连接到远程对等节点。调用立即返回并提供 ChannelFuture
            // 由于线程是非阻塞的，所以无需等待操作完成，而可以去干其他事，因此这令资源利用更高效。
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);
            //当监听器被通知连接完成，我们检查状态。如果是成功，就写数据到 Channel，否则我们检索 ChannelFuture 中的Throwable。
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        //为刚刚创建的channel，初始化channel属性
                        ChannelUtils.initDataMap(channel);
                    } else {
                        Throwable cause = future.cause();
                        cause.printStackTrace();
                    }
                }
            });
            System.out.println("长连接客户端启动！");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
