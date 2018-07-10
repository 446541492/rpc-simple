package com.demo;

import com.rpc.bean.RpcBean;
import com.rpc.transport.NettyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 13:41 2018/7/5
 */
@SpringBootApplication
public class ClientMain {
    @Bean
    public RpcBean rpcBean() {
        return new RpcBean();
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ClientMain.class, args);
//        HelloService bean = (HelloService) context.getBean(HelloService.class.getName());
//        bean.say("hello world!");

    }
}
