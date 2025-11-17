package org.springframework.web.servlet.config.annotation;

import java.util.Map;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/UrlBasedViewResolverRegistration.class */
public class UrlBasedViewResolverRegistration {
    protected final UrlBasedViewResolver viewResolver;

    public UrlBasedViewResolverRegistration(UrlBasedViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UrlBasedViewResolver getViewResolver() {
        return this.viewResolver;
    }

    public UrlBasedViewResolverRegistration prefix(String prefix) {
        this.viewResolver.setPrefix(prefix);
        return this;
    }

    public UrlBasedViewResolverRegistration suffix(String suffix) {
        this.viewResolver.setSuffix(suffix);
        return this;
    }

    public UrlBasedViewResolverRegistration viewClass(Class<?> viewClass) {
        this.viewResolver.setViewClass(viewClass);
        return this;
    }

    public UrlBasedViewResolverRegistration viewNames(String... viewNames) {
        this.viewResolver.setViewNames(viewNames);
        return this;
    }

    public UrlBasedViewResolverRegistration attributes(Map<String, ?> attributes) {
        this.viewResolver.setAttributesMap(attributes);
        return this;
    }

    public UrlBasedViewResolverRegistration cacheLimit(int cacheLimit) {
        this.viewResolver.setCacheLimit(cacheLimit);
        return this;
    }

    public UrlBasedViewResolverRegistration cache(boolean cache) {
        this.viewResolver.setCache(cache);
        return this;
    }
}
