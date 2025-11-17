package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/VersionStrategy.class */
public interface VersionStrategy extends VersionPathStrategy {
    String getResourceVersion(Resource resource);
}
