package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.CachingResourceTransformer;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ResourceChainRegistration.class */
public class ResourceChainRegistration {
    private static final String DEFAULT_CACHE_NAME = "spring-resource-chain-cache";
    private static final boolean isWebJarsAssetLocatorPresent = ClassUtils.isPresent("org.webjars.WebJarAssetLocator", ResourceChainRegistration.class.getClassLoader());
    private final List<ResourceResolver> resolvers;
    private final List<ResourceTransformer> transformers;
    private boolean hasVersionResolver;
    private boolean hasPathResolver;
    private boolean hasCssLinkTransformer;
    private boolean hasWebjarsResolver;

    public ResourceChainRegistration(boolean cacheResources) {
        this(cacheResources, cacheResources ? new ConcurrentMapCache(DEFAULT_CACHE_NAME) : null);
    }

    public ResourceChainRegistration(boolean cacheResources, @Nullable Cache cache) {
        this.resolvers = new ArrayList(4);
        this.transformers = new ArrayList(4);
        Assert.isTrue((cacheResources && cache == null) ? false : true, "'cache' is required when cacheResources=true");
        if (cacheResources) {
            this.resolvers.add(new CachingResourceResolver(cache));
            this.transformers.add(new CachingResourceTransformer(cache));
        }
    }

    public ResourceChainRegistration addResolver(ResourceResolver resolver) {
        Assert.notNull(resolver, "The provided ResourceResolver should not be null");
        this.resolvers.add(resolver);
        if (resolver instanceof VersionResourceResolver) {
            this.hasVersionResolver = true;
        } else if (resolver instanceof PathResourceResolver) {
            this.hasPathResolver = true;
        } else if (resolver instanceof WebJarsResourceResolver) {
            this.hasWebjarsResolver = true;
        }
        return this;
    }

    public ResourceChainRegistration addTransformer(ResourceTransformer transformer) {
        Assert.notNull(transformer, "The provided ResourceTransformer should not be null");
        this.transformers.add(transformer);
        if (transformer instanceof CssLinkResourceTransformer) {
            this.hasCssLinkTransformer = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<ResourceResolver> getResourceResolvers() {
        if (!this.hasPathResolver) {
            List<ResourceResolver> result = new ArrayList<>(this.resolvers);
            if (isWebJarsAssetLocatorPresent && !this.hasWebjarsResolver) {
                result.add(new WebJarsResourceResolver());
            }
            result.add(new PathResourceResolver());
            return result;
        }
        return this.resolvers;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<ResourceTransformer> getResourceTransformers() {
        if (this.hasVersionResolver && !this.hasCssLinkTransformer) {
            List<ResourceTransformer> result = new ArrayList<>(this.transformers);
            boolean hasTransformers = !this.transformers.isEmpty();
            boolean hasCaching = hasTransformers && (this.transformers.get(0) instanceof CachingResourceTransformer);
            result.add(hasCaching ? 1 : 0, new CssLinkResourceTransformer());
            return result;
        }
        return this.transformers;
    }
}
