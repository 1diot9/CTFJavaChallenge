package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/MediaTypeExpression.class */
public interface MediaTypeExpression {
    MediaType getMediaType();

    boolean isNegated();
}
