package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.PatternParseException;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/MappedInterceptor.class */
public final class MappedInterceptor implements HandlerInterceptor {
    private static final PathMatcher defaultPathMatcher = new AntPathMatcher();

    @Nullable
    private final PatternAdapter[] includePatterns;

    @Nullable
    private final PatternAdapter[] excludePatterns;
    private PathMatcher pathMatcher;
    private final HandlerInterceptor interceptor;

    public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns, HandlerInterceptor interceptor, @Nullable PathPatternParser parser) {
        this.pathMatcher = defaultPathMatcher;
        this.includePatterns = PatternAdapter.initPatterns(includePatterns, parser);
        this.excludePatterns = PatternAdapter.initPatterns(excludePatterns, parser);
        this.interceptor = interceptor;
    }

    public MappedInterceptor(@Nullable String[] includePatterns, HandlerInterceptor interceptor) {
        this(includePatterns, (String[]) null, interceptor);
    }

    public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns, HandlerInterceptor interceptor) {
        this(includePatterns, excludePatterns, interceptor, null);
    }

    public MappedInterceptor(@Nullable String[] includePatterns, WebRequestInterceptor interceptor) {
        this(includePatterns, (String[]) null, interceptor);
    }

    public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns, WebRequestInterceptor interceptor) {
        this(includePatterns, excludePatterns, new WebRequestHandlerInterceptorAdapter(interceptor));
    }

    @Nullable
    @Deprecated(since = "6.1", forRemoval = true)
    public String[] getPathPatterns() {
        return getIncludePathPatterns();
    }

    @Nullable
    public String[] getIncludePathPatterns() {
        if (!ObjectUtils.isEmpty((Object[]) this.includePatterns)) {
            return (String[]) Arrays.stream(this.includePatterns).map((v0) -> {
                return v0.getPatternString();
            }).toArray(x$0 -> {
                return new String[x$0];
            });
        }
        return null;
    }

    @Nullable
    public String[] getExcludePathPatterns() {
        if (!ObjectUtils.isEmpty((Object[]) this.excludePatterns)) {
            return (String[]) Arrays.stream(this.excludePatterns).map((v0) -> {
                return v0.getPatternString();
            }).toArray(x$0 -> {
                return new String[x$0];
            });
        }
        return null;
    }

    public HandlerInterceptor getInterceptor() {
        return this.interceptor;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    public boolean matches(HttpServletRequest request) {
        Object path = ServletRequestPathUtils.getCachedPath(request);
        if (this.pathMatcher != defaultPathMatcher) {
            path = path.toString();
        }
        boolean isPathContainer = path instanceof PathContainer;
        if (!ObjectUtils.isEmpty((Object[]) this.excludePatterns)) {
            for (PatternAdapter adapter : this.excludePatterns) {
                if (adapter.match(path, isPathContainer, this.pathMatcher)) {
                    return false;
                }
            }
        }
        if (ObjectUtils.isEmpty((Object[]) this.includePatterns)) {
            return true;
        }
        for (PatternAdapter adapter2 : this.includePatterns) {
            if (adapter2.match(path, isPathContainer, this.pathMatcher)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated(since = "5.3")
    public boolean matches(String lookupPath, PathMatcher pathMatcher) {
        PathMatcher pathMatcher2 = this.pathMatcher != defaultPathMatcher ? this.pathMatcher : pathMatcher;
        if (!ObjectUtils.isEmpty((Object[]) this.excludePatterns)) {
            for (PatternAdapter adapter : this.excludePatterns) {
                if (pathMatcher2.match(adapter.getPatternString(), lookupPath)) {
                    return false;
                }
            }
        }
        if (ObjectUtils.isEmpty((Object[]) this.includePatterns)) {
            return true;
        }
        for (PatternAdapter adapter2 : this.includePatterns) {
            if (pathMatcher2.match(adapter2.getPatternString(), lookupPath)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.interceptor.preHandle(request, response, handler);
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        this.interceptor.postHandle(request, response, handler, modelAndView);
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        this.interceptor.afterCompletion(request, response, handler, ex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/MappedInterceptor$PatternAdapter.class */
    public static class PatternAdapter {
        private final String patternString;

        @Nullable
        private final PathPattern pathPattern;

        public PatternAdapter(String pattern, @Nullable PathPatternParser parser) {
            this.patternString = pattern;
            this.pathPattern = initPathPattern(pattern, parser);
        }

        @Nullable
        private static PathPattern initPathPattern(String pattern, @Nullable PathPatternParser parser) {
            PathPatternParser pathPatternParser;
            if (parser != null) {
                pathPatternParser = parser;
            } else {
                try {
                    pathPatternParser = PathPatternParser.defaultInstance;
                } catch (PatternParseException e) {
                    return null;
                }
            }
            return pathPatternParser.parse(pattern);
        }

        public String getPatternString() {
            return this.patternString;
        }

        public boolean match(Object path, boolean isPathContainer, PathMatcher pathMatcher) {
            if (isPathContainer) {
                PathContainer pathContainer = (PathContainer) path;
                if (this.pathPattern != null) {
                    return this.pathPattern.matches(pathContainer);
                }
                String lookupPath = pathContainer.value();
                path = UrlPathHelper.defaultInstance.removeSemicolonContent(lookupPath);
            }
            return pathMatcher.match(this.patternString, (String) path);
        }

        @Nullable
        public static PatternAdapter[] initPatterns(@Nullable String[] patterns, @Nullable PathPatternParser parser) {
            if (ObjectUtils.isEmpty((Object[]) patterns)) {
                return null;
            }
            return (PatternAdapter[]) Arrays.stream(patterns).map(pattern -> {
                return new PatternAdapter(pattern, parser);
            }).toArray(x$0 -> {
                return new PatternAdapter[x$0];
            });
        }
    }
}
