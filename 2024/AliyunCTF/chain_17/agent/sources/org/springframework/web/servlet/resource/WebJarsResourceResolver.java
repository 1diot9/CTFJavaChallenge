package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.webjars.WebJarAssetLocator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/WebJarsResourceResolver.class */
public class WebJarsResourceResolver extends AbstractResourceResolver {
    private static final String WEBJARS_LOCATION = "META-INF/resources/webjars/";
    private static final int WEBJARS_LOCATION_LENGTH = WEBJARS_LOCATION.length();
    private final WebJarAssetLocator webJarAssetLocator;

    public WebJarsResourceResolver() {
        this(new WebJarAssetLocator());
    }

    public WebJarsResourceResolver(WebJarAssetLocator webJarAssetLocator) {
        this.webJarAssetLocator = webJarAssetLocator;
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String webJarResourcePath;
        Resource resolved = chain.resolveResource(request, requestPath, locations);
        if (resolved == null && (webJarResourcePath = findWebJarResourcePath(requestPath)) != null) {
            return chain.resolveResource(request, webJarResourcePath, locations);
        }
        return resolved;
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String webJarResourcePath;
        String path = chain.resolveUrlPath(resourceUrlPath, locations);
        if (path == null && (webJarResourcePath = findWebJarResourcePath(resourceUrlPath)) != null) {
            return chain.resolveUrlPath(webJarResourcePath, locations);
        }
        return path;
    }

    @Nullable
    protected String findWebJarResourcePath(String path) {
        int startOffset = path.startsWith("/") ? 1 : 0;
        int endOffset = path.indexOf(47, 1);
        if (endOffset != -1) {
            String webjar = path.substring(startOffset, endOffset);
            String partialPath = path.substring(endOffset + 1);
            String webJarPath = this.webJarAssetLocator.getFullPathExact(webjar, partialPath);
            if (webJarPath != null) {
                return webJarPath.substring(WEBJARS_LOCATION_LENGTH);
            }
            return null;
        }
        return null;
    }
}
