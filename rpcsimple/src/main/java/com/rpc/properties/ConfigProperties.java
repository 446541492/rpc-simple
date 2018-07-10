package com.rpc.properties;


import com.rpc.utils.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 15:27 2018/7/6
 */
public class ConfigProperties {
    private static Properties prop = PropertiesLoaderUtils.getProperties("config.properties");

    public static String getRpcPort(){
        return prop.getProperty("rpc.port");
    }

    public static String getZookeeperServer(){
        return prop.getProperty("zookeeper.address");
    }
}
