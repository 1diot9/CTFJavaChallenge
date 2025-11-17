package org.springframework.boot.web.servlet.error;

import java.util.Collections;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.web.context.request.WebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/error/ErrorAttributes.class */
public interface ErrorAttributes {
    Throwable getError(WebRequest webRequest);

    default Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        return Collections.emptyMap();
    }
}
