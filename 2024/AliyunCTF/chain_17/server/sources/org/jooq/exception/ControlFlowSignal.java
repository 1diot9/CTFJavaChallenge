package org.jooq.exception;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/exception/ControlFlowSignal.class */
public class ControlFlowSignal extends RuntimeException {
    public ControlFlowSignal() {
    }

    public ControlFlowSignal(String message) {
        super(message, null, false, false);
    }

    @Override // java.lang.Throwable
    public Throwable fillInStackTrace() {
        return this;
    }
}
