package com.rpc.transport;

import com.rpc.SerializUtil.ObjSerialByJdk;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义协议 响应编码
 *
 * @author wanglei
 * @date create in 9:58 2018/7/9
 */
public class RpcEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] body = ObjSerialByJdk.convertToBytes(o);  //将对象转换为byte，伪代码，具体用什么进行序列化，你们自行选择。可以使用我上面说的一些
        int dataLength = body.length;  //读取消息的长度
        byteBuf.writeInt(dataLength);  //先将消息长度写入，也就是消息头
        byteBuf.writeBytes(body);  //消息体中包含我们要发送的数据
    }
}
