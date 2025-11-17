package org.springframework.web.method.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/support/HandlerMethodReturnValueHandlerComposite.class */
public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {
    private final List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList();

    public List<HandlerMethodReturnValueHandler> getHandlers() {
        return Collections.unmodifiableList(this.returnValueHandlers);
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return getReturnValueHandler(returnType) != null;
    }

    @Nullable
    private HandlerMethodReturnValueHandler getReturnValueHandler(MethodParameter returnType) {
        for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
            if (handler.supportsReturnType(returnType)) {
                return handler;
            }
        }
        return null;
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        HandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
        }
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    @Nullable
    private HandlerMethodReturnValueHandler selectHandler(@Nullable Object value, MethodParameter returnType) {
        boolean isAsyncValue = isAsyncReturnValue(value, returnType);
        for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
            if (!isAsyncValue || (handler instanceof AsyncHandlerMethodReturnValueHandler)) {
                if (handler.supportsReturnType(returnType)) {
                    return handler;
                }
            }
        }
        return null;
    }

    private boolean isAsyncReturnValue(@Nullable Object value, MethodParameter returnType) {
        for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
            if (handler instanceof AsyncHandlerMethodReturnValueHandler) {
                AsyncHandlerMethodReturnValueHandler asyncHandler = (AsyncHandlerMethodReturnValueHandler) handler;
                if (asyncHandler.isAsyncReturnValue(value, returnType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public HandlerMethodReturnValueHandlerComposite addHandler(HandlerMethodReturnValueHandler handler) {
        this.returnValueHandlers.add(handler);
        return this;
    }

    public HandlerMethodReturnValueHandlerComposite addHandlers(@Nullable List<? extends HandlerMethodReturnValueHandler> handlers) {
        if (handlers != null) {
            this.returnValueHandlers.addAll(handlers);
        }
        return this;
    }
}
