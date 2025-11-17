package jakarta.servlet;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/RequestDispatcher.class */
public interface RequestDispatcher {
    public static final String FORWARD_REQUEST_URI = "jakarta.servlet.forward.request_uri";
    public static final String FORWARD_CONTEXT_PATH = "jakarta.servlet.forward.context_path";
    public static final String FORWARD_MAPPING = "jakarta.servlet.forward.mapping";
    public static final String FORWARD_PATH_INFO = "jakarta.servlet.forward.path_info";
    public static final String FORWARD_SERVLET_PATH = "jakarta.servlet.forward.servlet_path";
    public static final String FORWARD_QUERY_STRING = "jakarta.servlet.forward.query_string";
    public static final String INCLUDE_REQUEST_URI = "jakarta.servlet.include.request_uri";
    public static final String INCLUDE_CONTEXT_PATH = "jakarta.servlet.include.context_path";
    public static final String INCLUDE_PATH_INFO = "jakarta.servlet.include.path_info";
    public static final String INCLUDE_MAPPING = "jakarta.servlet.include.mapping";
    public static final String INCLUDE_SERVLET_PATH = "jakarta.servlet.include.servlet_path";
    public static final String INCLUDE_QUERY_STRING = "jakarta.servlet.include.query_string";
    public static final String ERROR_EXCEPTION = "jakarta.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "jakarta.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "jakarta.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "jakarta.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "jakarta.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "jakarta.servlet.error.status_code";

    void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;
}
