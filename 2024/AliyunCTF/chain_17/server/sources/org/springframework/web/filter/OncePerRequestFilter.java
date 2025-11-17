package org.springframework.web.filter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.context.request.async.WebAsyncUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/OncePerRequestFilter.class */
public abstract class OncePerRequestFilter extends GenericFilterBean {
    public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

    protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;

    @Override // jakarta.servlet.Filter
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (response instanceof HttpServletResponse) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
                boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;
                if (skipDispatch(httpRequest) || shouldNotFilter(httpRequest)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                if (hasAlreadyFilteredAttribute) {
                    if (DispatcherType.ERROR.equals(request.getDispatcherType())) {
                        doFilterNestedErrorDispatch(httpRequest, httpResponse, filterChain);
                        return;
                    } else {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }
                request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
                try {
                    doFilterInternal(httpRequest, httpResponse, filterChain);
                    request.removeAttribute(alreadyFilteredAttributeName);
                    return;
                } catch (Throwable th) {
                    request.removeAttribute(alreadyFilteredAttributeName);
                    throw th;
                }
            }
        }
        throw new ServletException("OncePerRequestFilter only supports HTTP requests");
    }

    private boolean skipDispatch(HttpServletRequest request) {
        if (isAsyncDispatch(request) && shouldNotFilterAsyncDispatch()) {
            return true;
        }
        if (request.getAttribute("jakarta.servlet.error.request_uri") != null && shouldNotFilterErrorDispatch()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAsyncDispatch(HttpServletRequest request) {
        return DispatcherType.ASYNC.equals(request.getDispatcherType());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAsyncStarted(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted();
    }

    protected String getAlreadyFilteredAttributeName() {
        String name = getFilterName();
        if (name == null) {
            name = getClass().getName();
        }
        return name + ".FILTERED";
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }

    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }
}
