package org.springframework.boot.autoconfigure.security.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.security.servlet.ApplicationContextRequestMatcher;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/StaticResourceRequest.class */
public final class StaticResourceRequest {
    static final StaticResourceRequest INSTANCE = new StaticResourceRequest();

    private StaticResourceRequest() {
    }

    public StaticResourceRequestMatcher atCommonLocations() {
        return at(EnumSet.allOf(StaticResourceLocation.class));
    }

    public StaticResourceRequestMatcher at(StaticResourceLocation first, StaticResourceLocation... rest) {
        return at(EnumSet.of(first, rest));
    }

    public StaticResourceRequestMatcher at(Set<StaticResourceLocation> locations) {
        Assert.notNull(locations, "Locations must not be null");
        return new StaticResourceRequestMatcher(new LinkedHashSet(locations));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/StaticResourceRequest$StaticResourceRequestMatcher.class */
    public static final class StaticResourceRequestMatcher extends ApplicationContextRequestMatcher<DispatcherServletPath> {
        private final Set<StaticResourceLocation> locations;
        private volatile RequestMatcher delegate;

        private StaticResourceRequestMatcher(Set<StaticResourceLocation> locations) {
            super(DispatcherServletPath.class);
            this.locations = locations;
        }

        public StaticResourceRequestMatcher excluding(StaticResourceLocation first, StaticResourceLocation... rest) {
            return excluding(EnumSet.of(first, rest));
        }

        public StaticResourceRequestMatcher excluding(Set<StaticResourceLocation> locations) {
            Assert.notNull(locations, "Locations must not be null");
            Set<StaticResourceLocation> subset = new LinkedHashSet<>(this.locations);
            subset.removeAll(locations);
            return new StaticResourceRequestMatcher(subset);
        }

        @Override // org.springframework.boot.security.servlet.ApplicationContextRequestMatcher
        protected void initialized(Supplier<DispatcherServletPath> dispatcherServletPath) {
            this.delegate = new OrRequestMatcher(getDelegateMatchers(dispatcherServletPath.get()).toList());
        }

        private Stream<RequestMatcher> getDelegateMatchers(DispatcherServletPath dispatcherServletPath) {
            return getPatterns(dispatcherServletPath).map(AntPathRequestMatcher::new);
        }

        private Stream<String> getPatterns(DispatcherServletPath dispatcherServletPath) {
            Stream<R> flatMap = this.locations.stream().flatMap((v0) -> {
                return v0.getPatterns();
            });
            Objects.requireNonNull(dispatcherServletPath);
            return flatMap.map(dispatcherServletPath::getRelativePath);
        }

        @Override // org.springframework.boot.security.servlet.ApplicationContextRequestMatcher
        protected boolean ignoreApplicationContext(WebApplicationContext applicationContext) {
            return WebServerApplicationContext.hasServerNamespace(applicationContext, "management");
        }

        @Override // org.springframework.boot.security.servlet.ApplicationContextRequestMatcher
        protected boolean matches(HttpServletRequest request, Supplier<DispatcherServletPath> context) {
            return this.delegate.matches(request);
        }
    }
}
