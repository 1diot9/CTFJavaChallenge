package org.springframework.http.server.reactive;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletHttpHandlerAdapter.class */
public class ServletHttpHandlerAdapter implements Servlet {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final HttpHandler httpHandler;

    @Nullable
    private String servletPath;
    private static final Log logger = HttpLogging.forLogName(ServletHttpHandlerAdapter.class);
    private static final String WRITE_ERROR_ATTRIBUTE_NAME = ServletHttpHandlerAdapter.class.getName() + ".ERROR";
    private int bufferSize = 8192;
    private DataBufferFactory dataBufferFactory = DefaultDataBufferFactory.sharedInstance;

    public ServletHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull(httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    public void setBufferSize(int bufferSize) {
        Assert.isTrue(bufferSize > 0, "Buffer size must be larger than zero");
        this.bufferSize = bufferSize;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    @Nullable
    public String getServletPath() {
        return this.servletPath;
    }

    public void setDataBufferFactory(DataBufferFactory dataBufferFactory) {
        Assert.notNull(dataBufferFactory, "DataBufferFactory must not be null");
        this.dataBufferFactory = dataBufferFactory;
    }

    public DataBufferFactory getDataBufferFactory() {
        return this.dataBufferFactory;
    }

    @Override // jakarta.servlet.Servlet
    public void init(ServletConfig config) {
        this.servletPath = getServletPath(config);
    }

    private String getServletPath(ServletConfig config) {
        String name = config.getServletName();
        ServletRegistration registration = config.getServletContext().getServletRegistration(name);
        if (registration == null) {
            throw new IllegalStateException("ServletRegistration not found for Servlet '" + name + "'");
        }
        Collection<String> mappings = registration.getMappings();
        if (mappings.size() == 1) {
            String mapping = mappings.iterator().next();
            if (mapping.equals("/")) {
                return "";
            }
            if (mapping.endsWith("/*")) {
                String path = mapping.substring(0, mapping.length() - 2);
                if (!path.isEmpty() && logger.isDebugEnabled()) {
                    logger.debug("Found servlet mapping prefix '" + path + "' for '" + name + "'");
                }
                return path;
            }
        }
        throw new IllegalArgumentException("Expected a single Servlet mapping: either the default Servlet mapping (i.e. '/'), or a path based mapping (e.g. '/*', '/foo/*'). Actual mappings: " + mappings + " for Servlet '" + name + "'");
    }

    @Override // jakarta.servlet.Servlet
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (DispatcherType.ASYNC == request.getDispatcherType()) {
            Throwable ex = (Throwable) request.getAttribute(WRITE_ERROR_ATTRIBUTE_NAME);
            throw new ServletException("Failed to create response content", ex);
        }
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(-1L);
        try {
            ServletServerHttpRequest httpRequest = createRequest((HttpServletRequest) request, asyncContext);
            AsyncListener requestListener = httpRequest.getAsyncListener();
            String logPrefix = httpRequest.getLogPrefix();
            ServletServerHttpResponse wrappedResponse = createResponse((HttpServletResponse) response, asyncContext, httpRequest);
            ServerHttpResponse httpResponse = wrappedResponse;
            AsyncListener responseListener = wrappedResponse.getAsyncListener();
            if (httpRequest.getMethod() == HttpMethod.HEAD) {
                httpResponse = new HttpHeadResponseDecorator(httpResponse);
            }
            AtomicBoolean completionFlag = new AtomicBoolean();
            HandlerResultSubscriber subscriber = new HandlerResultSubscriber(asyncContext, completionFlag, logPrefix);
            asyncContext.addListener(new HttpHandlerAsyncListener(requestListener, responseListener, subscriber, completionFlag, logPrefix));
            this.httpHandler.handle(httpRequest, httpResponse).subscribe(subscriber);
        } catch (URISyntaxException ex2) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to get request  URL: " + ex2.getMessage());
            }
            ((HttpServletResponse) response).setStatus(400);
            asyncContext.complete();
        }
    }

    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
        Assert.state(this.servletPath != null, "Servlet path is not initialized");
        return new ServletServerHttpRequest(request, context, this.servletPath, getDataBufferFactory(), getBufferSize());
    }

    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
        return new ServletServerHttpResponse(response, context, getDataBufferFactory(), getBufferSize(), request);
    }

    @Override // jakarta.servlet.Servlet
    public String getServletInfo() {
        return "";
    }

    @Override // jakarta.servlet.Servlet
    @Nullable
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override // jakarta.servlet.Servlet
    public void destroy() {
    }

    private static void runIfAsyncNotComplete(AsyncContext asyncContext, AtomicBoolean isCompleted, Runnable task) {
        try {
            if (asyncContext.getRequest().isAsyncStarted() && isCompleted.compareAndSet(false, true)) {
                task.run();
            }
        } catch (IllegalStateException e) {
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletHttpHandlerAdapter$HttpHandlerAsyncListener.class */
    private static class HttpHandlerAsyncListener implements AsyncListener {
        private final AsyncListener requestAsyncListener;
        private final AsyncListener responseAsyncListener;
        private final Runnable handlerDisposeTask;
        private final AtomicBoolean completionFlag;
        private final String logPrefix;

        public HttpHandlerAsyncListener(AsyncListener requestAsyncListener, AsyncListener responseAsyncListener, Runnable handlerDisposeTask, AtomicBoolean completionFlag, String logPrefix) {
            this.requestAsyncListener = requestAsyncListener;
            this.responseAsyncListener = responseAsyncListener;
            this.handlerDisposeTask = handlerDisposeTask;
            this.completionFlag = completionFlag;
            this.logPrefix = logPrefix;
        }

        @Override // jakarta.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) {
        }

        @Override // jakarta.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) {
            if (ServletHttpHandlerAdapter.logger.isDebugEnabled()) {
                ServletHttpHandlerAdapter.logger.debug(this.logPrefix + "AsyncEvent onTimeout");
            }
            delegateTimeout(this.requestAsyncListener, event);
            delegateTimeout(this.responseAsyncListener, event);
            handleTimeoutOrError(event);
        }

        @Override // jakarta.servlet.AsyncListener
        public void onError(AsyncEvent event) {
            Object ex = event.getThrowable();
            if (ServletHttpHandlerAdapter.logger.isDebugEnabled()) {
                ServletHttpHandlerAdapter.logger.debug(this.logPrefix + "AsyncEvent onError: " + (ex != null ? ex : "<no Throwable>"));
            }
            delegateError(this.requestAsyncListener, event);
            delegateError(this.responseAsyncListener, event);
            handleTimeoutOrError(event);
        }

        @Override // jakarta.servlet.AsyncListener
        public void onComplete(AsyncEvent event) {
            delegateComplete(this.requestAsyncListener, event);
            delegateComplete(this.responseAsyncListener, event);
        }

        private static void delegateTimeout(AsyncListener listener, AsyncEvent event) {
            try {
                listener.onTimeout(event);
            } catch (Exception e) {
            }
        }

        private static void delegateError(AsyncListener listener, AsyncEvent event) {
            try {
                listener.onError(event);
            } catch (Exception e) {
            }
        }

        private static void delegateComplete(AsyncListener listener, AsyncEvent event) {
            try {
                listener.onComplete(event);
            } catch (Exception e) {
            }
        }

        private void handleTimeoutOrError(AsyncEvent event) {
            AsyncContext context = event.getAsyncContext();
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(context, this.completionFlag, () -> {
                try {
                    this.handlerDisposeTask.run();
                } finally {
                    context.complete();
                }
            });
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletHttpHandlerAdapter$HandlerResultSubscriber.class */
    private static class HandlerResultSubscriber implements Subscriber<Void>, Runnable {
        private final AsyncContext asyncContext;
        private final AtomicBoolean completionFlag;
        private final String logPrefix;

        @Nullable
        private volatile Subscription subscription;

        public HandlerResultSubscriber(AsyncContext asyncContext, AtomicBoolean completionFlag, String logPrefix) {
            this.asyncContext = asyncContext;
            this.completionFlag = completionFlag;
            this.logPrefix = logPrefix;
        }

        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Void aVoid) {
        }

        public void onError(Throwable ex) {
            if (ServletHttpHandlerAdapter.logger.isTraceEnabled()) {
                ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "onError: " + ex);
            }
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.completionFlag, () -> {
                if (this.asyncContext.getResponse().isCommitted()) {
                    ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Dispatch to container, to raise the error on servlet thread");
                    this.asyncContext.getRequest().setAttribute(ServletHttpHandlerAdapter.WRITE_ERROR_ATTRIBUTE_NAME, ex);
                    this.asyncContext.dispatch();
                } else {
                    try {
                        ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Setting ServletResponse status to 500 Server Error");
                        this.asyncContext.getResponse().resetBuffer();
                        ((HttpServletResponse) this.asyncContext.getResponse()).setStatus(500);
                    } finally {
                        this.asyncContext.complete();
                    }
                }
            });
        }

        public void onComplete() {
            if (ServletHttpHandlerAdapter.logger.isTraceEnabled()) {
                ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "onComplete");
            }
            AsyncContext asyncContext = this.asyncContext;
            AtomicBoolean atomicBoolean = this.completionFlag;
            AsyncContext asyncContext2 = this.asyncContext;
            Objects.requireNonNull(asyncContext2);
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(asyncContext, atomicBoolean, asyncContext2::complete);
        }

        @Override // java.lang.Runnable
        public void run() {
            Subscription s = this.subscription;
            if (s != null) {
                s.cancel();
            }
        }
    }
}
