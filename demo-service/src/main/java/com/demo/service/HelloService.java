package com.demo.service;

import com.demo.model.UserEntity;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 10:28 2018/7/10
 */
public interface HelloService {
    String say(String msg);

    UserEntity userSay(UserEntity userEntity);
}
