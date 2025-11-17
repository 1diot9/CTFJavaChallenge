package cn.hutool.extra.mail;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/mail/MailException.class */
public class MailException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public MailException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public MailException(String message) {
        super(message);
    }

    public MailException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public MailException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MailException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public MailException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
