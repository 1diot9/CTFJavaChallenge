package org.springframework.web.context.support;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletRequestHandledEvent.class */
public class ServletRequestHandledEvent extends RequestHandledEvent {
    private final String requestUrl;
    private final String clientAddress;
    private final String method;
    private final String servletName;
    private final int statusCode;

    public ServletRequestHandledEvent(Object source, String requestUrl, String clientAddress, String method, String servletName, @Nullable String sessionId, @Nullable String userName, long processingTimeMillis) {
        super(source, sessionId, userName, processingTimeMillis);
        this.requestUrl = requestUrl;
        this.clientAddress = clientAddress;
        this.method = method;
        this.servletName = servletName;
        this.statusCode = -1;
    }

    public ServletRequestHandledEvent(Object source, String requestUrl, String clientAddress, String method, String servletName, @Nullable String sessionId, @Nullable String userName, long processingTimeMillis, @Nullable Throwable failureCause) {
        super(source, sessionId, userName, processingTimeMillis, failureCause);
        this.requestUrl = requestUrl;
        this.clientAddress = clientAddress;
        this.method = method;
        this.servletName = servletName;
        this.statusCode = -1;
    }

    public ServletRequestHandledEvent(Object source, String requestUrl, String clientAddress, String method, String servletName, @Nullable String sessionId, @Nullable String userName, long processingTimeMillis, @Nullable Throwable failureCause, int statusCode) {
        super(source, sessionId, userName, processingTimeMillis, failureCause);
        this.requestUrl = requestUrl;
        this.clientAddress = clientAddress;
        this.method = method;
        this.servletName = servletName;
        this.statusCode = statusCode;
    }

    public String getRequestUrl() {
        return this.requestUrl;
    }

    public String getClientAddress() {
        return this.clientAddress;
    }

    public String getMethod() {
        return this.method;
    }

    public String getServletName() {
        return this.servletName;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    @Override // org.springframework.web.context.support.RequestHandledEvent
    public String getShortDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("url=[").append(getRequestUrl()).append("]; ");
        sb.append("client=[").append(getClientAddress()).append("]; ");
        sb.append(super.getShortDescription());
        return sb.toString();
    }

    @Override // org.springframework.web.context.support.RequestHandledEvent
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("url=[").append(getRequestUrl()).append("]; ");
        sb.append("client=[").append(getClientAddress()).append("]; ");
        sb.append("method=[").append(getMethod()).append("]; ");
        sb.append("status=[").append(getStatusCode()).append("]; ");
        sb.append("servlet=[").append(getServletName()).append("]; ");
        sb.append(super.getDescription());
        return sb.toString();
    }

    @Override // org.springframework.web.context.support.RequestHandledEvent, java.util.EventObject
    public String toString() {
        return "ServletRequestHandledEvent: " + getDescription();
    }
}
