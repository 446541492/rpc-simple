package com.rpc.proxy;

import com.rpc.transport.NettyClient;
import com.rpc.transport.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 16:37 2018/7/9
 */
public class JdkProxy implements InvocationHandler {

    public <T>T createProxy(Class<?> cls) {
        return (T)Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request req = new Request();
        req.setRequestId("1");
        req.setClassName(method.getDeclaringClass().getName());
        req.setArgs(args);
        req.setClasses(method.getParameterTypes());
        req.setMethod(method.getName());
        return new NettyClient().send(req).getResult();
    }
}
