package org.springframework.web.servlet.mvc;

import jakarta.servlet.http.HttpServletRequest;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/LastModified.class */
public interface LastModified {
    long getLastModified(HttpServletRequest request);
}
