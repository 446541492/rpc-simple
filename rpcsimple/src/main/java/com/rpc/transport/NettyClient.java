package com.rpc.transport;

import com.rpc.cache.ResultMap;
import com.rpc.lock.Lock;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 11:31 2018/7/9
 */
public class NettyClient{

    public Response send(Request req) {
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
                                    .addLast(new RpcResHandler(req)); // 使用 RpcClient 发送 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            ChannelFuture channelFuture = future.channel().writeAndFlush(req).sync();
            boolean result = channelFuture.awaitUninterruptibly(10000, TimeUnit.MILLISECONDS);
            future.channel().closeFuture().sync();//sync()  表示阻塞等待channel主动关闭ChannelHandlerContext
            return ResultMap.getMap().get(req.getRequestId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }



}
