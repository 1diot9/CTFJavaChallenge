package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianProtocolException.class */
public class HessianProtocolException extends IOException {
    private Throwable rootCause;

    public HessianProtocolException() {
    }

    public HessianProtocolException(String message) {
        super(message);
    }

    public HessianProtocolException(String message, Throwable rootCause) {
        super(message);
        this.rootCause = rootCause;
    }

    public HessianProtocolException(Throwable rootCause) {
        super(String.valueOf(rootCause));
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return getRootCause();
    }
}
