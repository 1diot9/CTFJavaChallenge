package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/CachingResourceResolver.class */
public class CachingResourceResolver extends AbstractResourceResolver {
    public static final String RESOLVED_RESOURCE_CACHE_KEY_PREFIX = "resolvedResource:";
    public static final String RESOLVED_URL_PATH_CACHE_KEY_PREFIX = "resolvedUrlPath:";
    private final Cache cache;
    private final List<String> contentCodings = new ArrayList(EncodedResourceResolver.DEFAULT_CODINGS);

    public CachingResourceResolver(Cache cache) {
        Assert.notNull(cache, "Cache is required");
        this.cache = cache;
    }

    public CachingResourceResolver(CacheManager cacheManager, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalArgumentException("Cache '" + cacheName + "' not found");
        }
        this.cache = cache;
    }

    public Cache getCache() {
        return this.cache;
    }

    public void setContentCodings(List<String> codings) {
        Assert.notEmpty(codings, "At least one content coding expected");
        this.contentCodings.clear();
        this.contentCodings.addAll(codings);
    }

    public List<String> getContentCodings() {
        return Collections.unmodifiableList(this.contentCodings);
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String key = computeKey(request, requestPath);
        Resource resource = (Resource) this.cache.get(key, Resource.class);
        if (resource != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Resource resolved from cache");
            }
            return resource;
        }
        Resource resource2 = chain.resolveResource(request, requestPath, locations);
        if (resource2 != null) {
            this.cache.put(key, resource2);
        }
        return resource2;
    }

    protected String computeKey(@Nullable HttpServletRequest request, String requestPath) {
        if (request != null) {
            String codingKey = getContentCodingKey(request);
            if (StringUtils.hasText(codingKey)) {
                return "resolvedResource:" + requestPath + "+encoding=" + codingKey;
            }
        }
        return "resolvedResource:" + requestPath;
    }

    @Nullable
    private String getContentCodingKey(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.ACCEPT_ENCODING);
        if (!StringUtils.hasText(header)) {
            return null;
        }
        Stream map = Arrays.stream(StringUtils.tokenizeToStringArray(header, ",")).map(token -> {
            int index = token.indexOf(59);
            return (index >= 0 ? token.substring(0, index) : token).trim().toLowerCase();
        });
        List<String> list = this.contentCodings;
        Objects.requireNonNull(list);
        return (String) map.filter((v1) -> {
            return r1.contains(v1);
        }).sorted().collect(Collectors.joining(","));
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String key = "resolvedUrlPath:" + resourceUrlPath;
        String resolvedUrlPath = (String) this.cache.get(key, String.class);
        if (resolvedUrlPath != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Path resolved from cache");
            }
            return resolvedUrlPath;
        }
        String resolvedUrlPath2 = chain.resolveUrlPath(resourceUrlPath, locations);
        if (resolvedUrlPath2 != null) {
            this.cache.put(key, resolvedUrlPath2);
        }
        return resolvedUrlPath2;
    }
}
