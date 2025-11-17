package org.springframework.validation;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/MessageCodeFormatter.class */
public interface MessageCodeFormatter {
    String format(String errorCode, @Nullable String objectName, @Nullable String field);
}
