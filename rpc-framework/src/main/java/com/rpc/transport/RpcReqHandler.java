package com.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 业务处理类  处理进站数据和所有状态更改事件
 * SimpleChannelInboundHandler vs. ChannelInboundHandler
 * 建议SimpleChannelInboundHandler   因为需要尽快释放数据容器ByteBuf，Netty使用引用计数器来处理池化的 ByteBuf。所以当 ByteBuf 完全处理后，要确保引用计数器被调整。
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
public class RpcReqHandler extends SimpleChannelInboundHandler {
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

    /**
     *   channel 注册到一个 EventLoop. channel已创建但未注册到一个 EventLoop.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    /**
     *   channel已创建但未注册到一个 EventLoop.
     * @param ctx
     * @throws Exception
     */
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

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Request) {
            Request req = (Request) o;
            Object obj = rpcService.get(req.getClassName());
            Method method = Class.forName(req.getClassName()).getMethod(req.getMethod(), req.getClasses());
            Response res = new Response();
            res.setRequestId(req.getRequestId());
            res.setCode(200);
            res.setResult(method.invoke(obj, req.getArgs()));
            //Channel, ChannelPipeline .writeAndFlush(res)  或  channelHandlerContext.writeAndFlush(res);
            // 这两者主要的区别是，
            // 前一种方法会导致消息从 事件传递给 ChannelPipeline 的第一个 ChannelHandler，然后ChannelHandler 通过关联的 ChannelHandlerContext 传递事件给 ChannelPipeline 中的 下一个。
            //后一种 ChannelHandlerContext 方法调用 事件发送到了下一个 ChannelHandler 经过最后一个ChannelHandler后，事件从 ChannelPipeline 移除
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
