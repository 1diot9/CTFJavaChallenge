package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/HttpResource.class */
public interface HttpResource extends Resource {
    HttpHeaders getResponseHeaders();
}
