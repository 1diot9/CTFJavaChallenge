package org.springframework.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/ServletUriComponentsBuilder.class */
public class ServletUriComponentsBuilder extends UriComponentsBuilder {

    @Nullable
    private String originalPath;

    protected ServletUriComponentsBuilder() {
    }

    protected ServletUriComponentsBuilder(ServletUriComponentsBuilder other) {
        super(other);
        this.originalPath = other.originalPath;
    }

    public static ServletUriComponentsBuilder fromContextPath(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = initFromRequest(request);
        builder.replacePath(request.getContextPath());
        return builder;
    }

    public static ServletUriComponentsBuilder fromServletMapping(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = fromContextPath(request);
        if (StringUtils.hasText(UrlPathHelper.defaultInstance.getPathWithinServletMapping(request))) {
            builder.path(request.getServletPath());
        }
        return builder;
    }

    public static ServletUriComponentsBuilder fromRequestUri(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = initFromRequest(request);
        builder.initPath(request.getRequestURI());
        return builder;
    }

    public static ServletUriComponentsBuilder fromRequest(HttpServletRequest request) {
        ServletUriComponentsBuilder builder = initFromRequest(request);
        builder.initPath(request.getRequestURI());
        builder.query(request.getQueryString());
        return builder;
    }

    private static ServletUriComponentsBuilder initFromRequest(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        ServletUriComponentsBuilder builder = new ServletUriComponentsBuilder();
        builder.scheme(scheme);
        builder.host(host);
        if (("http".equals(scheme) && port != 80) || ("https".equals(scheme) && port != 443)) {
            builder.port(port);
        }
        return builder;
    }

    public static ServletUriComponentsBuilder fromCurrentContextPath() {
        return fromContextPath(getCurrentRequest());
    }

    public static ServletUriComponentsBuilder fromCurrentServletMapping() {
        return fromServletMapping(getCurrentRequest());
    }

    public static ServletUriComponentsBuilder fromCurrentRequestUri() {
        return fromRequestUri(getCurrentRequest());
    }

    public static ServletUriComponentsBuilder fromCurrentRequest() {
        return fromRequest(getCurrentRequest());
    }

    protected static HttpServletRequest getCurrentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        return ((ServletRequestAttributes) attrs).getRequest();
    }

    private void initPath(String path) {
        this.originalPath = path;
        replacePath(path);
    }

    @Nullable
    public String removePathExtension() {
        String extension = null;
        if (this.originalPath != null) {
            extension = UriUtils.extractFileExtension(this.originalPath);
            if (StringUtils.hasLength(extension)) {
                int end = this.originalPath.length() - (extension.length() + 1);
                replacePath(this.originalPath.substring(0, end));
            }
            this.originalPath = null;
        }
        return extension;
    }

    @Override // org.springframework.web.util.UriComponentsBuilder
    public ServletUriComponentsBuilder cloneBuilder() {
        return new ServletUriComponentsBuilder(this);
    }
}
