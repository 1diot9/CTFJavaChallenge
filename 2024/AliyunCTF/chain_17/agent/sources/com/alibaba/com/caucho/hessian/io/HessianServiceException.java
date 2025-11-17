package com.alibaba.com.caucho.hessian.io;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianServiceException.class */
public class HessianServiceException extends Exception {
    private String code;
    private Object detail;

    public HessianServiceException() {
    }

    public HessianServiceException(String message, String code, Object detail) {
        super(message);
        this.code = code;
        this.detail = detail;
    }

    public String getCode() {
        return this.code;
    }

    public Object getDetail() {
        return this.detail;
    }
}
