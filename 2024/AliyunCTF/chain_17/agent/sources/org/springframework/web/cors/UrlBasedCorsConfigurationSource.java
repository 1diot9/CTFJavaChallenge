package org.springframework.web.cors;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/UrlBasedCorsConfigurationSource.class */
public class UrlBasedCorsConfigurationSource implements CorsConfigurationSource {
    private static final PathMatcher defaultPathMatcher = new AntPathMatcher();
    private final PathPatternParser patternParser;
    private UrlPathHelper urlPathHelper;
    private PathMatcher pathMatcher;

    @Nullable
    private String lookupPathAttributeName;
    private boolean allowInitLookupPath;
    private final Map<PathPattern, CorsConfiguration> corsConfigurations;

    public UrlBasedCorsConfigurationSource() {
        this(PathPatternParser.defaultInstance);
    }

    public UrlBasedCorsConfigurationSource(PathPatternParser parser) {
        this.urlPathHelper = UrlPathHelper.defaultInstance;
        this.pathMatcher = defaultPathMatcher;
        this.allowInitLookupPath = true;
        this.corsConfigurations = new LinkedHashMap();
        Assert.notNull(parser, "PathPatternParser must not be null");
        this.patternParser = parser;
    }

    @Deprecated
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        initUrlPathHelper();
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
    }

    @Deprecated
    public void setUrlDecode(boolean urlDecode) {
        initUrlPathHelper();
        this.urlPathHelper.setUrlDecode(urlDecode);
    }

    @Deprecated
    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        initUrlPathHelper();
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
    }

    private void initUrlPathHelper() {
        if (this.urlPathHelper == UrlPathHelper.defaultInstance) {
            this.urlPathHelper = new UrlPathHelper();
        }
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    public void setAllowInitLookupPath(boolean allowInitLookupPath) {
        this.allowInitLookupPath = allowInitLookupPath;
    }

    @Deprecated
    public void setLookupPathAttributeName(String name) {
        this.lookupPathAttributeName = name;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations) {
        this.corsConfigurations.clear();
        if (corsConfigurations != null) {
            corsConfigurations.forEach(this::registerCorsConfiguration);
        }
    }

    public void registerCorsConfiguration(String pattern, CorsConfiguration config) {
        this.corsConfigurations.put(this.patternParser.parse(pattern), config);
    }

    public Map<String, CorsConfiguration> getCorsConfigurations() {
        Map<String, CorsConfiguration> result = CollectionUtils.newHashMap(this.corsConfigurations.size());
        this.corsConfigurations.forEach((pattern, config) -> {
            result.put(pattern.getPatternString(), config);
        });
        return Collections.unmodifiableMap(result);
    }

    @Override // org.springframework.web.cors.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        Object path = resolvePath(request);
        boolean isPathContainer = path instanceof PathContainer;
        for (Map.Entry<PathPattern, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
            if (match(path, isPathContainer, entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Object resolvePath(HttpServletRequest request) {
        if (this.allowInitLookupPath && !ServletRequestPathUtils.hasCachedPath(request)) {
            if (this.lookupPathAttributeName != null) {
                return this.urlPathHelper.getLookupPathForRequest(request, this.lookupPathAttributeName);
            }
            return this.urlPathHelper.getLookupPathForRequest(request);
        }
        Object lookupPath = ServletRequestPathUtils.getCachedPath(request);
        if (this.pathMatcher != defaultPathMatcher) {
            lookupPath = lookupPath.toString();
        }
        return lookupPath;
    }

    private boolean match(Object path, boolean isPathContainer, PathPattern pattern) {
        if (isPathContainer) {
            return pattern.matches((PathContainer) path);
        }
        return this.pathMatcher.match(pattern.getPatternString(), (String) path);
    }
}
