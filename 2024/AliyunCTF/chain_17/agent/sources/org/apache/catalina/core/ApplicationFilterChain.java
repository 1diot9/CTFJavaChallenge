package org.apache.catalina.core;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.util.Set;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationFilterChain.class */
public final class ApplicationFilterChain implements FilterChain {
    public static final int INCREMENT = 10;
    private ApplicationFilterConfig[] filters = new ApplicationFilterConfig[0];
    private int pos = 0;
    private int n = 0;
    private Servlet servlet = null;
    private boolean servletSupportsAsync = false;
    private boolean dispatcherWrapsSameObject = false;
    private static final ThreadLocal<ServletRequest> lastServicedRequest = new ThreadLocal<>();
    private static final ThreadLocal<ServletResponse> lastServicedResponse = new ThreadLocal<>();
    private static final StringManager sm = StringManager.getManager((Class<?>) ApplicationFilterChain.class);
    private static final Class<?>[] classType = {ServletRequest.class, ServletResponse.class, FilterChain.class};
    private static final Class<?>[] classTypeUsedInService = {ServletRequest.class, ServletResponse.class};

    @Override // jakarta.servlet.FilterChain
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                AccessController.doPrivileged(() -> {
                    internalDoFilter(request, response);
                    return null;
                });
                return;
            } catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw ((ServletException) e);
                }
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                }
                throw new ServletException(e.getMessage(), e);
            }
        }
        internalDoFilter(request, response);
    }

    private void internalDoFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        try {
            if (this.pos < this.n) {
                ApplicationFilterConfig[] applicationFilterConfigArr = this.filters;
                int i = this.pos;
                this.pos = i + 1;
                ApplicationFilterConfig filterConfig = applicationFilterConfigArr[i];
                try {
                    Filter filter = filterConfig.getFilter();
                    if (request.isAsyncSupported() && "false".equalsIgnoreCase(filterConfig.getFilterDef().getAsyncSupported())) {
                        request.setAttribute(Globals.ASYNC_SUPPORTED_ATTR, Boolean.FALSE);
                    }
                    if (Globals.IS_SECURITY_ENABLED) {
                        Principal principal = ((HttpServletRequest) request).getUserPrincipal();
                        Object[] args = {request, response, this};
                        SecurityUtil.doAsPrivilege("doFilter", filter, classType, args, principal);
                    } else {
                        filter.doFilter(request, response, this);
                    }
                    return;
                } catch (ServletException | IOException | RuntimeException e) {
                    throw e;
                } catch (Throwable e2) {
                    Throwable e3 = ExceptionUtils.unwrapInvocationTargetException(e2);
                    ExceptionUtils.handleThrowable(e3);
                    throw new ServletException(sm.getString("filterChain.filter"), e3);
                }
            }
            try {
                if (this.dispatcherWrapsSameObject) {
                    lastServicedRequest.set(request);
                    lastServicedResponse.set(response);
                }
                if (request.isAsyncSupported() && !this.servletSupportsAsync) {
                    request.setAttribute(Globals.ASYNC_SUPPORTED_ATTR, Boolean.FALSE);
                }
                if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse) && Globals.IS_SECURITY_ENABLED) {
                    Principal principal2 = ((HttpServletRequest) request).getUserPrincipal();
                    Object[] args2 = {request, response};
                    SecurityUtil.doAsPrivilege("service", this.servlet, classTypeUsedInService, args2, principal2);
                } else {
                    this.servlet.service(request, response);
                }
            } catch (ServletException | IOException | RuntimeException e4) {
                throw e4;
            } catch (Throwable e5) {
                Throwable e6 = ExceptionUtils.unwrapInvocationTargetException(e5);
                ExceptionUtils.handleThrowable(e6);
                throw new ServletException(sm.getString("filterChain.servlet"), e6);
            }
        } finally {
            if (this.dispatcherWrapsSameObject) {
                lastServicedRequest.set(null);
                lastServicedResponse.set(null);
            }
        }
    }

    public static ServletRequest getLastServicedRequest() {
        return lastServicedRequest.get();
    }

    public static ServletResponse getLastServicedResponse() {
        return lastServicedResponse.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addFilter(ApplicationFilterConfig filterConfig) {
        for (ApplicationFilterConfig filter : this.filters) {
            if (filter == filterConfig) {
                return;
            }
        }
        if (this.n == this.filters.length) {
            ApplicationFilterConfig[] newFilters = new ApplicationFilterConfig[this.n + 10];
            System.arraycopy(this.filters, 0, newFilters, 0, this.n);
            this.filters = newFilters;
        }
        ApplicationFilterConfig[] applicationFilterConfigArr = this.filters;
        int i = this.n;
        this.n = i + 1;
        applicationFilterConfigArr[i] = filterConfig;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void release() {
        for (int i = 0; i < this.n; i++) {
            this.filters[i] = null;
        }
        this.n = 0;
        this.pos = 0;
        this.servlet = null;
        this.servletSupportsAsync = false;
        this.dispatcherWrapsSameObject = false;
    }

    void reuse() {
        this.pos = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setServletSupportsAsync(boolean servletSupportsAsync) {
        this.servletSupportsAsync = servletSupportsAsync;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDispatcherWrapsSameObject(boolean dispatcherWrapsSameObject) {
        this.dispatcherWrapsSameObject = dispatcherWrapsSameObject;
    }

    public void findNonAsyncFilters(Set<String> result) {
        for (int i = 0; i < this.n; i++) {
            ApplicationFilterConfig filter = this.filters[i];
            if ("false".equalsIgnoreCase(filter.getFilterDef().getAsyncSupported())) {
                result.add(filter.getFilterClass());
            }
        }
    }
}
