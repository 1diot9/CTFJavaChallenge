package org.springframework.http.server;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/ServletServerHttpAsyncRequestControl.class */
public class ServletServerHttpAsyncRequestControl implements ServerHttpAsyncRequestControl, AsyncListener {
    private static final long NO_TIMEOUT_VALUE = Long.MIN_VALUE;
    private final ServletServerHttpRequest request;
    private final ServletServerHttpResponse response;

    @Nullable
    private AsyncContext asyncContext;
    private final AtomicBoolean asyncCompleted = new AtomicBoolean();

    public ServletServerHttpAsyncRequestControl(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        Assert.notNull(request, "request is required");
        Assert.notNull(response, "response is required");
        Assert.isTrue(request.getServletRequest().isAsyncSupported(), "Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml.");
        this.request = request;
        this.response = response;
    }

    @Override // org.springframework.http.server.ServerHttpAsyncRequestControl
    public boolean isStarted() {
        return this.asyncContext != null && this.request.getServletRequest().isAsyncStarted();
    }

    @Override // org.springframework.http.server.ServerHttpAsyncRequestControl
    public boolean isCompleted() {
        return this.asyncCompleted.get();
    }

    @Override // org.springframework.http.server.ServerHttpAsyncRequestControl
    public void start() {
        start(NO_TIMEOUT_VALUE);
    }

    @Override // org.springframework.http.server.ServerHttpAsyncRequestControl
    public void start(long timeout) {
        Assert.state(!isCompleted(), "Async processing has already completed");
        if (isStarted()) {
            return;
        }
        HttpServletRequest servletRequest = this.request.getServletRequest();
        HttpServletResponse servletResponse = this.response.getServletResponse();
        this.asyncContext = servletRequest.startAsync(servletRequest, servletResponse);
        this.asyncContext.addListener(this);
        if (timeout != NO_TIMEOUT_VALUE) {
            this.asyncContext.setTimeout(timeout);
        }
    }

    @Override // org.springframework.http.server.ServerHttpAsyncRequestControl
    public void complete() {
        if (this.asyncContext != null && isStarted() && !isCompleted()) {
            this.asyncContext.complete();
        }
    }

    @Override // jakarta.servlet.AsyncListener
    public void onStartAsync(AsyncEvent event) throws IOException {
    }

    @Override // jakarta.servlet.AsyncListener
    public void onTimeout(AsyncEvent event) throws IOException {
    }

    @Override // jakarta.servlet.AsyncListener
    public void onError(AsyncEvent event) throws IOException {
    }

    @Override // jakarta.servlet.AsyncListener
    public void onComplete(AsyncEvent event) throws IOException {
        this.asyncContext = null;
        this.asyncCompleted.set(true);
    }
}
