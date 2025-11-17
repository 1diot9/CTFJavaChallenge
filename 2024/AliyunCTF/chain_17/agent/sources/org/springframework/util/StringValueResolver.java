package org.springframework.util;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/StringValueResolver.class */
public interface StringValueResolver {
    @Nullable
    String resolveStringValue(String strVal);
}
