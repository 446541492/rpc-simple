package com.rpc.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc注解
 *
 * @author wanglei
 * @date create in 14:08 2018/7/5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service // spring的注解
public @interface RpcService {
    Class value();//指明是哪个接口
}
