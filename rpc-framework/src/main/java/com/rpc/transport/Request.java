package com.rpc.transport;

import java.io.Serializable;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 10:17 2018/7/9
 */
public class Request implements Serializable {
    private String requestId;
    private String className;
    private String method;
    private Object[] args;
    private Class[] classes;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class[] getClasses() {
        return classes;
    }

    public void setClasses(Class[] classes) {
        this.classes = classes;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
