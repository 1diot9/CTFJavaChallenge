package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/DispatcherServletWebRequest.class */
public class DispatcherServletWebRequest extends ServletWebRequest {
    public DispatcherServletWebRequest(HttpServletRequest request) {
        super(request);
    }

    public DispatcherServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override // org.springframework.web.context.request.ServletWebRequest, org.springframework.web.context.request.WebRequest
    public Locale getLocale() {
        return RequestContextUtils.getLocale(getRequest());
    }
}
