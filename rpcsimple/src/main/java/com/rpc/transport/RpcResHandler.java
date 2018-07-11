package com.rpc.transport;

import com.rpc.cache.ResultMap;
import com.rpc.lock.Lock;
import com.rpc.utils.ChannelUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 16:37 2018/7/10
 */
public class RpcResHandler  extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Response) {
            Channel channel = channelHandlerContext.channel();
            ResultMap.getMap().put(((Response) o).getRequestId(),(Response)o);
            CountDownLatch latch = ChannelUtils.removeCallback(channel,((Response) o).getRequestId());
            latch.countDown();
//            synchronized (lock){
//                lock.notifyAll();
//            }
//            if (res.getCode() == 200) {
//                Response result = new Response();
//                result.setCode(200);
//                result.setResult("success");
//                channelHandlerContext.writeAndFlush(result);
//            }
        }

    }
}
