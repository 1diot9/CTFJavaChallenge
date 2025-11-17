package org.springframework.web.servlet.handler;

import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/RequestMatchResult.class */
public class RequestMatchResult {

    @Nullable
    private final PathPattern pathPattern;

    @Nullable
    private final PathContainer lookupPathContainer;

    @Nullable
    private final String pattern;

    @Nullable
    private final String lookupPath;

    @Nullable
    private final PathMatcher pathMatcher;

    public RequestMatchResult(PathPattern pathPattern, PathContainer lookupPath) {
        Assert.notNull(pathPattern, "PathPattern is required");
        Assert.notNull(lookupPath, "PathContainer is required");
        this.pattern = null;
        this.lookupPath = null;
        this.pathMatcher = null;
        this.pathPattern = pathPattern;
        this.lookupPathContainer = lookupPath;
    }

    public RequestMatchResult(String pattern, String lookupPath, PathMatcher pathMatcher) {
        Assert.hasText(pattern, "'matchingPattern' is required");
        Assert.hasText(lookupPath, "'lookupPath' is required");
        Assert.notNull(pathMatcher, "PathMatcher is required");
        this.pattern = pattern;
        this.lookupPath = lookupPath;
        this.pathMatcher = pathMatcher;
        this.pathPattern = null;
        this.lookupPathContainer = null;
    }

    public Map<String, String> extractUriTemplateVariables() {
        if (this.pathPattern != null) {
            return this.pathPattern.matchAndExtract(this.lookupPathContainer).getUriVariables();
        }
        return this.pathMatcher.extractUriTemplateVariables(this.pattern, this.lookupPath);
    }
}
