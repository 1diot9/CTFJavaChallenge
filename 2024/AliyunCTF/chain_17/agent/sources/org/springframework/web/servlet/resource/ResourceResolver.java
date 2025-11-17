package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceResolver.class */
public interface ResourceResolver {
    @Nullable
    Resource resolveResource(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain);

    @Nullable
    String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain);
}
