package org.springframework.aot.hint;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ConditionalHint.class */
public interface ConditionalHint {
    @Nullable
    TypeReference getReachableType();

    default boolean conditionMatches(ClassLoader classLoader) {
        TypeReference reachableType = getReachableType();
        if (reachableType != null) {
            return ClassUtils.isPresent(reachableType.getCanonicalName(), classLoader);
        }
        return true;
    }
}
