package com.demo;

import com.demo.service.HelloService;
import com.rpc.bean.RpcBean;
import com.rpc.transport.NettyClient;
import com.rpc.utils.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;
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
    static int reqCount = 1000;
    static CountDownLatch countWait = new CountDownLatch(reqCount);//并发计数器
    static AtomicLong sumTime = new AtomicLong();
    static AtomicInteger resCount = new AtomicInteger(0);

    @Bean
    public NettyClient nettyClient() {
        return new NettyClient();
    }

    @Bean
    public RpcBean rpcBean() {
        return new RpcBean();
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ClientMain.class);
        SpringContextUtil.setContext(context);
//        currentTest(context);
//        sayHelloTest(context);
    }

    public static void currentTest(ApplicationContext context) {
        HelloService hello = (HelloService) context.getBean("helloService");
        CountDownLatch countBegin = new CountDownLatch(1);

        Runnable work = new Runnable() {
            @Override
            public void run() {
                try {
                    countBegin.await();//调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
                    Long start = System.currentTimeMillis();
                    hello.say("hello");
                    Long end = System.currentTimeMillis();
                    sumTime.addAndGet(end - start);
                    resCount.incrementAndGet();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                } finally {
                    countWait.countDown();//初始值减一
                }

            }
        };
        for (int i = 0; i < reqCount; i++) {
            new Thread(work).start();
        }
        countBegin.countDown();//
        try {
            countWait.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("发送请" + reqCount + "收到" + resCount + "平均耗时：" + (sumTime.longValue() / reqCount));
    }

    public static void sayHelloTest(ApplicationContext context) {
        HelloService hello = (HelloService) context.getBean("helloService");
        String res = hello.say("123");
        System.out.println(res);
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = hello.say("321");
        System.out.println(res);
    }
}
