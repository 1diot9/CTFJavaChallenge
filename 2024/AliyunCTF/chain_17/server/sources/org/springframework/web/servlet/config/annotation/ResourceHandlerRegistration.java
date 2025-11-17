package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.springframework.cache.Cache;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/ResourceHandlerRegistration.class */
public class ResourceHandlerRegistration {
    private final String[] pathPatterns;

    @Nullable
    private Integer cachePeriod;

    @Nullable
    private CacheControl cacheControl;

    @Nullable
    private ResourceChainRegistration resourceChainRegistration;

    @Nullable
    private Function<Resource, String> etagGenerator;
    private final List<String> locationValues = new ArrayList();
    private final List<Resource> locationsResources = new ArrayList();
    private boolean useLastModified = true;
    private boolean optimizeLocations = false;

    public ResourceHandlerRegistration(String... pathPatterns) {
        Assert.notEmpty(pathPatterns, "At least one path pattern is required for resource handling.");
        this.pathPatterns = pathPatterns;
    }

    public ResourceHandlerRegistration addResourceLocations(String... locations) {
        this.locationValues.addAll(Arrays.asList(locations));
        return this;
    }

    public ResourceHandlerRegistration addResourceLocations(Resource... locations) {
        this.locationsResources.addAll(Arrays.asList(locations));
        return this;
    }

    public ResourceHandlerRegistration setCachePeriod(Integer cachePeriod) {
        this.cachePeriod = cachePeriod;
        return this;
    }

    public ResourceHandlerRegistration setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    public ResourceHandlerRegistration setUseLastModified(boolean useLastModified) {
        this.useLastModified = useLastModified;
        return this;
    }

    public ResourceHandlerRegistration setEtagGenerator(@Nullable Function<Resource, String> etagGenerator) {
        this.etagGenerator = etagGenerator;
        return this;
    }

    public ResourceHandlerRegistration setOptimizeLocations(boolean optimizeLocations) {
        this.optimizeLocations = optimizeLocations;
        return this;
    }

    public ResourceChainRegistration resourceChain(boolean cacheResources) {
        this.resourceChainRegistration = new ResourceChainRegistration(cacheResources);
        return this.resourceChainRegistration;
    }

    public ResourceChainRegistration resourceChain(boolean cacheResources, Cache cache) {
        this.resourceChainRegistration = new ResourceChainRegistration(cacheResources, cache);
        return this.resourceChainRegistration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String[] getPathPatterns() {
        return this.pathPatterns;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ResourceHttpRequestHandler getRequestHandler() {
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        if (this.resourceChainRegistration != null) {
            handler.setResourceResolvers(this.resourceChainRegistration.getResourceResolvers());
            handler.setResourceTransformers(this.resourceChainRegistration.getResourceTransformers());
        }
        handler.setLocationValues(this.locationValues);
        handler.setLocations(this.locationsResources);
        if (this.cacheControl != null) {
            handler.setCacheControl(this.cacheControl);
        } else if (this.cachePeriod != null) {
            handler.setCacheSeconds(this.cachePeriod.intValue());
        }
        handler.setUseLastModified(this.useLastModified);
        handler.setEtagGenerator(this.etagGenerator);
        handler.setOptimizeLocations(this.optimizeLocations);
        return handler;
    }
}
