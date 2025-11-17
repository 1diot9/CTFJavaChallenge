package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/NameValueExpression.class */
public interface NameValueExpression<T> {
    String getName();

    @Nullable
    T getValue();

    boolean isNegated();
}
