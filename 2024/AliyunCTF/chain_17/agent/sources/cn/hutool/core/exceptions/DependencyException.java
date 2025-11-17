package cn.hutool.core.exceptions;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/DependencyException.class */
public class DependencyException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public DependencyException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public DependencyException(String message) {
        super(message);
    }

    public DependencyException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public DependencyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DependencyException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public DependencyException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
