package org.springframework.web.util;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/UriBuilderFactory.class */
public interface UriBuilderFactory extends UriTemplateHandler {
    UriBuilder uriString(String uriTemplate);

    UriBuilder builder();
}
