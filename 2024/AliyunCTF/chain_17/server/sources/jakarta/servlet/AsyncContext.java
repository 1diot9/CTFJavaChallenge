package jakarta.servlet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/AsyncContext.class */
public interface AsyncContext {
    public static final String ASYNC_REQUEST_URI = "jakarta.servlet.async.request_uri";
    public static final String ASYNC_CONTEXT_PATH = "jakarta.servlet.async.context_path";
    public static final String ASYNC_MAPPING = "jakarta.servlet.async.mapping";
    public static final String ASYNC_PATH_INFO = "jakarta.servlet.async.path_info";
    public static final String ASYNC_SERVLET_PATH = "jakarta.servlet.async.servlet_path";
    public static final String ASYNC_QUERY_STRING = "jakarta.servlet.async.query_string";

    ServletRequest getRequest();

    ServletResponse getResponse();

    boolean hasOriginalRequestAndResponse();

    void dispatch();

    void dispatch(String str);

    void dispatch(ServletContext servletContext, String str);

    void complete();

    void start(Runnable runnable);

    void addListener(AsyncListener asyncListener);

    void addListener(AsyncListener asyncListener, ServletRequest servletRequest, ServletResponse servletResponse);

    <T extends AsyncListener> T createListener(Class<T> cls) throws ServletException;

    void setTimeout(long j);

    long getTimeout();
}
