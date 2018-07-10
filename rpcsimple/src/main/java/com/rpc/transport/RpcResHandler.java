package com.rpc.transport;

import com.rpc.cache.ResultMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 16:37 2018/7/10
 */
public class RpcResHandler  extends SimpleChannelInboundHandler {
    private Request req;
    public RpcResHandler(Request req){
        this.req = req;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Response) {
            ResultMap.getMap().put(req.getRequestId(),(Response)o);
//            if (res.getCode() == 200) {
//                Response result = new Response();
//                result.setCode(200);
//                result.setResult("success");
//                channelHandlerContext.writeAndFlush(result);
//            }
        }
        channelHandlerContext.close();
    }
}
