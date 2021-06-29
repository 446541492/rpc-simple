package com.rpc.transport;

import com.rpc.SerializUtil.ObjSerialByJdk;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义协议编码（消息头和消息体）与序列化
 * 网络传输（网络协议是基于二进制的，内存中的参数的值要序列化成二进制的形式）
 * 1.无法跨语言。这应该是java序列化最致命的问题了。
 * 由于java序列化是java内部私有的协议，其他语言不支持，导致别的语言无法反序列化，这严重阻碍了它的应用。
 * 关于跨语言问题，也就是对象传输，一般都采用json字符串。
 * 2.序列后的码流太大。java序列化的大小是二进制编码的5倍多！
 * 3.序列化性能太低。java序列化的性能只有二进制编码的6.17倍，可见java序列化性能实在太差了。
 * 主流的编解码框架:
 *
 * ①JBoss的Marshalling包：
 * 对jdk默认的序列化进行了优化，又保持跟java.io.Serializable接口的兼容，同时增加了一些可调的参数和附加特性，
 * 并且这些参数和特性可通过工厂类的配置
 * 1.可拔插的类解析器，提供更加便捷的类加载定制策略，通过一个接口即可实现定制。
 * 2.可拔插的对象替换技术，不需要通过继承的方式。
 * 3.可拔插的预定义类缓存表，可以减少序列化的字节数组长度，提升常用类型的对象序列化性能。
 * 4.无须实现java.io.Serializable接口
 * 5.通过缓存技术提升对象的序列化性能。
 * 6.使用非常简单
 *
 * ②google的Protobuf
 *
 * ③基于Protobuf的Kyro
 *
 * ④MessagePack框架
 * @author wanglei
 * @date create in 9:58 2018/7/9
 */
public class RpcEncoder extends MessageToByteEncoder {
    /**
     *
     * @param channelHandlerContext
     * @param o
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] body = ObjSerialByJdk.convertToBytes(o);  //将对象转换为byte，具体用什么进行序列化，你们自行选择。可以使用我上面说的一些
        int dataLength = body.length;  //读取消息的长度
        byteBuf.writeInt(dataLength);  //先将消息长度写入，也就是消息头
        byteBuf.writeBytes(body);  //消息体中包含我们要发送的数据
    }
}
