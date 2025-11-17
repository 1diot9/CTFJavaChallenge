package org.springframework.web.servlet.mvc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.http.CacheControl;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/WebContentInterceptor.class */
public class WebContentInterceptor extends WebContentGenerator implements HandlerInterceptor {
    private static PathMatcher defaultPathMatcher = new AntPathMatcher();
    private final PathPatternParser patternParser;
    private PathMatcher pathMatcher;
    private final Map<PathPattern, Integer> cacheMappings;
    private final Map<PathPattern, CacheControl> cacheControlMappings;

    public WebContentInterceptor() {
        this(PathPatternParser.defaultInstance);
    }

    public WebContentInterceptor(PathPatternParser parser) {
        super(false);
        this.pathMatcher = defaultPathMatcher;
        this.cacheMappings = new HashMap();
        this.cacheControlMappings = new HashMap();
        this.patternParser = parser;
    }

    @Deprecated
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
    }

    @Deprecated
    public void setUrlDecode(boolean urlDecode) {
    }

    @Deprecated
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }

    public void setCacheMappings(Properties cacheMappings) {
        this.cacheMappings.clear();
        Enumeration<?> propNames = cacheMappings.propertyNames();
        while (propNames.hasMoreElements()) {
            String path = (String) propNames.nextElement();
            int cacheSeconds = Integer.parseInt(cacheMappings.getProperty(path));
            this.cacheMappings.put(this.patternParser.parse(path), Integer.valueOf(cacheSeconds));
        }
    }

    public void addCacheMapping(CacheControl cacheControl, String... paths) {
        for (String path : paths) {
            this.cacheControlMappings.put(this.patternParser.parse(path), cacheControl);
        }
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        Integer lookupCacheSeconds;
        CacheControl lookupCacheControl;
        checkRequest(request);
        Object path = ServletRequestPathUtils.getCachedPath(request);
        if (this.pathMatcher != defaultPathMatcher) {
            path = path.toString();
        }
        if (!ObjectUtils.isEmpty(this.cacheControlMappings)) {
            if (path instanceof PathContainer) {
                PathContainer pathContainer = (PathContainer) path;
                lookupCacheControl = lookupCacheControl(pathContainer);
            } else {
                lookupCacheControl = lookupCacheControl((String) path);
            }
            CacheControl control = lookupCacheControl;
            if (control != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Applying " + control);
                }
                applyCacheControl(response, control);
                return true;
            }
        }
        if (!ObjectUtils.isEmpty(this.cacheMappings)) {
            if (path instanceof PathContainer) {
                PathContainer pathContainer2 = (PathContainer) path;
                lookupCacheSeconds = lookupCacheSeconds(pathContainer2);
            } else {
                lookupCacheSeconds = lookupCacheSeconds((String) path);
            }
            Integer cacheSeconds = lookupCacheSeconds;
            if (cacheSeconds != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Applying cacheSeconds " + cacheSeconds);
                }
                applyCacheSeconds(response, cacheSeconds.intValue());
                return true;
            }
        }
        prepareResponse(response);
        return true;
    }

    @Nullable
    protected CacheControl lookupCacheControl(PathContainer path) {
        for (Map.Entry<PathPattern, CacheControl> entry : this.cacheControlMappings.entrySet()) {
            if (entry.getKey().matches(path)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    protected CacheControl lookupCacheControl(String lookupPath) {
        for (Map.Entry<PathPattern, CacheControl> entry : this.cacheControlMappings.entrySet()) {
            if (this.pathMatcher.match(entry.getKey().getPatternString(), lookupPath)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    protected Integer lookupCacheSeconds(PathContainer path) {
        for (Map.Entry<PathPattern, Integer> entry : this.cacheMappings.entrySet()) {
            if (entry.getKey().matches(path)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    protected Integer lookupCacheSeconds(String lookupPath) {
        for (Map.Entry<PathPattern, Integer> entry : this.cacheMappings.entrySet()) {
            if (this.pathMatcher.match(entry.getKey().getPatternString(), lookupPath)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
