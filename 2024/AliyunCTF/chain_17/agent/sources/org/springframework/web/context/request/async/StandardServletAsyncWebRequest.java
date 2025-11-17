package org.springframework.web.context.request.async;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.ServletWebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/StandardServletAsyncWebRequest.class */
public class StandardServletAsyncWebRequest extends ServletWebRequest implements AsyncWebRequest, AsyncListener {
    private final AtomicBoolean asyncCompleted;
    private final List<Runnable> timeoutHandlers;
    private final List<Consumer<Throwable>> exceptionHandlers;
    private final List<Runnable> completionHandlers;

    @Nullable
    private Long timeout;

    @Nullable
    private AsyncContext asyncContext;

    public StandardServletAsyncWebRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        this.asyncCompleted = new AtomicBoolean();
        this.timeoutHandlers = new ArrayList();
        this.exceptionHandlers = new ArrayList();
        this.completionHandlers = new ArrayList();
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public void setTimeout(Long timeout) {
        Assert.state(!isAsyncStarted(), "Cannot change the timeout with concurrent handling in progress");
        this.timeout = timeout;
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public void addTimeoutHandler(Runnable timeoutHandler) {
        this.timeoutHandlers.add(timeoutHandler);
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public void addErrorHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandlers.add(exceptionHandler);
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public void addCompletionHandler(Runnable runnable) {
        this.completionHandlers.add(runnable);
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public boolean isAsyncStarted() {
        return this.asyncContext != null && getRequest().isAsyncStarted();
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public boolean isAsyncComplete() {
        return this.asyncCompleted.get();
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public void startAsync() {
        Assert.state(getRequest().isAsyncSupported(), "Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml.");
        Assert.state(!isAsyncComplete(), "Async processing has already completed");
        if (isAsyncStarted()) {
            return;
        }
        this.asyncContext = getRequest().startAsync(getRequest(), getResponse());
        this.asyncContext.addListener(this);
        if (this.timeout != null) {
            this.asyncContext.setTimeout(this.timeout.longValue());
        }
    }

    @Override // org.springframework.web.context.request.async.AsyncWebRequest
    public void dispatch() {
        Assert.state(this.asyncContext != null, "Cannot dispatch without an AsyncContext");
        this.asyncContext.dispatch();
    }

    @Override // jakarta.servlet.AsyncListener
    public void onStartAsync(AsyncEvent event) throws IOException {
    }

    @Override // jakarta.servlet.AsyncListener
    public void onTimeout(AsyncEvent event) throws IOException {
        this.timeoutHandlers.forEach((v0) -> {
            v0.run();
        });
    }

    @Override // jakarta.servlet.AsyncListener
    public void onError(AsyncEvent event) throws IOException {
        this.exceptionHandlers.forEach(consumer -> {
            consumer.accept(event.getThrowable());
        });
    }

    @Override // jakarta.servlet.AsyncListener
    public void onComplete(AsyncEvent event) throws IOException {
        this.completionHandlers.forEach((v0) -> {
            v0.run();
        });
        this.asyncContext = null;
        this.asyncCompleted.set(true);
    }
}
