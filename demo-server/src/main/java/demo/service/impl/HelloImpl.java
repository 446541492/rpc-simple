package demo.service.impl;

import com.demo.service.HelloService;
import com.rpc.annotation.RpcService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 13:45 2018/7/5
 */
@RpcService(HelloService.class)
public class HelloImpl implements HelloService{
    AtomicInteger sum = new AtomicInteger(0);
    @Override
    public String say(String msg) {
        System.out.println("收到第"+sum.incrementAndGet()+"条消息，msg:"+msg);
        return "服务回复："+msg;
    }
}
