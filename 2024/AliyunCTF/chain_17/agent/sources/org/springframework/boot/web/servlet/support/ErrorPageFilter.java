package org.springframework.boot.web.servlet.support;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/support/ErrorPageFilter.class */
public class ErrorPageFilter implements Filter, ErrorPageRegistry, Ordered {
    private static final Log logger = LogFactory.getLog((Class<?>) ErrorPageFilter.class);
    private static final String ERROR_EXCEPTION = "jakarta.servlet.error.exception";
    private static final String ERROR_EXCEPTION_TYPE = "jakarta.servlet.error.exception_type";
    private static final String ERROR_MESSAGE = "jakarta.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "jakarta.servlet.error.request_uri";
    private static final String ERROR_STATUS_CODE = "jakarta.servlet.error.status_code";
    private static final Set<Class<?>> CLIENT_ABORT_EXCEPTIONS;
    private String global;
    private final Map<Integer, String> statuses = new HashMap();
    private final Map<Class<?>, String> exceptions = new HashMap();
    private final OncePerRequestFilter delegate = new OncePerRequestFilter() { // from class: org.springframework.boot.web.servlet.support.ErrorPageFilter.1
        @Override // org.springframework.web.filter.OncePerRequestFilter
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            ErrorPageFilter.this.doFilter(request, response, chain);
        }

