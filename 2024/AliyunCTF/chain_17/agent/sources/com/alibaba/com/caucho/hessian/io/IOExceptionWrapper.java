package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/IOExceptionWrapper.class */
public class IOExceptionWrapper extends IOException {
    private Throwable _cause;

    public IOExceptionWrapper(Throwable cause) {
        super(cause.toString());
        this._cause = cause;
    }

    public IOExceptionWrapper(String msg, Throwable cause) {
        super(msg);
        this._cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this._cause;
    }
}
