package org.springframework.core.type;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationCollectors;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotationSelectors;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/AnnotatedTypeMetadata.class */
public interface AnnotatedTypeMetadata {
    MergedAnnotations getAnnotations();

    default boolean isAnnotated(String annotationName) {
        return getAnnotations().isPresent(annotationName);
    }

    @Nullable
    default Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Nullable
    default Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        MergedAnnotation<Annotation> annotation = getAnnotations().get(annotationName, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared());
        if (!annotation.isPresent()) {
            return null;
        }
        return annotation.asAnnotationAttributes(MergedAnnotation.Adapt.values(classValuesAsString, true));
    }

    @Nullable
    default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Nullable
    default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, true);
        return (MultiValueMap) getAnnotations().stream(annotationName).filter(MergedAnnotationPredicates.unique((v0) -> {
            return v0.getMetaTypes();
        })).map((v0) -> {
            return v0.withNonMergedAttributes();
        }).collect(MergedAnnotationCollectors.toMultiValueMap(map -> {
            if (map.isEmpty()) {
                return null;
            }
            return map;
        }, adaptations));
    }

    default Set<AnnotationAttributes> getMergedRepeatableAnnotationAttributes(Class<? extends Annotation> annotationType, Class<? extends Annotation> containerType, boolean classValuesAsString) {
        return getMergedRepeatableAnnotationAttributes(annotationType, containerType, classValuesAsString, false);
    }

    default Set<AnnotationAttributes> getMergedRepeatableAnnotationAttributes(Class<? extends Annotation> annotationType, Class<? extends Annotation> containerType, boolean classValuesAsString, boolean sortByReversedMetaDistance) {
        return getMergedRepeatableAnnotationAttributes(annotationType, containerType, mergedAnnotation -> {
            return true;
        }, classValuesAsString, sortByReversedMetaDistance);
    }

    default Set<AnnotationAttributes> getMergedRepeatableAnnotationAttributes(Class<? extends Annotation> annotationType, Class<? extends Annotation> containerType, Predicate<MergedAnnotation<? extends Annotation>> predicate, boolean classValuesAsString, boolean sortByReversedMetaDistance) {
        Stream<MergedAnnotation<Annotation>> stream = getAnnotations().stream().filter(predicate).filter(MergedAnnotationPredicates.typeIn((Class<?>[]) new Class[]{containerType, annotationType}));
        if (sortByReversedMetaDistance) {
            stream = stream.sorted(reversedMetaDistance());
        }
        MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, true);
        return (Set) stream.map(annotation -> {
            return annotation.asAnnotationAttributes(adaptations);
        }).flatMap(attributes -> {
            if (containerType.equals(attributes.annotationType())) {
                return Stream.of((Object[]) attributes.getAnnotationArray("value"));
            }
            return Stream.of(attributes);
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Comparator<MergedAnnotation<Annotation>> reversedMetaDistance() {
        return Comparator.comparingInt((v0) -> {
            return v0.getDistance();
        }).reversed();
    }
}
