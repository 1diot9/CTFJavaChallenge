package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/converter/ConditionalConverter.class */
public interface ConditionalConverter {
    boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
}
