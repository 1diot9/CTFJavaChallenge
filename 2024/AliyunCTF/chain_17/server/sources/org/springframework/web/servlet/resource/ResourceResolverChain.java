package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceResolverChain.class */
public interface ResourceResolverChain {
    @Nullable
    Resource resolveResource(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations);

    @Nullable
    String resolveUrlPath(String resourcePath, List<? extends Resource> locations);
}
