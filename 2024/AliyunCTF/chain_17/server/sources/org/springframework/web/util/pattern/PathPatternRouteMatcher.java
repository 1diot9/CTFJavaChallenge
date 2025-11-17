package org.springframework.web.util.pattern;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.RouteMatcher;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PathPatternRouteMatcher.class */
public class PathPatternRouteMatcher implements RouteMatcher {
    private final PathPatternParser parser;
    private final Map<String, PathPattern> pathPatternCache;

    public PathPatternRouteMatcher() {
        this.pathPatternCache = new ConcurrentHashMap();
        this.parser = new PathPatternParser();
        this.parser.setPathOptions(PathContainer.Options.MESSAGE_ROUTE);
    }

    public PathPatternRouteMatcher(PathPatternParser parser) {
        this.pathPatternCache = new ConcurrentHashMap();
        Assert.notNull(parser, "PathPatternParser must not be null");
        this.parser = parser;
    }

    @Override // org.springframework.util.RouteMatcher
    public RouteMatcher.Route parseRoute(String routeValue) {
        return new PathContainerRoute(PathContainer.parsePath(routeValue, this.parser.getPathOptions()));
    }

    @Override // org.springframework.util.RouteMatcher
    public boolean isPattern(String route) {
        return getPathPattern(route).hasPatternSyntax();
    }

    @Override // org.springframework.util.RouteMatcher
    public String combine(String pattern1, String pattern2) {
        return getPathPattern(pattern1).combine(getPathPattern(pattern2)).getPatternString();
    }

    @Override // org.springframework.util.RouteMatcher
    public boolean match(String pattern, RouteMatcher.Route route) {
        return getPathPattern(pattern).matches(getPathContainer(route));
    }

    @Override // org.springframework.util.RouteMatcher
    @Nullable
    public Map<String, String> matchAndExtract(String pattern, RouteMatcher.Route route) {
        PathPattern.PathMatchInfo info = getPathPattern(pattern).matchAndExtract(getPathContainer(route));
        if (info != null) {
            return info.getUriVariables();
        }
        return null;
    }

    @Override // org.springframework.util.RouteMatcher
    public Comparator<String> getPatternComparator(RouteMatcher.Route route) {
        return Comparator.comparing(this::getPathPattern);
    }

    private PathPattern getPathPattern(String pattern) {
        Map<String, PathPattern> map = this.pathPatternCache;
        PathPatternParser pathPatternParser = this.parser;
        Objects.requireNonNull(pathPatternParser);
        return map.computeIfAbsent(pattern, pathPatternParser::parse);
    }

    private PathContainer getPathContainer(RouteMatcher.Route route) {
        Assert.isInstanceOf(PathContainerRoute.class, route);
        return ((PathContainerRoute) route).pathContainer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PathPatternRouteMatcher$PathContainerRoute.class */
    public static class PathContainerRoute implements RouteMatcher.Route {
        private final PathContainer pathContainer;

        PathContainerRoute(PathContainer pathContainer) {
            this.pathContainer = pathContainer;
        }

        @Override // org.springframework.util.RouteMatcher.Route
        public String value() {
            return this.pathContainer.value();
        }

        public String toString() {
            return value();
        }
    }
}
