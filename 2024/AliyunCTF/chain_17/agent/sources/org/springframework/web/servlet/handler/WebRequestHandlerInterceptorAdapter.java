package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/WebRequestHandlerInterceptorAdapter.class */
public class WebRequestHandlerInterceptorAdapter implements AsyncHandlerInterceptor {
    private final WebRequestInterceptor requestInterceptor;

    public WebRequestHandlerInterceptorAdapter(WebRequestInterceptor requestInterceptor) {
        Assert.notNull(requestInterceptor, "WebRequestInterceptor must not be null");
        this.requestInterceptor = requestInterceptor;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.requestInterceptor.preHandle(new DispatcherServletWebRequest(request, response));
        return true;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        this.requestInterceptor.postHandle(new DispatcherServletWebRequest(request, response), (modelAndView == null || modelAndView.wasCleared()) ? null : modelAndView.getModelMap());
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        this.requestInterceptor.afterCompletion(new DispatcherServletWebRequest(request, response), ex);
    }

    @Override // org.springframework.web.servlet.AsyncHandlerInterceptor
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        WebRequestInterceptor webRequestInterceptor = this.requestInterceptor;
        if (webRequestInterceptor instanceof AsyncWebRequestInterceptor) {
            AsyncWebRequestInterceptor asyncInterceptor = (AsyncWebRequestInterceptor) webRequestInterceptor;
            DispatcherServletWebRequest webRequest = new DispatcherServletWebRequest(request, response);
            asyncInterceptor.afterConcurrentHandlingStarted(webRequest);
        }
    }
}
