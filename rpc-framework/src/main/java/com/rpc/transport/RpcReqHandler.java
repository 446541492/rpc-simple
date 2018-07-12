package com.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 业务处理类
 *
 * @author wanglei
 * @date create in 10:51 2018/7/9
 */
public class RpcReqHandler extends SimpleChannelInboundHandler {
    private Map rpcService;
    private AtomicInteger sum = new AtomicInteger(0);
    public RpcReqHandler(Map rpcService) {
        this.rpcService = rpcService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if(o instanceof Request){
            Request req = (Request)o;
            Object obj = rpcService.get(req.getClassName());
            Method method = Class.forName(req.getClassName()).getMethod(req.getMethod(), req.getClasses());
            Response res = new Response();
            res.setRequestId(req.getRequestId());
            res.setCode(200);
            res.setResult(method.invoke(obj, req.getArgs()));
            channelHandlerContext.writeAndFlush(res);
        }
        System.out.println(sum.incrementAndGet());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
