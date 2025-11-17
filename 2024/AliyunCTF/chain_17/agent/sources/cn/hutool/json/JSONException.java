package cn.hutool.json;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONException.class */
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public JSONException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public JSONException(String message) {
        super(message);
    }

    public JSONException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public JSONException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
