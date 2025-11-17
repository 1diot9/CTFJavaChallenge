package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/support/AsyncHandlerMethodReturnValueHandler.class */
public interface AsyncHandlerMethodReturnValueHandler extends HandlerMethodReturnValueHandler {
    boolean isAsyncReturnValue(@Nullable Object returnValue, MethodParameter returnType);
}
