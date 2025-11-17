package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/HandlerExceptionResolverComposite.class */
public class HandlerExceptionResolverComposite implements HandlerExceptionResolver, Ordered {

    @Nullable
    private List<HandlerExceptionResolver> resolvers;
    private int order = Integer.MAX_VALUE;

    public void setExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        this.resolvers = exceptionResolvers;
    }

    public List<HandlerExceptionResolver> getExceptionResolvers() {
        return this.resolvers != null ? Collections.unmodifiableList(this.resolvers) : Collections.emptyList();
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.web.servlet.HandlerExceptionResolver
    @Nullable
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        if (this.resolvers != null) {
            for (HandlerExceptionResolver handlerExceptionResolver : this.resolvers) {
                ModelAndView mav = handlerExceptionResolver.resolveException(request, response, handler, ex);
                if (mav != null) {
                    return mav;
                }
            }
            return null;
        }
        return null;
    }
}
