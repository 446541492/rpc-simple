package com.rpc.proxy;

import com.rpc.cache.ResultMap;
import com.rpc.transport.NettyClient;
import com.rpc.transport.Request;
import com.rpc.transport.Response;
import com.rpc.utils.SpringContextUtil;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 16:37 2018/7/9
 */
public class JdkProxy implements InvocationHandler {

    public <T> T createProxy(Class<?> cls) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Long start = System.currentTimeMillis();
        Request req = new Request();
        req.setRequestId(UUID.randomUUID().toString());
        req.setClassName(method.getDeclaringClass().getName());
        req.setArgs(args);
        req.setClasses(method.getParameterTypes());
        req.setMethod(method.getName());
        NettyClient client = SpringContextUtil.getContext().getBean(NettyClient.class);
        Response res = client.send(req);
        Object result = res.getResult();
        Long end = System.currentTimeMillis();
        Long spend = end - start;
//        System.out.println("RPC请求" + req.getRequestId() + "耗时：" + spend + "毫秒，res:" + res.getRequestId());

        return result;
    }
}
