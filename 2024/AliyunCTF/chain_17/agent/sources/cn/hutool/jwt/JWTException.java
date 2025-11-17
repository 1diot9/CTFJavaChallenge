package cn.hutool.jwt;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/JWTException.class */
public class JWTException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public JWTException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public JWTException(String message) {
        super(message);
    }

    public JWTException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public JWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public JWTException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public JWTException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
