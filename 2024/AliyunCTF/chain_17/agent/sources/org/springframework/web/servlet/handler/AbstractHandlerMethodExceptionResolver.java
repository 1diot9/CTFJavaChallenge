package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodExceptionResolver.class */
public abstract class AbstractHandlerMethodExceptionResolver extends AbstractHandlerExceptionResolver {
    @Nullable
    protected abstract ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handlerMethod, Exception ex);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    public boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        if (handler == null) {
            return super.shouldApplyTo(request, null);
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return super.shouldApplyTo(request, handlerMethod.getBean());
        }
        if (hasGlobalExceptionHandlers() && hasHandlerMappings()) {
            return super.shouldApplyTo(request, handler);
        }
        return false;
    }

    protected boolean hasGlobalExceptionHandlers() {
        return false;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    @Nullable
    protected final ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        HandlerMethod handlerMethod;
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            handlerMethod = hm;
        } else {
            handlerMethod = null;
        }
        HandlerMethod handlerMethod2 = handlerMethod;
        return doResolveHandlerMethodException(request, response, handlerMethod2, ex);
    }
}
