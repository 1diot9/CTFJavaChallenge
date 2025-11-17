package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.core.io.Resource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceTransformerChain.class */
public interface ResourceTransformerChain {
    ResourceResolverChain getResolverChain();

    Resource transform(HttpServletRequest request, Resource resource) throws IOException;
}
