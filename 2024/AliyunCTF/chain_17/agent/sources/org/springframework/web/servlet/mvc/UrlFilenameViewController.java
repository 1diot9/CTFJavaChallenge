package org.springframework.web.servlet.mvc;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/UrlFilenameViewController.class */
public class UrlFilenameViewController extends AbstractUrlViewController {
    private String prefix = "";
    private String suffix = "";
    private final Map<String, String> viewNameCache = new ConcurrentHashMap(256);

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    protected String getPrefix() {
        return this.prefix;
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix != null ? suffix : "";
    }

    protected String getSuffix() {
        return this.suffix;
    }

    @Override // org.springframework.web.servlet.mvc.AbstractUrlViewController
    protected String getViewNameForRequest(HttpServletRequest request) {
        String uri = extractOperableUrl(request);
        return getViewNameForUrlPath(uri);
    }

    protected String extractOperableUrl(HttpServletRequest request) {
        String urlPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (!StringUtils.hasText(urlPath)) {
            urlPath = ServletRequestPathUtils.getCachedPathValue(request);
        }
        return urlPath;
    }

    protected String getViewNameForUrlPath(String uri) {
        return this.viewNameCache.computeIfAbsent(uri, u -> {
            return postProcessViewName(extractViewNameFromUrlPath(u));
        });
    }

    protected String extractViewNameFromUrlPath(String uri) {
        int start = uri.charAt(0) == '/' ? 1 : 0;
        int lastIndex = uri.lastIndexOf(46);
        int end = lastIndex < 0 ? uri.length() : lastIndex;
        return uri.substring(start, end);
    }

    protected String postProcessViewName(String viewName) {
        return getPrefix() + viewName + getSuffix();
    }
}
