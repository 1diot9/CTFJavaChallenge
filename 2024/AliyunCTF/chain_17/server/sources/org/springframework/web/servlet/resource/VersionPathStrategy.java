package org.springframework.web.servlet.resource;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/VersionPathStrategy.class */
public interface VersionPathStrategy {
    @Nullable
    String extractVersion(String requestPath);

    String removeVersion(String requestPath, String version);

    String addVersion(String requestPath, String version);
}
