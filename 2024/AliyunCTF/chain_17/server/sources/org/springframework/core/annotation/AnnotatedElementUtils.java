package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotatedElementUtils.class */
public abstract class AnnotatedElementUtils {
    public static AnnotatedElement forAnnotations(Annotation... annotations) {
        return new AnnotatedElementForAnnotations(annotations);
    }

    public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return getMetaAnnotationTypes(element, element.getAnnotation(annotationType));
    }

    public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
        for (Annotation annotation : element.getAnnotations()) {
            if (annotation.annotationType().getName().equals(annotationName)) {
                return getMetaAnnotationTypes(element, annotation);
            }
        }
        return Collections.emptySet();
    }

    private static Set<String> getMetaAnnotationTypes(AnnotatedElement element, @Nullable Annotation annotation) {
        if (annotation == null) {
            return Collections.emptySet();
        }
        return (Set) getAnnotations(annotation.annotationType()).stream().map(mergedAnnotation -> {
            return mergedAnnotation.getType().getName();
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return getAnnotations(element).stream(annotationType).anyMatch((v0) -> {
            return v0.isMetaPresent();
        });
    }

    public static boolean hasMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
        return getAnnotations(element).stream(annotationName).anyMatch((v0) -> {
            return v0.isMetaPresent();
        });
    }

    public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (AnnotationFilter.PLAIN.matches(annotationType) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(element)) {
            return element.isAnnotationPresent(annotationType);
        }
        return getAnnotations(element).isPresent(annotationType);
    }

    public static boolean isAnnotated(AnnotatedElement element, String annotationName) {
        return getAnnotations(element).isPresent(annotationName);
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        MergedAnnotation<?> mergedAnnotation = getAnnotations(element).get(annotationType, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared());
        return getAnnotationAttributes(mergedAnnotation, false, false);
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName) {
        return getMergedAnnotationAttributes(element, annotationName, false, false);
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        MergedAnnotation<?> mergedAnnotation = getAnnotations(element).get(annotationName, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared());
        return getAnnotationAttributes(mergedAnnotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    @Nullable
    public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement annotatedElement, Class<A> cls) {
        if (AnnotationFilter.PLAIN.matches((Class<?>) cls) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return (A) annotatedElement.getDeclaredAnnotation(cls);
        }
        return getAnnotations(annotatedElement).get(cls, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared()).synthesize((v0) -> {
            return v0.isPresent();
        }).orElse(null);
    }

    public static <A extends Annotation> Set<A> getAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return (Set) getAnnotations(element).stream(annotationType).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    public static Set<Annotation> getAllMergedAnnotations(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes) {
        return (Set) getAnnotations(element).stream().filter(MergedAnnotationPredicates.typeIn(annotationTypes)).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return getMergedRepeatableAnnotations(element, annotationType, null);
    }

    public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
        return (Set) getRepeatableAnnotations(element, containerType, annotationType).stream(annotationType).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    @Nullable
    public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName) {
        return getAllAnnotationAttributes(element, annotationName, false, false);
    }

    @Nullable
    public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {
        MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, nestedAnnotationsAsMap);
        return (MultiValueMap) getAnnotations(element).stream(annotationName).filter(MergedAnnotationPredicates.unique((v0) -> {
            return v0.getMetaTypes();
        })).map((v0) -> {
            return v0.withNonMergedAttributes();
        }).collect(MergedAnnotationCollectors.toMultiValueMap(AnnotatedElementUtils::nullIfEmpty, adaptations));
    }

    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (AnnotationFilter.PLAIN.matches(annotationType) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(element)) {
            return element.isAnnotationPresent(annotationType);
        }
        return findAnnotations(element).isPresent(annotationType);
    }

    @Nullable
    public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        MergedAnnotation<?> mergedAnnotation = findAnnotations(element).get(annotationType, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared());
        return getAnnotationAttributes(mergedAnnotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    @Nullable
    public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        MergedAnnotation<?> mergedAnnotation = findAnnotations(element).get(annotationName, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared());
        return getAnnotationAttributes(mergedAnnotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    @Nullable
    public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement annotatedElement, Class<A> cls) {
        if (AnnotationFilter.PLAIN.matches((Class<?>) cls) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return (A) annotatedElement.getDeclaredAnnotation(cls);
        }
        return findAnnotations(annotatedElement).get(cls, (Predicate) null, MergedAnnotationSelectors.firstDirectlyDeclared()).synthesize((v0) -> {
            return v0.isPresent();
        }).orElse(null);
    }

    public static <A extends Annotation> Set<A> findAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return (Set) findAnnotations(element).stream(annotationType).sorted(highAggregateIndexesFirst()).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    public static Set<Annotation> findAllMergedAnnotations(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes) {
        return (Set) findAnnotations(element).stream().filter(MergedAnnotationPredicates.typeIn(annotationTypes)).sorted(highAggregateIndexesFirst()).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return findMergedRepeatableAnnotations(element, annotationType, null);
    }

    public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
        return (Set) findRepeatableAnnotations(element, containerType, annotationType).stream(annotationType).sorted(highAggregateIndexesFirst()).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    private static MergedAnnotations getAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none());
    }

    private static MergedAnnotations getRepeatableAnnotations(AnnotatedElement element, @Nullable Class<? extends Annotation> containerType, Class<? extends Annotation> annotationType) {
        RepeatableContainers repeatableContainers;
        if (containerType == null) {
            RepeatableContainers.of(annotationType, null);
            repeatableContainers = RepeatableContainers.standardRepeatables();
        } else {
            repeatableContainers = RepeatableContainers.of(annotationType, containerType);
        }
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, repeatableContainers);
    }

    private static MergedAnnotations findAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
    }

    private static MergedAnnotations findRepeatableAnnotations(AnnotatedElement element, @Nullable Class<? extends Annotation> containerType, Class<? extends Annotation> annotationType) {
        RepeatableContainers repeatableContainers;
        if (containerType == null) {
            RepeatableContainers.of(annotationType, null);
            repeatableContainers = RepeatableContainers.standardRepeatables();
        } else {
            repeatableContainers = RepeatableContainers.of(annotationType, containerType);
        }
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, repeatableContainers);
    }

    @Nullable
    private static MultiValueMap<String, Object> nullIfEmpty(MultiValueMap<String, Object> map) {
        if (map.isEmpty()) {
            return null;
        }
        return map;
    }

    private static <A extends Annotation> Comparator<MergedAnnotation<A>> highAggregateIndexesFirst() {
        return Comparator.comparingInt((v0) -> {
            return v0.getAggregateIndex();
        }).reversed();
    }

    @Nullable
    private static AnnotationAttributes getAnnotationAttributes(MergedAnnotation<?> annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        if (!annotation.isPresent()) {
            return null;
        }
        return annotation.asAnnotationAttributes(MergedAnnotation.Adapt.values(classValuesAsString, nestedAnnotationsAsMap));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotatedElementUtils$AnnotatedElementForAnnotations.class */
    private static class AnnotatedElementForAnnotations implements AnnotatedElement {
        private final Annotation[] annotations;

        AnnotatedElementForAnnotations(Annotation... annotations) {
            this.annotations = annotations;
        }

        @Override // java.lang.reflect.AnnotatedElement
        @Nullable
        public <T extends Annotation> T getAnnotation(Class<T> cls) {
            for (Annotation annotation : this.annotations) {
                T t = (T) annotation;
                if (t.annotationType() == cls) {
                    return t;
                }
            }
            return null;
        }

        @Override // java.lang.reflect.AnnotatedElement
        public Annotation[] getAnnotations() {
            return (Annotation[]) this.annotations.clone();
        }

        @Override // java.lang.reflect.AnnotatedElement
        public Annotation[] getDeclaredAnnotations() {
            return (Annotation[]) this.annotations.clone();
        }
    }
}
