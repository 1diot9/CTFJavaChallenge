package org.springframework.web.util;

import jakarta.servlet.ServletException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.Nullable;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/NestedServletException.class */
public class NestedServletException extends ServletException {
    private static final long serialVersionUID = -5292377985529381145L;

    static {
        NestedExceptionUtils.class.getName();
    }

    public NestedServletException(String msg) {
        super(msg);
    }

    public NestedServletException(@Nullable String msg, @Nullable Throwable cause) {
        super(NestedExceptionUtils.buildMessage(msg, cause), cause);
    }
}
