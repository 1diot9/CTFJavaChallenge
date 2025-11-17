package cn.hutool.core.clone;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/clone/CloneRuntimeException.class */
public class CloneRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 6774837422188798989L;

    public CloneRuntimeException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public CloneRuntimeException(String message) {
        super(message);
    }

    public CloneRuntimeException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public CloneRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CloneRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
