package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.core.io.Resource;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceTransformer.class */
public interface ResourceTransformer {
    Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException;
}
