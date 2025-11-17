package org.springframework.util;

import java.util.Comparator;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.RouteMatcher;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/SimpleRouteMatcher.class */
public class SimpleRouteMatcher implements RouteMatcher {
    private final PathMatcher pathMatcher;

    public SimpleRouteMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher is required");
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    @Override // org.springframework.util.RouteMatcher
    public RouteMatcher.Route parseRoute(String route) {
        return new DefaultRoute(route);
    }

    @Override // org.springframework.util.RouteMatcher
    public boolean isPattern(String route) {
        return this.pathMatcher.isPattern(route);
    }

    @Override // org.springframework.util.RouteMatcher
    public String combine(String pattern1, String pattern2) {
        return this.pathMatcher.combine(pattern1, pattern2);
    }

    @Override // org.springframework.util.RouteMatcher
    public boolean match(String pattern, RouteMatcher.Route route) {
        return this.pathMatcher.match(pattern, route.value());
    }

    @Override // org.springframework.util.RouteMatcher
    @Nullable
    public Map<String, String> matchAndExtract(String pattern, RouteMatcher.Route route) {
        if (!match(pattern, route)) {
            return null;
        }
        return this.pathMatcher.extractUriTemplateVariables(pattern, route.value());
    }

    @Override // org.springframework.util.RouteMatcher
    public Comparator<String> getPatternComparator(RouteMatcher.Route route) {
        return this.pathMatcher.getPatternComparator(route.value());
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/SimpleRouteMatcher$DefaultRoute.class */
    private static class DefaultRoute implements RouteMatcher.Route {
        private final String path;

        DefaultRoute(String path) {
            this.path = path;
        }

        @Override // org.springframework.util.RouteMatcher.Route
        public String value() {
            return this.path;
        }

        public String toString() {
            return value();
        }
    }
}
