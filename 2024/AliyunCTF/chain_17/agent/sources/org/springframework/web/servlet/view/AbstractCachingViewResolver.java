package org.springframework.web.servlet.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/AbstractCachingViewResolver.class */
public abstract class AbstractCachingViewResolver extends WebApplicationObjectSupport implements ViewResolver {
    public static final int DEFAULT_CACHE_LIMIT = 1024;
    private static final View UNRESOLVED_VIEW = new View() { // from class: org.springframework.web.servlet.view.AbstractCachingViewResolver.1
        @Override // org.springframework.web.servlet.View
        @Nullable
        public String getContentType() {
            return null;
        }

        @Override // org.springframework.web.servlet.View
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        }
    };
    private static final CacheFilter DEFAULT_CACHE_FILTER = (view, viewName, locale) -> {
        return true;
    };
    private volatile int cacheLimit = 1024;
    private boolean cacheUnresolved = true;
    private CacheFilter cacheFilter = DEFAULT_CACHE_FILTER;
    private final Map<Object, View> viewAccessCache = new ConcurrentHashMap(1024);
    private final Map<Object, View> viewCreationCache = new LinkedHashMap<Object, View>(1024, 0.75f, true) { // from class: org.springframework.web.servlet.view.AbstractCachingViewResolver.2
        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Object, View> eldest) {
            if (size() > AbstractCachingViewResolver.this.getCacheLimit()) {
                AbstractCachingViewResolver.this.viewAccessCache.remove(eldest.getKey());
                return true;
            }
            return false;
        }
    };

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/AbstractCachingViewResolver$CacheFilter.class */
    public interface CacheFilter {
        boolean filter(View view, String viewName, Locale locale);
    }

    @Nullable
    protected abstract View loadView(String viewName, Locale locale) throws Exception;

    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    public int getCacheLimit() {
        return this.cacheLimit;
    }

    public void setCache(boolean cache) {
        this.cacheLimit = cache ? 1024 : 0;
    }

    public boolean isCache() {
        return this.cacheLimit > 0;
    }

    public void setCacheUnresolved(boolean cacheUnresolved) {
        this.cacheUnresolved = cacheUnresolved;
    }

    public boolean isCacheUnresolved() {
        return this.cacheUnresolved;
    }

    public void setCacheFilter(CacheFilter cacheFilter) {
        Assert.notNull(cacheFilter, "CacheFilter must not be null");
        this.cacheFilter = cacheFilter;
    }

    public CacheFilter getCacheFilter() {
        return this.cacheFilter;
    }

    @Override // org.springframework.web.servlet.ViewResolver
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (!isCache()) {
            return createView(viewName, locale);
        }
        Object cacheKey = getCacheKey(viewName, locale);
        View view = this.viewAccessCache.get(cacheKey);
        if (view == null) {
            synchronized (this.viewCreationCache) {
                view = this.viewCreationCache.get(cacheKey);
                if (view == null) {
                    view = createView(viewName, locale);
                    if (view == null && this.cacheUnresolved) {
                        view = UNRESOLVED_VIEW;
                    }
                    if (view != null && this.cacheFilter.filter(view, viewName, locale)) {
                        this.viewAccessCache.put(cacheKey, view);
                        this.viewCreationCache.put(cacheKey, view);
                    }
                }
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace(formatKey(cacheKey) + "served from cache");
        }
        if (view != UNRESOLVED_VIEW) {
            return view;
        }
        return null;
    }

    private static String formatKey(Object cacheKey) {
        return "View with key [" + cacheKey + "] ";
    }

    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + "_" + locale;
    }

    public void removeFromCache(String viewName, Locale locale) {
        Object cachedView;
        if (!isCache()) {
            this.logger.warn("Caching is OFF (removal not necessary)");
            return;
        }
        Object cacheKey = getCacheKey(viewName, locale);
        synchronized (this.viewCreationCache) {
            this.viewAccessCache.remove(cacheKey);
            cachedView = this.viewCreationCache.remove(cacheKey);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(formatKey(cacheKey) + (cachedView != null ? "cleared from cache" : "not found in the cache"));
        }
    }

    public void clearCache() {
        this.logger.debug("Clearing all views from the cache");
        synchronized (this.viewCreationCache) {
            this.viewAccessCache.clear();
            this.viewCreationCache.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public View createView(String viewName, Locale locale) throws Exception {
        return loadView(viewName, locale);
    }
}
