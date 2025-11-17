package cn.hutool.extra.ftp;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ftp/FtpException.class */
public class FtpException extends RuntimeException {
    private static final long serialVersionUID = -8490149159895201756L;

    public FtpException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public FtpException(String message) {
        super(message);
    }

    public FtpException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public FtpException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public FtpException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public FtpException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
