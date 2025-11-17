package org.springframework.core.type;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/AnnotationMetadata.class */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {
    Set<MethodMetadata> getAnnotatedMethods(String annotationName);

    Set<MethodMetadata> getDeclaredMethods();

    default Set<String> getAnnotationTypes() {
        return (Set) getAnnotations().stream().filter((v0) -> {
            return v0.isDirectlyPresent();
        }).map(annotation -> {
            return annotation.getType().getName();
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    default Set<String> getMetaAnnotationTypes(String annotationName) {
        MergedAnnotation<?> annotation = getAnnotations().get(annotationName, (v0) -> {
            return v0.isDirectlyPresent();
        });
        if (!annotation.isPresent()) {
            return Collections.emptySet();
        }
        return (Set) MergedAnnotations.from(annotation.getType()).stream().map(mergedAnnotation -> {
            return mergedAnnotation.getType().getName();
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    default boolean hasAnnotation(String annotationName) {
        return getAnnotations().isDirectlyPresent(annotationName);
    }

    default boolean hasMetaAnnotation(String metaAnnotationName) {
        return getAnnotations().get(metaAnnotationName, (v0) -> {
            return v0.isMetaPresent();
        }).isPresent();
    }

    default boolean hasAnnotatedMethods(String annotationName) {
        return !getAnnotatedMethods(annotationName).isEmpty();
    }

    static AnnotationMetadata introspect(Class<?> type) {
        return StandardAnnotationMetadata.from(type);
    }
}
