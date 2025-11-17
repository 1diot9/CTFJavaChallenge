package cn.hutool.db;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/DbRuntimeException.class */
public class DbRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 3624487785708765623L;

    public DbRuntimeException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public DbRuntimeException(String message) {
        super(message);
    }

    public DbRuntimeException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public DbRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DbRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public DbRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
