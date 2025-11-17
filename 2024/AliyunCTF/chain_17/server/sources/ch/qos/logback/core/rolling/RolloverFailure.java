package ch.qos.logback.core.rolling;

import ch.qos.logback.core.LogbackException;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/RolloverFailure.class */
public class RolloverFailure extends LogbackException {
    private static final long serialVersionUID = -4407533730831239458L;

    public RolloverFailure(String msg) {
        super(msg);
    }

    public RolloverFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
