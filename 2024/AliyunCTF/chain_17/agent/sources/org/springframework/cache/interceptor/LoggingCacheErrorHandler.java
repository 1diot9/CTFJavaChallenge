package org.springframework.cache.interceptor;

import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/LoggingCacheErrorHandler.class */
public class LoggingCacheErrorHandler implements CacheErrorHandler {
    private final Log logger;
    private final boolean logStackTraces;

    public LoggingCacheErrorHandler() {
        this(false);
    }

    public LoggingCacheErrorHandler(boolean logStackTraces) {
        this(LogFactory.getLog((Class<?>) LoggingCacheErrorHandler.class), logStackTraces);
    }

    public LoggingCacheErrorHandler(Log logger, boolean logStackTraces) {
        Assert.notNull(logger, "'logger' must not be null");
        this.logger = logger;
        this.logStackTraces = logStackTraces;
    }

    public LoggingCacheErrorHandler(String loggerName, boolean logStackTraces) {
        Assert.notNull(loggerName, "'loggerName' must not be null");
        this.logger = LogFactory.getLog(loggerName);
        this.logStackTraces = logStackTraces;
    }

    @Override // org.springframework.cache.interceptor.CacheErrorHandler
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logCacheError(() -> {
            return String.format("Cache '%s' failed to get entry with key '%s'", cache.getName(), key);
        }, exception);
    }

    @Override // org.springframework.cache.interceptor.CacheErrorHandler
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
        logCacheError(() -> {
            return String.format("Cache '%s' failed to put entry with key '%s'", cache.getName(), key);
        }, exception);
    }

    @Override // org.springframework.cache.interceptor.CacheErrorHandler
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        logCacheError(() -> {
            return String.format("Cache '%s' failed to evict entry with key '%s'", cache.getName(), key);
        }, exception);
    }

    @Override // org.springframework.cache.interceptor.CacheErrorHandler
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        logCacheError(() -> {
            return String.format("Cache '%s' failed to clear entries", cache.getName());
        }, exception);
    }

    protected final Log getLogger() {
        return this.logger;
    }

    protected final boolean isLogStackTraces() {
        return this.logStackTraces;
    }

    protected void logCacheError(Supplier<String> messageSupplier, RuntimeException exception) {
        if (getLogger().isWarnEnabled()) {
            if (isLogStackTraces()) {
                getLogger().warn(messageSupplier.get(), exception);
            } else {
                getLogger().warn(messageSupplier.get());
            }
        }
    }
}
