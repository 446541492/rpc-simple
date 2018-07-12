package com.rpc.transport;

import com.rpc.cache.ResultMap;
import com.rpc.utils.ChannelUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.CountDownLatch;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 11:31 2018/7/9
 */
public class NettyClient implements ApplicationContextAware {
    public Channel channel;
    /**
     * 客户端长连接并使用连接池，channel可能复用，发送数据和接受数据异步
     * @param req
     * @return
     * @throws InterruptedException
     */
    public Response send(Request req) throws Exception {
        //如果管道没有被开启或者被关闭了，那么重连
        if(null == channel || !channel.isActive()){
            initChannel();
        }
        CountDownLatch latch = new CountDownLatch(1);
        ChannelUtils.putCallback2DataMap(channel,req.getRequestId(),latch);
        channel.writeAndFlush(req).sync();//sync()
        latch.await();
        return ResultMap.getMap().remove(req.getRequestId());
    }

    /**
     * 建立一个长连接
     */
    public NettyClient() {
        initChannel();
    }

    private void initChannel(){
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
                                    .addLast(new RpcResHandler()); // 使用 RpcClient 发送 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            if (future.isSuccess()) {
                channel = future.channel();
                System.out.println("长连接客户端启动！");
            }
            //为刚刚创建的channel，初始化channel属性
            ChannelUtils.initDataMap(channel);
//            future.channel().closeFuture();//客户端长连接不主动关闭
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

//        System.out.println(hello.say("hello1 "));
//        System.out.println(hello.say("hello2 "));
//        System.out.println(hello.say("hello3"));
    }
}
