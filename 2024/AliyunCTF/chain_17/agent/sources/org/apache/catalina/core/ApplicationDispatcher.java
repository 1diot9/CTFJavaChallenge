package org.apache.catalina.core;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletResponseWrapper;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.apache.catalina.AsyncDispatcher;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationDispatcher.class */
public final class ApplicationDispatcher implements AsyncDispatcher, RequestDispatcher {
    private final Context context;
    private final String name;
    private final String pathInfo;
    private final String queryString;
    private final String requestURI;
    private final String servletPath;
    private final HttpServletMapping mapping;
    private static final StringManager sm = StringManager.getManager((Class<?>) ApplicationDispatcher.class);
    private final Wrapper wrapper;

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationDispatcher$PrivilegedForward.class */
    protected class PrivilegedForward implements PrivilegedExceptionAction<Void> {
        private final ServletRequest request;
        private final ServletResponse response;

        PrivilegedForward(ServletRequest request, ServletResponse response) {
            this.request = request;
            this.response = response;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            ApplicationDispatcher.this.doForward(this.request, this.response);
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationDispatcher$PrivilegedInclude.class */
    protected class PrivilegedInclude implements PrivilegedExceptionAction<Void> {
        private final ServletRequest request;
        private final ServletResponse response;

        PrivilegedInclude(ServletRequest request, ServletResponse response) {
            this.request = request;
            this.response = response;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws ServletException, IOException {
            ApplicationDispatcher.this.doInclude(this.request, this.response);
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationDispatcher$PrivilegedDispatch.class */
    protected class PrivilegedDispatch implements PrivilegedExceptionAction<Void> {
        private final ServletRequest request;
        private final ServletResponse response;

        PrivilegedDispatch(ServletRequest request, ServletResponse response) {
            this.request = request;
            this.response = response;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws ServletException, IOException {
            ApplicationDispatcher.this.doDispatch(this.request, this.response);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationDispatcher$State.class */
    public static class State {
        ServletRequest outerRequest;
        ServletResponse outerResponse;
        boolean including;
        ServletRequest wrapRequest = null;
        ServletResponse wrapResponse = null;
        HttpServletRequest hrequest = null;
        HttpServletResponse hresponse = null;

        State(ServletRequest request, ServletResponse response, boolean including) {
            this.outerRequest = null;
            this.outerResponse = null;
            this.including = false;
            this.outerRequest = request;
            this.outerResponse = response;
            this.including = including;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationDispatcher(Wrapper wrapper, String requestURI, String servletPath, String pathInfo, String queryString, HttpServletMapping mapping, String name) {
        this.wrapper = wrapper;
        this.context = (Context) wrapper.getParent();
        this.requestURI = requestURI;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
        this.queryString = queryString;
        this.mapping = mapping;
        this.name = name;
    }

    @Override // jakarta.servlet.RequestDispatcher
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                PrivilegedForward dp = new PrivilegedForward(request, response);
                AccessController.doPrivileged(dp);
                return;
            } catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw ((ServletException) e);
                }
                throw ((IOException) e);
            }
        }
        doForward(request, response);
    }

    private void doForward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (response.isCommitted()) {
            throw new IllegalStateException(sm.getString("applicationDispatcher.forward.ise"));
        }
        try {
            response.resetBuffer();
            State state = new State(request, response, false);
            if (this.context.getDispatcherWrapsSameObject()) {
                checkSameObjects(request, response);
            }
            wrapResponse(state);
            if (this.servletPath == null && this.pathInfo == null) {
                ApplicationHttpRequest wrequest = (ApplicationHttpRequest) wrapRequest(state);
                HttpServletRequest hrequest = state.hrequest;
                wrequest.setRequestURI(hrequest.getRequestURI());
                wrequest.setContextPath(hrequest.getContextPath());
                wrequest.setServletPath(hrequest.getServletPath());
                wrequest.setPathInfo(hrequest.getPathInfo());
                wrequest.setQueryString(hrequest.getQueryString());
                processRequest(request, response, state);
            } else {
                ApplicationHttpRequest wrequest2 = (ApplicationHttpRequest) wrapRequest(state);
                HttpServletRequest hrequest2 = state.hrequest;
                if (hrequest2.getAttribute("jakarta.servlet.forward.request_uri") == null) {
                    wrequest2.setAttribute("jakarta.servlet.forward.request_uri", hrequest2.getRequestURI());
                    wrequest2.setAttribute("jakarta.servlet.forward.context_path", hrequest2.getContextPath());
                    wrequest2.setAttribute("jakarta.servlet.forward.servlet_path", hrequest2.getServletPath());
                    wrequest2.setAttribute("jakarta.servlet.forward.path_info", hrequest2.getPathInfo());
                    wrequest2.setAttribute("jakarta.servlet.forward.query_string", hrequest2.getQueryString());
                    wrequest2.setAttribute(RequestDispatcher.FORWARD_MAPPING, hrequest2.getHttpServletMapping());
                }
                wrequest2.setContextPath(this.context.getEncodedPath());
                wrequest2.setRequestURI(this.requestURI);
                wrequest2.setServletPath(this.servletPath);
                wrequest2.setPathInfo(this.pathInfo);
                if (this.queryString != null) {
                    wrequest2.setQueryString(this.queryString);
                    wrequest2.setQueryParams(this.queryString);
                }
                wrequest2.setMapping(this.mapping);
                processRequest(request, response, state);
            }
            if (request.isAsyncStarted()) {
                return;
            }
            if (this.wrapper.getLogger().isDebugEnabled()) {
                this.wrapper.getLogger().debug(" Disabling the response for further output");
            }
            if (response instanceof ResponseFacade) {
                ((ResponseFacade) response).finish();
                return;
            }
            if (this.wrapper.getLogger().isDebugEnabled()) {
                this.wrapper.getLogger().debug(" The Response is vehiculed using a wrapper: " + response.getClass().getName());
            }
            try {
                PrintWriter writer = response.getWriter();
                writer.close();
            } catch (IOException e) {
            } catch (IllegalStateException e2) {
                try {
                    ServletOutputStream stream = response.getOutputStream();
                    stream.close();
                } catch (IOException | IllegalStateException e3) {
                }
            }
        } catch (IllegalStateException e4) {
            throw e4;
        }
    }

    private void processRequest(ServletRequest request, ServletResponse response, State state) throws IOException, ServletException {
        DispatcherType disInt = (DispatcherType) request.getAttribute(Globals.DISPATCHER_TYPE_ATTR);
        if (disInt != null) {
            boolean doInvoke = true;
            if (this.context.getFireRequestListenersOnForwards() && !this.context.fireRequestInitEvent(request)) {
                doInvoke = false;
            }
            if (doInvoke) {
                if (disInt != DispatcherType.ERROR) {
                    state.outerRequest.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR, getCombinedPath());
                    state.outerRequest.setAttribute(Globals.DISPATCHER_TYPE_ATTR, DispatcherType.FORWARD);
                    invoke(state.outerRequest, response, state);
                } else {
                    invoke(state.outerRequest, response, state);
                }
                if (this.context.getFireRequestListenersOnForwards()) {
                    this.context.fireRequestDestroyEvent(request);
                }
            }
        }
    }

    private String getCombinedPath() {
        if (this.servletPath == null) {
            return null;
        }
        if (this.pathInfo == null) {
            return this.servletPath;
        }
        return this.servletPath + this.pathInfo;
    }

    @Override // jakarta.servlet.RequestDispatcher
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                PrivilegedInclude dp = new PrivilegedInclude(request, response);
                AccessController.doPrivileged(dp);
                return;
            } catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw ((ServletException) e);
                }
                throw ((IOException) e);
            }
        }
        doInclude(request, response);
    }

    private void doInclude(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        State state = new State(request, response, true);
        if (this.context.getDispatcherWrapsSameObject()) {
            checkSameObjects(request, response);
        }
        wrapResponse(state);
        if (this.name != null) {
            ApplicationHttpRequest wrequest = (ApplicationHttpRequest) wrapRequest(state);
            wrequest.setAttribute(Globals.NAMED_DISPATCHER_ATTR, this.name);
            if (this.servletPath != null) {
                wrequest.setServletPath(this.servletPath);
            }
            wrequest.setAttribute(Globals.DISPATCHER_TYPE_ATTR, DispatcherType.INCLUDE);
            wrequest.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR, getCombinedPath());
            invoke(state.outerRequest, state.outerResponse, state);
            return;
        }
        ApplicationHttpRequest wrequest2 = (ApplicationHttpRequest) wrapRequest(state);
        String contextPath = this.context.getPath();
        if (this.requestURI != null) {
            wrequest2.setAttribute("jakarta.servlet.include.request_uri", this.requestURI);
        }
        if (contextPath != null) {
            wrequest2.setAttribute("jakarta.servlet.include.context_path", contextPath);
        }
        if (this.servletPath != null) {
            wrequest2.setAttribute("jakarta.servlet.include.servlet_path", this.servletPath);
        }
        if (this.pathInfo != null) {
            wrequest2.setAttribute("jakarta.servlet.include.path_info", this.pathInfo);
        }
        if (this.queryString != null) {
            wrequest2.setAttribute("jakarta.servlet.include.query_string", this.queryString);
            wrequest2.setQueryParams(this.queryString);
        }
        if (this.mapping != null) {
            wrequest2.setAttribute(RequestDispatcher.INCLUDE_MAPPING, this.mapping);
        }
        wrequest2.setAttribute(Globals.DISPATCHER_TYPE_ATTR, DispatcherType.INCLUDE);
        wrequest2.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR, getCombinedPath());
        invoke(state.outerRequest, state.outerResponse, state);
    }

    @Override // org.apache.catalina.AsyncDispatcher
    public void dispatch(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                PrivilegedDispatch dp = new PrivilegedDispatch(request, response);
                AccessController.doPrivileged(dp);
                return;
            } catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw ((ServletException) e);
                }
                throw ((IOException) e);
            }
        }
        doDispatch(request, response);
    }

    private void doDispatch(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        State state = new State(request, response, false);
        wrapResponse(state);
        ApplicationHttpRequest wrequest = (ApplicationHttpRequest) wrapRequest(state);
        HttpServletRequest hrequest = state.hrequest;
        wrequest.setAttribute(Globals.DISPATCHER_TYPE_ATTR, DispatcherType.ASYNC);
        wrequest.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR, getCombinedPath());
        wrequest.setAttribute(AsyncContext.ASYNC_MAPPING, hrequest.getHttpServletMapping());
        wrequest.setContextPath(this.context.getEncodedPath());
        wrequest.setRequestURI(this.requestURI);
        wrequest.setServletPath(this.servletPath);
        wrequest.setPathInfo(this.pathInfo);
        if (this.queryString != null) {
            wrequest.setQueryString(this.queryString);
            wrequest.setQueryParams(this.queryString);
        }
        wrequest.setMapping(this.mapping);
        invoke(state.outerRequest, state.outerResponse, state);
    }

    private void invoke(ServletRequest request, ServletResponse response, State state) throws IOException, ServletException {
        ClassLoader oldCCL = this.context.bind(false, null);
        HttpServletResponse hresponse = state.hresponse;
        Servlet servlet = null;
        IOException ioException = null;
        ServletException servletException = null;
        RuntimeException runtimeException = null;
        boolean unavailable = false;
        if (this.wrapper.isUnavailable()) {
            this.wrapper.getLogger().warn(sm.getString("applicationDispatcher.isUnavailable", this.wrapper.getName()));
            long available = this.wrapper.getAvailable();
            if (available > 0 && available < Long.MAX_VALUE) {
                hresponse.setDateHeader(HttpHeaders.RETRY_AFTER, available);
            }
            hresponse.sendError(503, sm.getString("applicationDispatcher.isUnavailable", this.wrapper.getName()));
            unavailable = true;
        }
        if (!unavailable) {
            try {
                servlet = this.wrapper.allocate();
            } catch (ServletException e) {
                this.wrapper.getLogger().error(sm.getString("applicationDispatcher.allocateException", this.wrapper.getName()), StandardWrapper.getRootCause(e));
                servletException = e;
            } catch (Throwable e2) {
                ExceptionUtils.handleThrowable(e2);
                this.wrapper.getLogger().error(sm.getString("applicationDispatcher.allocateException", this.wrapper.getName()), e2);
                servletException = new ServletException(sm.getString("applicationDispatcher.allocateException", this.wrapper.getName()), e2);
                servlet = null;
            }
        }
        ApplicationFilterChain filterChain = ApplicationFilterFactory.createFilterChain(request, this.wrapper, servlet);
        if (servlet != null && filterChain != null) {
            try {
                filterChain.doFilter(request, response);
            } catch (UnavailableException e3) {
                this.wrapper.getLogger().error(sm.getString("applicationDispatcher.serviceException", this.wrapper.getName()), e3);
                servletException = e3;
                this.wrapper.unavailable(e3);
            } catch (ServletException e4) {
                Throwable rootCause = StandardWrapper.getRootCause(e4);
                if (!(rootCause instanceof BadRequestException)) {
                    this.wrapper.getLogger().error(sm.getString("applicationDispatcher.serviceException", this.wrapper.getName()), rootCause);
                }
                servletException = e4;
            } catch (RuntimeException e5) {
                this.wrapper.getLogger().error(sm.getString("applicationDispatcher.serviceException", this.wrapper.getName()), e5);
                runtimeException = e5;
            } catch (BadRequestException e6) {
                ioException = e6;
            } catch (IOException e7) {
                this.wrapper.getLogger().error(sm.getString("applicationDispatcher.serviceException", this.wrapper.getName()), e7);
                ioException = e7;
            }
        }
        if (filterChain != null) {
            filterChain.release();
        }
        if (servlet != null) {
            try {
                this.wrapper.deallocate(servlet);
            } catch (ServletException e8) {
                this.wrapper.getLogger().error(sm.getString("applicationDispatcher.deallocateException", this.wrapper.getName()), e8);
                servletException = e8;
            } catch (Throwable e9) {
                ExceptionUtils.handleThrowable(e9);
                this.wrapper.getLogger().error(sm.getString("applicationDispatcher.deallocateException", this.wrapper.getName()), e9);
                servletException = new ServletException(sm.getString("applicationDispatcher.deallocateException", this.wrapper.getName()), e9);
            }
        }
        this.context.unbind(false, oldCCL);
        unwrapRequest(state);
        unwrapResponse(state);
        recycleRequestWrapper(state);
        if (ioException != null) {
            throw ioException;
        }
        if (servletException != null) {
            throw servletException;
        }
        if (runtimeException != null) {
            throw runtimeException;
        }
    }

    private void unwrapRequest(State state) {
        if (state.wrapRequest == null) {
            return;
        }
        if (state.outerRequest.isAsyncStarted() && !state.outerRequest.getAsyncContext().hasOriginalRequestAndResponse()) {
            return;
        }
        ServletRequest previous = null;
        ServletRequest servletRequest = state.outerRequest;
        while (true) {
            ServletRequest current = servletRequest;
            if (current != null && !(current instanceof Request) && !(current instanceof RequestFacade)) {
                if (current == state.wrapRequest) {
                    ServletRequest next = ((ServletRequestWrapper) current).getRequest();
                    if (previous == null) {
                        state.outerRequest = next;
                        return;
                    } else {
                        ((ServletRequestWrapper) previous).setRequest(next);
                        return;
                    }
                }
                previous = current;
                servletRequest = ((ServletRequestWrapper) current).getRequest();
            } else {
                return;
            }
        }
    }

    private void unwrapResponse(State state) {
        if (state.wrapResponse == null) {
            return;
        }
        if (state.outerRequest.isAsyncStarted() && !state.outerRequest.getAsyncContext().hasOriginalRequestAndResponse()) {
            return;
        }
        ServletResponse previous = null;
        ServletResponse servletResponse = state.outerResponse;
        while (true) {
            ServletResponse current = servletResponse;
            if (current != null && !(current instanceof Response) && !(current instanceof ResponseFacade)) {
                if (current == state.wrapResponse) {
                    ServletResponse next = ((ServletResponseWrapper) current).getResponse();
                    if (previous == null) {
                        state.outerResponse = next;
                        return;
                    } else {
                        ((ServletResponseWrapper) previous).setResponse(next);
                        return;
                    }
                }
                previous = current;
                servletResponse = ((ServletResponseWrapper) current).getResponse();
            } else {
                return;
            }
        }
    }

    private ServletRequest wrapRequest(State state) {
        ServletRequest current;
        ServletRequest wrapper;
        ServletRequest previous = null;
        ServletRequest servletRequest = state.outerRequest;
        while (true) {
            current = servletRequest;
            if (current != null) {
                if (state.hrequest == null && (current instanceof HttpServletRequest)) {
                    state.hrequest = (HttpServletRequest) current;
                }
                if (!(current instanceof ServletRequestWrapper) || (current instanceof ApplicationHttpRequest) || (current instanceof ApplicationRequest)) {
                    break;
                }
                previous = current;
                servletRequest = ((ServletRequestWrapper) current).getRequest();
            } else {
                break;
            }
        }
        if ((current instanceof ApplicationHttpRequest) || (current instanceof Request) || (current instanceof HttpServletRequest)) {
            HttpServletRequest hcurrent = (HttpServletRequest) current;
            boolean crossContext = false;
            if ((state.outerRequest instanceof ApplicationHttpRequest) || (state.outerRequest instanceof Request) || (state.outerRequest instanceof HttpServletRequest)) {
                HttpServletRequest houterRequest = (HttpServletRequest) state.outerRequest;
                Object contextPath = houterRequest.getAttribute("jakarta.servlet.include.context_path");
                if (contextPath == null) {
                    contextPath = houterRequest.getContextPath();
                }
                crossContext = !this.context.getPath().equals(contextPath);
            }
            wrapper = new ApplicationHttpRequest(hcurrent, this.context, crossContext);
        } else {
            wrapper = new ApplicationRequest(current);
        }
        if (previous == null) {
            state.outerRequest = wrapper;
        } else {
            ((ServletRequestWrapper) previous).setRequest(wrapper);
        }
        state.wrapRequest = wrapper;
        return wrapper;
    }

    private ServletResponse wrapResponse(State state) {
        ServletResponse current;
        ServletResponse wrapper;
        ServletResponse previous = null;
        ServletResponse servletResponse = state.outerResponse;
        while (true) {
            current = servletResponse;
            if (current != null) {
                if (state.hresponse == null && (current instanceof HttpServletResponse)) {
                    state.hresponse = (HttpServletResponse) current;
                    if (!state.including) {
                        return null;
                    }
                }
                if (!(current instanceof ServletResponseWrapper) || (current instanceof ApplicationHttpResponse) || (current instanceof ApplicationResponse)) {
                    break;
                }
                previous = current;
                servletResponse = ((ServletResponseWrapper) current).getResponse();
            } else {
                break;
            }
        }
        if ((current instanceof ApplicationHttpResponse) || (current instanceof Response) || (current instanceof HttpServletResponse)) {
            wrapper = new ApplicationHttpResponse((HttpServletResponse) current, state.including);
        } else {
            wrapper = new ApplicationResponse(current, state.including);
        }
        if (previous == null) {
            state.outerResponse = wrapper;
        } else {
            ((ServletResponseWrapper) previous).setResponse(wrapper);
        }
        state.wrapResponse = wrapper;
        return wrapper;
    }

    private void checkSameObjects(ServletRequest appRequest, ServletResponse appResponse) throws ServletException {
        ServletRequest originalRequest = ApplicationFilterChain.getLastServicedRequest();
        ServletResponse originalResponse = ApplicationFilterChain.getLastServicedResponse();
        if (originalRequest == null || originalResponse == null) {
            return;
        }
        boolean same = false;
        ServletRequest dispatchedRequest = appRequest;
        while ((originalRequest instanceof ServletRequestWrapper) && ((ServletRequestWrapper) originalRequest).getRequest() != null) {
            originalRequest = ((ServletRequestWrapper) originalRequest).getRequest();
        }
        while (!same) {
            if (originalRequest.equals(dispatchedRequest)) {
                same = true;
            }
            if (same || !(dispatchedRequest instanceof ServletRequestWrapper)) {
                break;
            } else {
                dispatchedRequest = ((ServletRequestWrapper) dispatchedRequest).getRequest();
            }
        }
        if (!same) {
            throw new ServletException(sm.getString("applicationDispatcher.specViolation.request"));
        }
        boolean same2 = false;
        ServletResponse dispatchedResponse = appResponse;
        while ((originalResponse instanceof ServletResponseWrapper) && ((ServletResponseWrapper) originalResponse).getResponse() != null) {
            originalResponse = ((ServletResponseWrapper) originalResponse).getResponse();
        }
        while (!same2) {
            if (originalResponse.equals(dispatchedResponse)) {
                same2 = true;
            }
            if (same2 || !(dispatchedResponse instanceof ServletResponseWrapper)) {
                break;
            } else {
                dispatchedResponse = ((ServletResponseWrapper) dispatchedResponse).getResponse();
            }
        }
        if (!same2) {
            throw new ServletException(sm.getString("applicationDispatcher.specViolation.response"));
        }
    }

    private void recycleRequestWrapper(State state) {
        if (state.wrapRequest instanceof ApplicationHttpRequest) {
            ((ApplicationHttpRequest) state.wrapRequest).recycle();
        }
    }
}
