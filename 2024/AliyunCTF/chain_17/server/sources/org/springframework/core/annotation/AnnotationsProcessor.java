package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationsProcessor.class */
interface AnnotationsProcessor<C, R> {
    @Nullable
    R doWithAnnotations(C context, int aggregateIndex, @Nullable Object source, Annotation[] annotations);

    @Nullable
    default R doWithAggregate(C context, int aggregateIndex) {
        return null;
    }

    @Nullable
    default R finish(@Nullable R result) {
        return result;
    }
}
