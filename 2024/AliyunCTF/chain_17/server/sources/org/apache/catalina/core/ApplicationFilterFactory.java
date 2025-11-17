package org.apache.catalina.core;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletRequest;
import org.apache.catalina.Globals;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.tomcat.util.descriptor.web.FilterMap;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationFilterFactory.class */
public final class ApplicationFilterFactory {
    private ApplicationFilterFactory() {
    }

    public static ApplicationFilterChain createFilterChain(ServletRequest request, Wrapper wrapper, Servlet servlet) {
        ApplicationFilterChain filterChain;
        ApplicationFilterConfig filterConfig;
        ApplicationFilterConfig filterConfig2;
        if (servlet == null) {
            return null;
        }
        if (request instanceof Request) {
            Request req = (Request) request;
            if (Globals.IS_SECURITY_ENABLED) {
                filterChain = new ApplicationFilterChain();
            } else {
                filterChain = (ApplicationFilterChain) req.getFilterChain();
                if (filterChain == null) {
                    filterChain = new ApplicationFilterChain();
                    req.setFilterChain(filterChain);
                }
            }
        } else {
            filterChain = new ApplicationFilterChain();
        }
        filterChain.setServlet(servlet);
        filterChain.setServletSupportsAsync(wrapper.isAsyncSupported());
        StandardContext context = (StandardContext) wrapper.getParent();
        filterChain.setDispatcherWrapsSameObject(context.getDispatcherWrapsSameObject());
        FilterMap[] filterMaps = context.findFilterMaps();
        if (filterMaps == null || filterMaps.length == 0) {
            return filterChain;
        }
        DispatcherType dispatcher = (DispatcherType) request.getAttribute(Globals.DISPATCHER_TYPE_ATTR);
        String requestPath = null;
        Object attribute = request.getAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR);
        if (attribute != null) {
            requestPath = attribute.toString();
        }
        String servletName = wrapper.getName();
        for (FilterMap filterMap : filterMaps) {
            if (matchDispatcher(filterMap, dispatcher) && matchFiltersURL(filterMap, requestPath) && (filterConfig2 = (ApplicationFilterConfig) context.findFilterConfig(filterMap.getFilterName())) != null) {
                filterChain.addFilter(filterConfig2);
            }
        }
        for (FilterMap filterMap2 : filterMaps) {
            if (matchDispatcher(filterMap2, dispatcher) && matchFiltersServlet(filterMap2, servletName) && (filterConfig = (ApplicationFilterConfig) context.findFilterConfig(filterMap2.getFilterName())) != null) {
                filterChain.addFilter(filterConfig);
            }
        }
        return filterChain;
    }

    private static boolean matchFiltersURL(FilterMap filterMap, String requestPath) {
        if (filterMap.getMatchAllUrlPatterns()) {
            return true;
        }
        if (requestPath == null) {
            return false;
        }
        String[] testPaths = filterMap.getURLPatterns();
        for (String testPath : testPaths) {
            if (matchFiltersURL(testPath, requestPath)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchFiltersURL(String testPath, String requestPath) {
        if (testPath == null) {
            return false;
        }
        if (testPath.equals(requestPath) || testPath.equals("/*")) {
            return true;
        }
        if (testPath.endsWith("/*")) {
            if (testPath.regionMatches(0, requestPath, 0, testPath.length() - 2)) {
                if (requestPath.length() == testPath.length() - 2 || '/' == requestPath.charAt(testPath.length() - 2)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        if (testPath.startsWith("*.")) {
            int slash = requestPath.lastIndexOf(47);
            int period = requestPath.lastIndexOf(46);
            if (slash >= 0 && period > slash && period != requestPath.length() - 1 && requestPath.length() - period == testPath.length() - 1) {
                return testPath.regionMatches(2, requestPath, period + 1, testPath.length() - 2);
            }
            return false;
        }
        return false;
    }

    private static boolean matchFiltersServlet(FilterMap filterMap, String servletName) {
        if (servletName == null) {
            return false;
        }
        if (filterMap.getMatchAllServletNames()) {
            return true;
        }
        String[] servletNames = filterMap.getServletNames();
        for (String name : servletNames) {
            if (servletName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchDispatcher(FilterMap filterMap, DispatcherType type) {
        switch (type) {
            case FORWARD:
                if ((filterMap.getDispatcherMapping() & 2) != 0) {
                    return true;
                }
                return false;
            case INCLUDE:
                if ((filterMap.getDispatcherMapping() & 4) != 0) {
                    return true;
                }
                return false;
            case REQUEST:
                if ((filterMap.getDispatcherMapping() & 8) != 0) {
                    return true;
                }
                return false;
            case ERROR:
                if ((filterMap.getDispatcherMapping() & 1) != 0) {
                    return true;
                }
                return false;
            case ASYNC:
                if ((filterMap.getDispatcherMapping() & 16) != 0) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }
}
