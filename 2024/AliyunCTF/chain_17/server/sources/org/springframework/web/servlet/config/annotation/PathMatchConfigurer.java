package org.springframework.web.servlet.config.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/PathMatchConfigurer.class */
public class PathMatchConfigurer {
    private boolean preferPathMatcher = false;

    @Nullable
    private PathPatternParser patternParser;

    @Nullable
    private Boolean trailingSlashMatch;

    @Nullable
    private Map<String, Predicate<Class<?>>> pathPrefixes;

    @Nullable
    private Boolean suffixPatternMatch;

    @Nullable
    private Boolean registeredSuffixPatternMatch;

    @Nullable
    private UrlPathHelper urlPathHelper;

    @Nullable
    private PathMatcher pathMatcher;

    @Nullable
    private PathPatternParser defaultPatternParser;

    @Nullable
    private UrlPathHelper defaultUrlPathHelper;

    @Nullable
    private PathMatcher defaultPathMatcher;

    public PathMatchConfigurer setPatternParser(@Nullable PathPatternParser patternParser) {
        this.patternParser = patternParser;
        this.preferPathMatcher = patternParser == null;
        return this;
    }

    @Deprecated(since = "6.0")
    public PathMatchConfigurer setUseTrailingSlashMatch(Boolean trailingSlashMatch) {
        this.trailingSlashMatch = trailingSlashMatch;
        return this;
    }

    public PathMatchConfigurer addPathPrefix(String prefix, Predicate<Class<?>> predicate) {
        if (this.pathPrefixes == null) {
            this.pathPrefixes = new LinkedHashMap();
        }
        this.pathPrefixes.put(prefix, predicate);
        return this;
    }

    @Deprecated
    public PathMatchConfigurer setUseSuffixPatternMatch(@Nullable Boolean suffixPatternMatch) {
        this.suffixPatternMatch = suffixPatternMatch;
        this.preferPathMatcher |= suffixPatternMatch != null && suffixPatternMatch.booleanValue();
        return this;
    }

    @Deprecated
    public PathMatchConfigurer setUseRegisteredSuffixPatternMatch(@Nullable Boolean registeredSuffixPatternMatch) {
        this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
        this.preferPathMatcher |= registeredSuffixPatternMatch != null && registeredSuffixPatternMatch.booleanValue();
        return this;
    }

    public PathMatchConfigurer setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
        this.preferPathMatcher = true;
        return this;
    }

    public PathMatchConfigurer setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
        this.preferPathMatcher = true;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean preferPathMatcher() {
        return this.patternParser == null && this.preferPathMatcher;
    }

    @Nullable
    public PathPatternParser getPatternParser() {
        return this.patternParser;
    }

    @Nullable
    @Deprecated
    public Boolean isUseTrailingSlashMatch() {
        return this.trailingSlashMatch;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Map<String, Predicate<Class<?>>> getPathPrefixes() {
        return this.pathPrefixes;
    }

    @Nullable
    @Deprecated
    public Boolean isUseRegisteredSuffixPatternMatch() {
        return this.registeredSuffixPatternMatch;
    }

    @Nullable
    @Deprecated
    public Boolean isUseSuffixPatternMatch() {
        return this.suffixPatternMatch;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Nullable
    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UrlPathHelper getUrlPathHelperOrDefault() {
        if (this.urlPathHelper != null) {
            return this.urlPathHelper;
        }
        if (this.defaultUrlPathHelper == null) {
            this.defaultUrlPathHelper = new UrlPathHelper();
        }
        return this.defaultUrlPathHelper;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PathMatcher getPathMatcherOrDefault() {
        if (this.pathMatcher != null) {
            return this.pathMatcher;
        }
        if (this.defaultPathMatcher == null) {
            this.defaultPathMatcher = new AntPathMatcher();
        }
        return this.defaultPathMatcher;
    }

    public PathPatternParser getPatternParserOrDefault() {
        if (this.patternParser != null) {
            return this.patternParser;
        }
        if (this.defaultPatternParser == null) {
            this.defaultPatternParser = new PathPatternParser();
        }
        return this.defaultPatternParser;
    }
}
