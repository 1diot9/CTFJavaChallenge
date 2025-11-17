package org.springframework.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/RequestDataValueProcessor.class */
public interface RequestDataValueProcessor {
    String processAction(HttpServletRequest request, String action, String httpMethod);

    String processFormFieldValue(HttpServletRequest request, @Nullable String name, String value, String type);

    @Nullable
    Map<String, String> getExtraHiddenFields(HttpServletRequest request);

    String processUrl(HttpServletRequest request, String url);
}
