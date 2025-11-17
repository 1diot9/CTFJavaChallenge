package org.springframework.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/HandlerExecutionChain.class */
public class HandlerExecutionChain {
    private static final Log logger = LogFactory.getLog((Class<?>) HandlerExecutionChain.class);
    private final Object handler;
    private final List<HandlerInterceptor> interceptorList;
    private int interceptorIndex;

    public HandlerExecutionChain(Object handler) {
        this(handler, (HandlerInterceptor[]) null);
    }

    public HandlerExecutionChain(Object handler, @Nullable HandlerInterceptor... interceptors) {
        this(handler, (List<HandlerInterceptor>) (interceptors != null ? Arrays.asList(interceptors) : Collections.emptyList()));
    }

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptorList) {
        this.interceptorList = new ArrayList();
        this.interceptorIndex = -1;
        if (handler instanceof HandlerExecutionChain) {
            HandlerExecutionChain originalChain = (HandlerExecutionChain) handler;
            this.handler = originalChain.getHandler();
            this.interceptorList.addAll(originalChain.interceptorList);
        } else {
            this.handler = handler;
        }
        this.interceptorList.addAll(interceptorList);
    }

    public Object getHandler() {
        return this.handler;
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    public void addInterceptor(int index, HandlerInterceptor interceptor) {
        this.interceptorList.add(index, interceptor);
    }

    public void addInterceptors(HandlerInterceptor... interceptors) {
        CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
    }

    @Nullable
    public HandlerInterceptor[] getInterceptors() {
        if (this.interceptorList.isEmpty()) {
            return null;
        }
        return (HandlerInterceptor[]) this.interceptorList.toArray(new HandlerInterceptor[0]);
    }

    public List<HandlerInterceptor> getInterceptorList() {
        return !this.interceptorList.isEmpty() ? Collections.unmodifiableList(this.interceptorList) : Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (int i = 0; i < this.interceptorList.size(); i++) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            if (!interceptor.preHandle(request, response, this.handler)) {
                triggerAfterCompletion(request, response, null);
                return false;
            }
            this.interceptorIndex = i;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv) throws Exception {
        for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            interceptor.postHandle(request, response, this.handler, mv);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex) {
        for (int i = this.interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            try {
                interceptor.afterCompletion(request, response, this.handler, ex);
            } catch (Throwable ex2) {
                logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyAfterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response) {
        for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            if (interceptor instanceof AsyncHandlerInterceptor) {
                AsyncHandlerInterceptor asyncInterceptor = (AsyncHandlerInterceptor) interceptor;
                try {
                    asyncInterceptor.afterConcurrentHandlingStarted(request, response, this.handler);
                } catch (Throwable ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Interceptor [" + interceptor + "] failed in afterConcurrentHandlingStarted", ex);
                    }
                }
            }
        }
    }

    public String toString() {
        return "HandlerExecutionChain with [" + getHandler() + "] and " + this.interceptorList.size() + " interceptors";
    }
}
