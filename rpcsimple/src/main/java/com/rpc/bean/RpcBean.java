package com.rpc.bean;

import com.demo.service.HelloService;
import com.rpc.proxy.JdkProxy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 15:18 2018/7/10
 */
public class RpcBean  implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JdkProxy proxy = new JdkProxy();
        HelloService hello = proxy.createProxy(HelloService.class);

//        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
//        beanFactory.applyBeanPostProcessorsAfterInitialization(hello, hello.getClass().getName());
//        beanFactory.registerSingleton(hello.getClass().getName(), hello);
        String msg = hello.say("hello ");
        System.out.println(msg);
    }
}
