package org.springframework.validation;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/MessageCodesResolver.class */
public interface MessageCodesResolver {
    String[] resolveMessageCodes(String errorCode, String objectName);

    String[] resolveMessageCodes(String errorCode, String objectName, String field, @Nullable Class<?> fieldType);
}
