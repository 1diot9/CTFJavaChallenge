package jakarta.servlet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/AsyncEvent.class */
public class AsyncEvent {
    private final AsyncContext context;
    private final ServletRequest request;
    private final ServletResponse response;
    private final Throwable throwable;

    public AsyncEvent(AsyncContext context) {
        this.context = context;
        this.request = null;
        this.response = null;
        this.throwable = null;
    }

    public AsyncEvent(AsyncContext context, ServletRequest request, ServletResponse response) {
        this.context = context;
        this.request = request;
        this.response = response;
        this.throwable = null;
    }

    public AsyncEvent(AsyncContext context, Throwable throwable) {
        this.context = context;
        this.throwable = throwable;
        this.request = null;
        this.response = null;
    }

    public AsyncEvent(AsyncContext context, ServletRequest request, ServletResponse response, Throwable throwable) {
        this.context = context;
        this.request = request;
        this.response = response;
        this.throwable = throwable;
    }

    public AsyncContext getAsyncContext() {
        return this.context;
    }

    public ServletRequest getSuppliedRequest() {
        return this.request;
    }

    public ServletResponse getSuppliedResponse() {
        return this.response;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
