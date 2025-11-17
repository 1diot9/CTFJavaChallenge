package cn.hutool.extra.pinyin;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/PinyinException.class */
public class PinyinException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public PinyinException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public PinyinException(String message) {
        super(message);
    }

    public PinyinException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public PinyinException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PinyinException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public PinyinException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
