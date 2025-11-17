package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationTypeMappings.class */
public final class AnnotationTypeMappings {
    private static final IntrospectionFailureLogger failureLogger = IntrospectionFailureLogger.DEBUG;
    private static final Map<AnnotationFilter, Cache> standardRepeatablesCache = new ConcurrentReferenceHashMap();
    private static final Map<AnnotationFilter, Cache> noRepeatablesCache = new ConcurrentReferenceHashMap();
    private final RepeatableContainers repeatableContainers;
    private final AnnotationFilter filter;
    private final List<AnnotationTypeMapping> mappings = new ArrayList();

    private AnnotationTypeMappings(RepeatableContainers repeatableContainers, AnnotationFilter filter, Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        this.repeatableContainers = repeatableContainers;
        this.filter = filter;
        addAllMappings(annotationType, visitedAnnotationTypes);
        this.mappings.forEach((v0) -> {
            v0.afterAllMappingsSet();
        });
    }

    private void addAllMappings(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        Deque<AnnotationTypeMapping> queue = new ArrayDeque<>();
        addIfPossible(queue, null, annotationType, null, visitedAnnotationTypes);
        while (!queue.isEmpty()) {
            AnnotationTypeMapping mapping = queue.removeFirst();
            this.mappings.add(mapping);
            addMetaAnnotationsToQueue(queue, mapping);
        }
    }

    private void addMetaAnnotationsToQueue(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source) {
        Annotation[] metaAnnotations = AnnotationsScanner.getDeclaredAnnotations(source.getAnnotationType(), false);
        for (Annotation metaAnnotation : metaAnnotations) {
            if (isMappable(source, metaAnnotation)) {
                Annotation[] repeatedAnnotations = this.repeatableContainers.findRepeatedAnnotations(metaAnnotation);
                if (repeatedAnnotations != null) {
                    for (Annotation repeatedAnnotation : repeatedAnnotations) {
                        if (isMappable(source, repeatedAnnotation)) {
                            addIfPossible(queue, source, repeatedAnnotation);
                        }
                    }
                } else {
                    addIfPossible(queue, source, metaAnnotation);
                }
            }
        }
    }

    private void addIfPossible(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source, Annotation ann) {
        addIfPossible(queue, source, ann.annotationType(), ann, new HashSet());
    }

    private void addIfPossible(Deque<AnnotationTypeMapping> queue, @Nullable AnnotationTypeMapping source, Class<? extends Annotation> annotationType, @Nullable Annotation ann, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        try {
            queue.addLast(new AnnotationTypeMapping(source, annotationType, ann, visitedAnnotationTypes));
        } catch (Exception ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            if (failureLogger.isEnabled()) {
                failureLogger.log("Failed to introspect meta-annotation " + annotationType.getName(), source != null ? source.getAnnotationType() : null, ex);
            }
        }
    }

    private boolean isMappable(AnnotationTypeMapping source, @Nullable Annotation metaAnnotation) {
        return (metaAnnotation == null || this.filter.matches(metaAnnotation) || AnnotationFilter.PLAIN.matches(source.getAnnotationType()) || isAlreadyMapped(source, metaAnnotation)) ? false : true;
    }

    private boolean isAlreadyMapped(AnnotationTypeMapping source, Annotation metaAnnotation) {
        Class<? extends Annotation> annotationType = metaAnnotation.annotationType();
        AnnotationTypeMapping annotationTypeMapping = source;
        while (true) {
            AnnotationTypeMapping mapping = annotationTypeMapping;
            if (mapping != null) {
                if (mapping.getAnnotationType() == annotationType) {
                    return true;
                }
                annotationTypeMapping = mapping.getSource();
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int size() {
        return this.mappings.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationTypeMapping get(int index) {
        return this.mappings.get(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType) {
        return forAnnotationType(annotationType, new HashSet());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        return forAnnotationType(annotationType, RepeatableContainers.standardRepeatables(), AnnotationFilter.PLAIN, visitedAnnotationTypes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        return forAnnotationType(annotationType, repeatableContainers, annotationFilter, new HashSet());
    }

    static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        if (repeatableContainers == RepeatableContainers.standardRepeatables()) {
            return standardRepeatablesCache.computeIfAbsent(annotationFilter, key -> {
                return new Cache(repeatableContainers, key);
            }).get(annotationType, visitedAnnotationTypes);
        }
        if (repeatableContainers == RepeatableContainers.none()) {
            return noRepeatablesCache.computeIfAbsent(annotationFilter, key2 -> {
                return new Cache(repeatableContainers, key2);
            }).get(annotationType, visitedAnnotationTypes);
        }
        return new AnnotationTypeMappings(repeatableContainers, annotationFilter, annotationType, visitedAnnotationTypes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void clearCache() {
        standardRepeatablesCache.clear();
        noRepeatablesCache.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationTypeMappings$Cache.class */
    public static class Cache {
        private final RepeatableContainers repeatableContainers;
        private final AnnotationFilter filter;
        private final Map<Class<? extends Annotation>, AnnotationTypeMappings> mappings = new ConcurrentReferenceHashMap();

        Cache(RepeatableContainers repeatableContainers, AnnotationFilter filter) {
            this.repeatableContainers = repeatableContainers;
            this.filter = filter;
        }

        AnnotationTypeMappings get(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
            return this.mappings.computeIfAbsent(annotationType, key -> {
                return createMappings(key, visitedAnnotationTypes);
            });
        }

        private AnnotationTypeMappings createMappings(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
            return new AnnotationTypeMappings(this.repeatableContainers, this.filter, annotationType, visitedAnnotationTypes);
        }
    }
}
