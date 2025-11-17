package org.springframework.core.annotation;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/ValueExtractor.class */
interface ValueExtractor {
    @Nullable
    Object extract(Method attribute, @Nullable Object object);
}
