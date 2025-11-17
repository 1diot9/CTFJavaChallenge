package cn.hutool.core.exceptions;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/InvocationTargetRuntimeException.class */
public class InvocationTargetRuntimeException extends UtilException {
    public InvocationTargetRuntimeException(Throwable e) {
        super(e);
    }

    public InvocationTargetRuntimeException(String message) {
        super(message);
    }

    public InvocationTargetRuntimeException(String messageTemplate, Object... params) {
        super(messageTemplate, params);
    }

    public InvocationTargetRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InvocationTargetRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public InvocationTargetRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(throwable, messageTemplate, params);
    }
}
