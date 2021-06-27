package com.rpc.utils;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端请求缓存，channel
 *
 * @author wanglei
 * @date create in 14:40 2018/7/11
 */
public class ChannelUtils {
    public static final AttributeKey<ConcurrentHashMap<String, Object>> DATA_MAP_ATTRIBUTEKEY = AttributeKey.valueOf("dataMap");
    public static void initDataMap(Channel channel){
        Attribute<ConcurrentHashMap<String,Object>> attribute = channel.attr(DATA_MAP_ATTRIBUTEKEY);
        ConcurrentHashMap<String, Object> dataMap = new ConcurrentHashMap<>();
        attribute.set(dataMap);
    }
    public static <T> void putCallback2DataMap(Channel channel, String seq, T callback) {
        channel.attr(DATA_MAP_ATTRIBUTEKEY).get().put(seq, callback);
    }

    public static <T> T removeCallback(Channel channel, String seq) {
        return (T) channel.attr(DATA_MAP_ATTRIBUTEKEY).get().remove(seq);
    }
}
