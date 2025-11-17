package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/AbstractResourceResolver.class */
public abstract class AbstractResourceResolver implements ResourceResolver {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    protected abstract Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain);

    @Nullable
    protected abstract String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain);

    @Override // org.springframework.web.servlet.resource.ResourceResolver
    @Nullable
    public Resource resolveResource(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return resolveResourceInternal(request, requestPath, locations, chain);
    }

    @Override // org.springframework.web.servlet.resource.ResourceResolver
    @Nullable
    public String resolveUrlPath(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return resolveUrlPathInternal(resourceUrlPath, locations, chain);
    }
}
