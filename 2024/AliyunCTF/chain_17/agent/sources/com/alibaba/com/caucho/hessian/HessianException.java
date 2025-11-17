package com.alibaba.com.caucho.hessian;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/HessianException.class */
public class HessianException extends RuntimeException {
    public HessianException() {
    }

    public HessianException(String message) {
        super(message);
    }

    public HessianException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public HessianException(Throwable rootCause) {
        super(rootCause);
    }
}
