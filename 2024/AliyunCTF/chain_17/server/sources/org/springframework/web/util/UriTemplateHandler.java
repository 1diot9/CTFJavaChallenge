package org.springframework.web.util;

import java.net.URI;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/UriTemplateHandler.class */
public interface UriTemplateHandler {
    URI expand(String uriTemplate, Map<String, ?> uriVariables);

    URI expand(String uriTemplate, Object... uriVariables);
}
