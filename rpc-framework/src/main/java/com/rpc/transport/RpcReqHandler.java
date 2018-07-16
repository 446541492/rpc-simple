package com.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 业务处理类
 * SimpleChannelInboundHandler vs. ChannelInboundHandler
 * 何时用这两个要看具体业务的需要。
 * SimpleChannelInboundHandler channelRead()会释放对 ByteBuf（保存信息） 的引用。
 * ChannelInboundHandlerAdapter 需要自己释放 ReferenceCountUtil.release(msg) 或者  writeAndFlush（msg）;
 *
 * 不能阻塞
 * I/O 线程一定不能完全阻塞，因此禁止任何直接阻塞操作在你的 ChannelHandler， 有一种方法来实现这一要求。
 * 你可以指定一个 EventExecutorGroup。当添加 ChannelHandler 到ChannelPipeline。此 EventExecutorGroup 将用于获得EventExecutor，将执行所有的 ChannelHandler 的方法。
 * 这EventExecutor 将从 I/O 线程使用不同的线程，从而释放EventLoop。
 * @author wanglei
 * @date create in 10:51 2018/7/9
 */
public class RpcReqHandler extends ChannelInboundHandlerAdapter {
    private Map rpcService;


    public RpcReqHandler(Map rpcService) {
        this.rpcService = rpcService;
    }

    /**
     * Netty 内部使用回调Callback 处理事件时。一旦这样的回调被触发，事件可以由接口 ChannelHandler 的实现来处理。
     * 如下面的代码，一旦一个新的连接建立了,调用 channelActive()。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端断开链接");
        ctx.close();
    }

    /**
     * 通知处理器最后的 channelread() 是当前批处理中的最后一条消息时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    /**
     *  每个信息入站都会调用
     * @param channelHandlerContext
     * @param o
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Request) {
            Request req = (Request) o;
            Object obj = rpcService.get(req.getClassName());
            Method method = Class.forName(req.getClassName()).getMethod(req.getMethod(), req.getClasses());
            Response res = new Response();
            res.setRequestId(req.getRequestId());
            res.setCode(200);
            res.setResult(method.invoke(obj, req.getArgs()));
            //在 Netty 发送消息可以采用两种方式：直接写消息给 Channel 或者写入 ChannelHandlerContext 对象。
            // 这两者主要的区别是， 前一种方法会导致消息从 ChannelPipeline的尾部开始，而后者导致消息从 ChannelPipeline 下一个处理器开始。
            channelHandlerContext.writeAndFlush(res);
        }
    }

    /**
     * 捕获到异常时调用
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();//关闭channel
    }
}
