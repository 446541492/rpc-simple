package com.rpc.cache;

import com.rpc.transport.Response;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 16:39 2018/7/10
 */
public class ResultMap {
    private static ConcurrentHashMap<String,Response> resultMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String,Response> getMap(){
        return resultMap;
    }
}
