package com.rpc.transport;

import com.rpc.cache.ResultMap;
import com.rpc.utils.ChannelUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CountDownLatch;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 16:37 2018/7/10
 */
public class RpcResHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Response) {
            Channel channel = channelHandlerContext.channel();
            ResultMap.getMap().put(((Response) o).getRequestId(), (Response) o);
            CountDownLatch latch = ChannelUtils.removeCallback(channel, ((Response) o).getRequestId());
            latch.countDown();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
