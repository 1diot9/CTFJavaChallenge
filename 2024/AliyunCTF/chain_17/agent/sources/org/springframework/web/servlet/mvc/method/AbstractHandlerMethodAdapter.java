package org.springframework.web.servlet.mvc.method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/AbstractHandlerMethodAdapter.class */
public abstract class AbstractHandlerMethodAdapter extends WebContentGenerator implements HandlerAdapter, Ordered {
    private int order;

    protected abstract boolean supportsInternal(HandlerMethod handlerMethod);

    @Nullable
    protected abstract ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception;

    @Deprecated
    protected abstract long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod);

    public AbstractHandlerMethodAdapter() {
        super(false);
        this.order = Integer.MAX_VALUE;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    public final boolean supports(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (supportsInternal(handlerMethod)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    @Nullable
    public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return handleInternal(request, response, (HandlerMethod) handler);
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    public final long getLastModified(HttpServletRequest request, Object handler) {
        return getLastModifiedInternal(request, (HandlerMethod) handler);
    }
}
