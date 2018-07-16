package com.rpc.utils;

import org.springframework.context.ApplicationContext;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 17:30 2018/7/13
 */
public class SpringContextUtil{
        private static ApplicationContext context;

        public static ApplicationContext getContext() {
            return context;
        }

        public static void setContext(ApplicationContext context) {
            SpringContextUtil.context = context;
        }
}
