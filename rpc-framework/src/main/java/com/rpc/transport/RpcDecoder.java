package com.rpc.transport;

import com.rpc.SerializUtil.ObjSerialByJdk;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *  解码
 *  粘包和拆包与反序列化
 *  数据在TCP层 “流” 的时候为了保证安全和节约效率会把 “流” 做一些分包处理，比如：
     * 发送方约定了每次数据传输的最大包大小，超过该值的内容将会被拆分成两个包发送；
     * 发送端 和 接收端 约定每次发送数据包长度并随着网络状况动态调整接收窗口大小，这里也会出现拆包的情况；
 * 常用解决方案
 * 1. 定长协议
 * 指定一个报文具有固定长度。比如约定一个报文的长度是 5 字节，那么：
 * 报文：1234，只有4字节，但是还差一个怎么办呢，不足部分用空格补齐。就变为：1234 。
 * 如果不补齐空格，那么就会读到下一个报文的字节来填充上一个报文直到补齐为止，这样粘包了。
 * 定长协议的优点是使用简单，缺点很明显：浪费带宽。
 * Netty 中提供了 FixedLengthFrameDecoder ，支持把固定的长度的字节数当做一个完整的消息进行解码。
 * 2. 特殊字符分割协议
 * 很好理解，在每一个你认为是一个完整的包的尾部添加指定的特殊字符，比如：\n，\r等等。
 * 需要注意的是：约定的特殊字符要保证唯一性，不能出现在报文的正文中，否则就将正文一分为二了。
 * Netty 中提供了 DelimiterBasedFrameDecoder 根据特殊字符进行解码，LineBasedFrameDecoder默认以换行符作为分隔符。
 * 3. 变长协议
 * 变长协议的核心就是：将消息分为消息头和消息体，消息头中标识当前完整的消息体长度。
 * 发送方在发送数据之前先获取数据的二进制字节大小，然后在消息体前面添加消息大小；
 * 接收方在解析消息时先获取消息大小，之后必须读到该大小的字节数才认为是完整的消息。
 * Netty 中提供了 LengthFieldBasedFrameDecoder ，通过LengthFieldPrepender 来给实际的消息体添加 length 字段。
 *
 * @author wanglei
 * @date create in 9:54 2018/7/9
 */
public class RpcDecoder extends ByteToMessageDecoder {
    final int HEAD_LENGTH = 4;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes() < HEAD_LENGTH) {  //这个HEAD_LENGTH是我们用于表示头长度的字节数。  由于上面我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
            return;
        }
        in.markReaderIndex();                  //我们标记一下当前的readIndex的位置
        int dataLength = in.readInt();       // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        if (dataLength < 0) { // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
            ctx.close();
        }

        if (in.readableBytes() < dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[dataLength];  //  嗯，这时候，我们读到的长度，满足我们的要求了，把传送过来的数据，取出来吧~~
        in.readBytes(body);  //
        Object o = ObjSerialByJdk.convertToObject(body);  //将byte数据转化为我们需要的对象。用什么序列化，自行选择
        list.add(o);
    }
}
