package org.springframework.core.style;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/style/ToStringStyler.class */
public interface ToStringStyler {
    void styleStart(StringBuilder buffer, Object obj);

    void styleEnd(StringBuilder buffer, Object obj);

    void styleField(StringBuilder buffer, String fieldName, @Nullable Object value);

    void styleValue(StringBuilder buffer, Object value);

    void styleFieldSeparator(StringBuilder buffer);
}
