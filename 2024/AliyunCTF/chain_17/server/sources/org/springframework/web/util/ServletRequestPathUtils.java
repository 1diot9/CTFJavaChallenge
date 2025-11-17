package org.springframework.web.util;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.MappingMatch;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ServletRequestPathUtils.class */
public abstract class ServletRequestPathUtils {
    public static final String PATH_ATTRIBUTE = ServletRequestPathUtils.class.getName() + ".PATH";

    public static RequestPath parseAndCache(HttpServletRequest request) {
        RequestPath requestPath = ServletRequestPath.parse(request);
        request.setAttribute(PATH_ATTRIBUTE, requestPath);
        return requestPath;
    }

    public static RequestPath getParsedRequestPath(ServletRequest request) {
        RequestPath path = (RequestPath) request.getAttribute(PATH_ATTRIBUTE);
        Assert.notNull(path, (Supplier<String>) () -> {
            return "Expected parsed RequestPath in request attribute \"" + PATH_ATTRIBUTE + "\".";
        });
        return path;
    }

    public static void setParsedRequestPath(@Nullable RequestPath requestPath, ServletRequest request) {
        if (requestPath != null) {
            request.setAttribute(PATH_ATTRIBUTE, requestPath);
        } else {
            request.removeAttribute(PATH_ATTRIBUTE);
        }
    }

    public static boolean hasParsedRequestPath(ServletRequest request) {
        return request.getAttribute(PATH_ATTRIBUTE) != null;
    }

    public static void clearParsedRequestPath(ServletRequest request) {
        request.removeAttribute(PATH_ATTRIBUTE);
    }

    public static Object getCachedPath(ServletRequest request) {
        String lookupPath = (String) request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
        if (lookupPath != null) {
            return lookupPath;
        }
        RequestPath requestPath = (RequestPath) request.getAttribute(PATH_ATTRIBUTE);
        if (requestPath != null) {
            return requestPath.pathWithinApplication();
        }
        throw new IllegalArgumentException("Neither a pre-parsed RequestPath nor a pre-resolved String lookupPath is available.");
    }

    public static String getCachedPathValue(ServletRequest request) {
        Object path = getCachedPath(request);
        if (path instanceof PathContainer) {
            PathContainer pathContainer = (PathContainer) path;
            String value = pathContainer.value();
            path = UrlPathHelper.defaultInstance.removeSemicolonContent(value);
        }
        return (String) path;
    }

    public static boolean hasCachedPath(ServletRequest request) {
        return (request.getAttribute(PATH_ATTRIBUTE) == null && request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ServletRequestPathUtils$ServletRequestPath.class */
    public static final class ServletRequestPath implements RequestPath {
        private final RequestPath requestPath;
        private final PathContainer contextPath;

        private ServletRequestPath(String rawPath, @Nullable String contextPath, String servletPathPrefix) {
            Assert.notNull(servletPathPrefix, "`servletPathPrefix` is required");
            this.requestPath = RequestPath.parse(rawPath, contextPath + servletPathPrefix);
            this.contextPath = PathContainer.parsePath(StringUtils.hasText(contextPath) ? contextPath : "");
        }

        @Override // org.springframework.http.server.PathContainer
        public String value() {
            return this.requestPath.value();
        }

        @Override // org.springframework.http.server.PathContainer
        public List<PathContainer.Element> elements() {
            return this.requestPath.elements();
        }

        @Override // org.springframework.http.server.RequestPath
        public PathContainer contextPath() {
            return this.contextPath;
        }

        @Override // org.springframework.http.server.RequestPath
        public PathContainer pathWithinApplication() {
            return this.requestPath.pathWithinApplication();
        }

        @Override // org.springframework.http.server.RequestPath
        public RequestPath modifyContextPath(String contextPath) {
            throw new UnsupportedOperationException();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            return this.requestPath.equals(((ServletRequestPath) other).requestPath);
        }

        public int hashCode() {
            return this.requestPath.hashCode();
        }

        public String toString() {
            return this.requestPath.toString();
        }

        public static RequestPath parse(HttpServletRequest request) {
            String requestUri = (String) request.getAttribute("jakarta.servlet.include.request_uri");
            String requestUri2 = requestUri != null ? requestUri : request.getRequestURI();
            String servletPathPrefix = getServletPathPrefix(request);
            if (StringUtils.hasText(servletPathPrefix)) {
                return new ServletRequestPath(requestUri2, request.getContextPath(), servletPathPrefix);
            }
            return RequestPath.parse(requestUri2, request.getContextPath());
        }

        @Nullable
        private static String getServletPathPrefix(HttpServletRequest request) {
            HttpServletMapping mapping = (HttpServletMapping) request.getAttribute(RequestDispatcher.INCLUDE_MAPPING);
            if (ObjectUtils.nullSafeEquals((mapping != null ? mapping : request.getHttpServletMapping()).getMappingMatch(), MappingMatch.PATH)) {
                String servletPath = (String) request.getAttribute("jakarta.servlet.include.servlet_path");
                String servletPath2 = servletPath != null ? servletPath : request.getServletPath();
                return UriUtils.encodePath(servletPath2.endsWith("/") ? servletPath2.substring(0, servletPath2.length() - 1) : servletPath2, StandardCharsets.UTF_8);
            }
            return null;
        }
    }
}
