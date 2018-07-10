package com.rpc.utils;


import java.io.IOException;
import java.util.Properties;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 15:23 2018/7/5
 */
public class PropertiesLoaderUtils {

    public static Properties getProperties(String fileName) {
        try {
            return org.springframework.core.io.support.PropertiesLoaderUtils.loadAllProperties("config.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
