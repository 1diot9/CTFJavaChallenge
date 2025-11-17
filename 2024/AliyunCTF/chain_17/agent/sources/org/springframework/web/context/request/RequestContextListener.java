package org.springframework.web.context.request;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/RequestContextListener.class */
public class RequestContextListener implements ServletRequestListener {
    private static final String REQUEST_ATTRIBUTES_ATTRIBUTE = RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES";

    @Override // jakarta.servlet.ServletRequestListener
    public void requestInitialized(ServletRequestEvent requestEvent) {
        ServletRequest servletRequest = requestEvent.getServletRequest();
        if (!(servletRequest instanceof HttpServletRequest)) {
            throw new IllegalArgumentException("Request is not an HttpServletRequest: " + requestEvent.getServletRequest());
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, attributes);
        LocaleContextHolder.setLocale(request.getLocale());
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Override // jakarta.servlet.ServletRequestListener
    public void requestDestroyed(ServletRequestEvent requestEvent) {
        ServletRequestAttributes attributes = null;
        Object reqAttr = requestEvent.getServletRequest().getAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE);
        if (reqAttr instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) reqAttr;
            attributes = servletRequestAttributes;
        }
        RequestAttributes threadAttributes = RequestContextHolder.getRequestAttributes();
        if (threadAttributes != null) {
            LocaleContextHolder.resetLocaleContext();
            RequestContextHolder.resetRequestAttributes();
            if (attributes == null && (threadAttributes instanceof ServletRequestAttributes)) {
                ServletRequestAttributes servletRequestAttributes2 = (ServletRequestAttributes) threadAttributes;
                attributes = servletRequestAttributes2;
            }
        }
        if (attributes != null) {
            attributes.requestCompleted();
        }
    }
}
