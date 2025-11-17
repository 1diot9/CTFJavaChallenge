package cn.hutool.extra.ssh;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ssh/JschRuntimeException.class */
public class JschRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public JschRuntimeException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public JschRuntimeException(String message) {
        super(message);
    }

    public JschRuntimeException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public JschRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public JschRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public JschRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
