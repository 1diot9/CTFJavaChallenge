package org.apache.catalina.core;

import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/AsyncListenerWrapper.class */
public class AsyncListenerWrapper {
    private AsyncListener listener = null;
    private ServletRequest servletRequest = null;
    private ServletResponse servletResponse = null;

    public void fireOnStartAsync(AsyncEvent event) throws IOException {
        this.listener.onStartAsync(customizeEvent(event));
    }

    public void fireOnComplete(AsyncEvent event) throws IOException {
        this.listener.onComplete(customizeEvent(event));
    }

    public void fireOnTimeout(AsyncEvent event) throws IOException {
        this.listener.onTimeout(customizeEvent(event));
    }

    public void fireOnError(AsyncEvent event) throws IOException {
        this.listener.onError(customizeEvent(event));
    }

    public AsyncListener getListener() {
        return this.listener;
    }

    public void setListener(AsyncListener listener) {
        this.listener = listener;
    }

    public void setServletRequest(ServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public void setServletResponse(ServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    private AsyncEvent customizeEvent(AsyncEvent event) {
        if (this.servletRequest != null && this.servletResponse != null) {
            return new AsyncEvent(event.getAsyncContext(), this.servletRequest, this.servletResponse, event.getThrowable());
        }
        return event;
    }
}