        @Override // org.springframework.web.filter.OncePerRequestFilter
        protected boolean shouldNotFilterAsyncDispatch() {
            return false;
        }
    };

    static {
        Set<Class<?>> clientAbortExceptions = new HashSet<>();
        addClassIfPresent(clientAbortExceptions, "org.apache.catalina.connector.ClientAbortException");
        CLIENT_ABORT_EXCEPTIONS = Collections.unmodifiableSet(clientAbortExceptions);
    }

    @Override // jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        this.delegate.init(filterConfig);
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.delegate.doFilter(request, response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        ErrorWrapperResponse wrapped = new ErrorWrapperResponse(response);
        try {
            chain.doFilter(request, wrapped);
            if (wrapped.hasErrorToSend()) {
                handleErrorStatus(request, response, wrapped.getStatus(), wrapped.getMessage());
                response.flushBuffer();
            } else if (!request.isAsyncStarted() && !response.isCommitted()) {
                response.flushBuffer();
            }
        } catch (Throwable ex) {
            Throwable exceptionToHandle = ex;
            if (ex instanceof ServletException) {
                ServletException servletException = (ServletException) ex;
                Throwable rootCause = servletException.getRootCause();
                if (rootCause != null) {
                    exceptionToHandle = rootCause;
                }
            }
            handleException(request, response, wrapped, exceptionToHandle);
            response.flushBuffer();
        }
    }

    private void handleErrorStatus(HttpServletRequest request, HttpServletResponse response, int status, String message) throws ServletException, IOException {
        if (response.isCommitted()) {
            handleCommittedResponse(request, null);
            return;
        }
        String errorPath = getErrorPath(this.statuses, Integer.valueOf(status));
        if (errorPath == null) {
            response.sendError(status, message);
            return;
        }
        response.setStatus(status);
        setErrorAttributes(request, status, message);
        request.getRequestDispatcher(errorPath).forward(request, response);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, ErrorWrapperResponse wrapped, Throwable ex) throws IOException, ServletException {
        Class<?> type = ex.getClass();
        String errorPath = getErrorPath(type);
        if (errorPath == null) {
            rethrow(ex);
        } else if (response.isCommitted()) {
            handleCommittedResponse(request, ex);
        } else {
            forwardToErrorPage(errorPath, request, wrapped, ex);
        }
    }

    private void forwardToErrorPage(String path, HttpServletRequest request, HttpServletResponse response, Throwable ex) throws ServletException, IOException {
        if (logger.isErrorEnabled()) {
            String message = "Forwarding to error page from request " + getDescription(request) + " due to exception [" + ex.getMessage() + "]";
            logger.error(message, ex);
        }
        setErrorAttributes(request, 500, ex.getMessage());
        request.setAttribute("jakarta.servlet.error.exception", ex);
        request.setAttribute("jakarta.servlet.error.exception_type", ex.getClass());
        response.reset();
        response.setStatus(500);
        request.getRequestDispatcher(path).forward(request, response);
        request.removeAttribute("jakarta.servlet.error.exception");
        request.removeAttribute("jakarta.servlet.error.exception_type");
    }

    protected String getDescription(HttpServletRequest request) {
        String pathInfo = request.getPathInfo() != null ? request.getPathInfo() : "";
        return "[" + request.getServletPath() + pathInfo + "]";
    }

    private void handleCommittedResponse(HttpServletRequest request, Throwable ex) {
        if (isClientAbortException(ex)) {
            return;
        }
        String message = "Cannot forward to error page for request " + getDescription(request) + " as the response has already been committed. As a result, the response may have the wrong status code. If your application is running on WebSphere Application Server you may be able to resolve this problem by setting com.ibm.ws.webcontainer.invokeFlushAfterService to false";
        if (ex == null) {
            logger.error(message);
        } else {
            logger.error(message, ex);
        }
    }

    private boolean isClientAbortException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        for (Class<?> candidate : CLIENT_ABORT_EXCEPTIONS) {
            if (candidate.isInstance(ex)) {
                return true;
            }
        }
        return isClientAbortException(ex.getCause());
    }

    private String getErrorPath(Map<Integer, String> map, Integer status) {
        if (map.containsKey(status)) {
            return map.get(status);
        }
        return this.global;
    }

    private String getErrorPath(Class<?> type) {
        while (type != Object.class) {
            String path = this.exceptions.get(type);
            if (path != null) {
                return path;
            }
            type = type.getSuperclass();
        }
        return this.global;
    }

    private void setErrorAttributes(HttpServletRequest request, int status, String message) {
        request.setAttribute("jakarta.servlet.error.status_code", Integer.valueOf(status));
        request.setAttribute("jakarta.servlet.error.message", message);
        request.setAttribute("jakarta.servlet.error.request_uri", request.getRequestURI());
    }

    private void rethrow(Throwable ex) throws IOException, ServletException {
        if (ex instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException) ex;
            throw runtimeException;
        }
        if (ex instanceof Error) {
            Error error = (Error) ex;
            throw error;
        }
        if (ex instanceof IOException) {
            IOException ioException = (IOException) ex;
            throw ioException;
        }
        if (ex instanceof ServletException) {
            ServletException servletException = (ServletException) ex;
            throw servletException;
        }
        throw new IllegalStateException(ex);
    }

    @Override // org.springframework.boot.web.server.ErrorPageRegistry
    public void addErrorPages(ErrorPage... errorPages) {
        for (ErrorPage errorPage : errorPages) {
            if (errorPage.isGlobal()) {
                this.global = errorPage.getPath();
            } else if (errorPage.getStatus() != null) {
                this.statuses.put(Integer.valueOf(errorPage.getStatus().value()), errorPage.getPath());
            } else {
                this.exceptions.put(errorPage.getException(), errorPage.getPath());
            }
        }
    }

    @Override // jakarta.servlet.Filter
    public void destroy() {
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return RandomValuePropertySourceEnvironmentPostProcessor.ORDER;
    }

    private static void addClassIfPresent(Collection<Class<?>> collection, String className) {
        try {
            collection.add(ClassUtils.forName(className, null));
        } catch (Throwable th) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/support/ErrorPageFilter$ErrorWrapperResponse.class */
    public static class ErrorWrapperResponse extends HttpServletResponseWrapper {
        private int status;
        private String message;
        private boolean hasErrorToSend;

        ErrorWrapperResponse(HttpServletResponse response) {
            super(response);
            this.hasErrorToSend = false;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void sendError(int status) throws IOException {
            sendError(status, null);
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void sendError(int status, String message) throws IOException {
            this.status = status;
            this.message = message;
            this.hasErrorToSend = true;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public int getStatus() {
            if (this.hasErrorToSend) {
                return this.status;
            }
            return super.getStatus();
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void flushBuffer() throws IOException {
            sendErrorIfNecessary();
            super.flushBuffer();
        }

        private void sendErrorIfNecessary() throws IOException {
            if (this.hasErrorToSend && !isCommitted()) {
                ((HttpServletResponse) getResponse()).sendError(this.status, this.message);
            }
        }

        String getMessage() {
            return this.message;
        }

        boolean hasErrorToSend() {
            return this.hasErrorToSend;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public PrintWriter getWriter() throws IOException {
            sendErrorIfNecessary();
            return super.getWriter();
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public ServletOutputStream getOutputStream() throws IOException {
            sendErrorIfNecessary();
            return super.getOutputStream();
        }
    }
}
