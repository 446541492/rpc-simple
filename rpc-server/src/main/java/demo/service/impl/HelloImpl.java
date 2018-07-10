package demo.service.impl;

import com.demo.service.HelloService;
import com.rpc.annotation.RpcService;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 13:45 2018/7/5
 */
@RpcService(HelloService.class)
public class HelloImpl implements HelloService{

    @Override
    public String say(String msg) {
        System.out.println(msg);
        return msg+":res";
    }
}
