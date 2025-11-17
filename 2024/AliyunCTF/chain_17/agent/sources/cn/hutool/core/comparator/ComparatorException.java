package cn.hutool.core.comparator;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/ComparatorException.class */
public class ComparatorException extends RuntimeException {
    private static final long serialVersionUID = 4475602435485521971L;

    public ComparatorException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public ComparatorException(String message) {
        super(message);
    }

    public ComparatorException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public ComparatorException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ComparatorException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
