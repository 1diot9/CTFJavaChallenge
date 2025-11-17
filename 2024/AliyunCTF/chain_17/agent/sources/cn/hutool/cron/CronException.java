package cn.hutool.cron;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/CronException.class */
public class CronException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public CronException(Throwable e) {
        super(e.getMessage(), e);
    }

    public CronException(String message) {
        super(message);
    }

    public CronException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public CronException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public CronException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
