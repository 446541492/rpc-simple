package com.rpc.transport;

import java.io.Serializable;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 10:41 2018/7/9
 */
public class Response implements Serializable{

    private String requestId;
    private int code;
    private Object result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
