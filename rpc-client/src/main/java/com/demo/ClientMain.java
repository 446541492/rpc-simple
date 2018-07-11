package com.demo;

import com.demo.service.HelloService;
import com.rpc.proxy.JdkProxy;
import com.rpc.transport.NettyClient;
import io.netty.util.internal.ConcurrentSet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 13:41 2018/7/5
 */
@SpringBootApplication
public class ClientMain {
    static int count = 10000;
    static CountDownLatch countWait = new CountDownLatch(count);//并发计数器
    static AtomicLong sumTime = new AtomicLong();

    @Bean
    public NettyClient nettyClient() {
        return new NettyClient();
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ClientMain.class, args);
//        HelloService bean = (HelloService) context.getBean(HelloService.class.getName());
//        bean.say("hello world!");
        JdkProxy proxy = new JdkProxy(context);
        HelloService hello = proxy.createProxy(HelloService.class);
        CountDownLatch countBegin = new CountDownLatch(1);

        Runnable work = new Runnable() {
            @Override
            public void run() {
                try {
                    countBegin.await();//当count!=0时,阻塞
                    Long start = System.currentTimeMillis();
                    hello.say("hello ");
                    Long end = System.currentTimeMillis();
                    sumTime.addAndGet(end - start);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                } finally {
                    countWait.countDown();//初始值减一
                }

            }
        };
        for (int i = 0; i < count; i++) {
            new Thread(work).start();
        }
        countBegin.countDown();//
        try {
            countWait.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("平均耗时：" + (sumTime.longValue() / count));

    }
}
