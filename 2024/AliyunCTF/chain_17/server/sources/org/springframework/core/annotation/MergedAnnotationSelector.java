package org.springframework.core.annotation;

import java.lang.annotation.Annotation;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationSelector.class */
public interface MergedAnnotationSelector<A extends Annotation> {
    MergedAnnotation<A> select(MergedAnnotation<A> existing, MergedAnnotation<A> candidate);

    default boolean isBestCandidate(MergedAnnotation<A> annotation) {
        return false;
    }
}
