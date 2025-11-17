package org.springframework.web.multipart;

import jakarta.servlet.http.HttpServletRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/MultipartResolver.class */
public interface MultipartResolver {
    boolean isMultipart(HttpServletRequest request);

    MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException;

    void cleanupMultipart(MultipartHttpServletRequest request);
}
