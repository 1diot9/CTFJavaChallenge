package org.springframework.web.context.request;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/AsyncWebRequestInterceptor.class */
public interface AsyncWebRequestInterceptor extends WebRequestInterceptor {
    void afterConcurrentHandlingStarted(WebRequest request);
}
