package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/PathPatternMatchableHandlerMapping.class */
class PathPatternMatchableHandlerMapping implements MatchableHandlerMapping {
    private static final int MAX_PATTERNS = 1024;
    private final MatchableHandlerMapping delegate;
    private final PathPatternParser parser;
    private final Map<String, PathPattern> pathPatternCache = new ConcurrentHashMap();

    public PathPatternMatchableHandlerMapping(MatchableHandlerMapping delegate) {
        Assert.notNull(delegate, "HandlerMapping to delegate to is required.");
        Assert.notNull(delegate.getPatternParser(), "Expected HandlerMapping configured to use PatternParser.");
        this.delegate = delegate;
        this.parser = delegate.getPatternParser();
    }

    @Override // org.springframework.web.servlet.handler.MatchableHandlerMapping
    @Nullable
    public RequestMatchResult match(HttpServletRequest request, String pattern) {
        PathPattern pathPattern = this.pathPatternCache.computeIfAbsent(pattern, value -> {
            Assert.state(this.pathPatternCache.size() < 1024, "Max size for pattern cache exceeded.");
            return this.parser.parse(pattern);
        });
        PathContainer path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication();
        if (pathPattern.matches(path)) {
            return new RequestMatchResult(pathPattern, path);
        }
        return null;
    }

    @Override // org.springframework.web.servlet.HandlerMapping
    @Nullable
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        return this.delegate.getHandler(request);
    }
}
