package org.springframework.web.servlet.mvc.condition;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/RequestCondition.class */
public interface RequestCondition<T> {
    T combine(T other);

    @Nullable
    T getMatchingCondition(HttpServletRequest request);

    int compareTo(T other, HttpServletRequest request);
}
