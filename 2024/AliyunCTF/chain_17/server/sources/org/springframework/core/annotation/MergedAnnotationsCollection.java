package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationsCollection.class */
public final class MergedAnnotationsCollection implements MergedAnnotations {
    private final MergedAnnotation<?>[] annotations;
    private final AnnotationTypeMappings[] mappings;

    private MergedAnnotationsCollection(Collection<MergedAnnotation<?>> annotations) {
        Assert.notNull(annotations, "Annotations must not be null");
        this.annotations = (MergedAnnotation[]) annotations.toArray(new MergedAnnotation[0]);
        this.mappings = new AnnotationTypeMappings[this.annotations.length];
        for (int i = 0; i < this.annotations.length; i++) {
            MergedAnnotation<?> annotation = this.annotations[i];
            Assert.notNull(annotation, "Annotation must not be null");
            Assert.isTrue(annotation.isDirectlyPresent(), "Annotation must be directly present");
            Assert.isTrue(annotation.getAggregateIndex() == 0, "Annotation must have aggregate index of zero");
            this.mappings[i] = AnnotationTypeMappings.forAnnotationType(annotation.getType());
        }
    }

    @Override // java.lang.Iterable
    public Iterator<MergedAnnotation<Annotation>> iterator() {
        return Spliterators.iterator(spliterator());
    }

    @Override // java.lang.Iterable
    public Spliterator<MergedAnnotation<Annotation>> spliterator() {
        return spliterator(null);
    }

    private <A extends Annotation> Spliterator<MergedAnnotation<A>> spliterator(@Nullable Object annotationType) {
        return new AnnotationsSpliterator(annotationType);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> boolean isPresent(Class<A> annotationType) {
        return isPresent(annotationType, false);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public boolean isPresent(String annotationType) {
        return isPresent(annotationType, false);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType) {
        return isPresent(annotationType, true);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public boolean isDirectlyPresent(String annotationType) {
        return isPresent(annotationType, true);
    }

    private boolean isPresent(Object requiredType, boolean directOnly) {
        for (MergedAnnotation<?> annotation : this.annotations) {
            Class<?> type = annotation.getType();
            if (type == requiredType || type.getName().equals(requiredType)) {
                return true;
            }
        }
        if (!directOnly) {
            for (AnnotationTypeMappings mappings : this.mappings) {
                for (int i = 1; i < mappings.size(); i++) {
                    AnnotationTypeMapping mapping = mappings.get(i);
                    if (isMappingForType(mapping, requiredType)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
        return get(annotationType, (Predicate) null, (MergedAnnotationSelector) null);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate) {
        return get(annotationType, predicate, (MergedAnnotationSelector) null);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
        MergedAnnotation<A> result = find(annotationType, predicate, selector);
        return result != null ? result : MergedAnnotation.missing();
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType) {
        return get(annotationType, (Predicate) null, (MergedAnnotationSelector) null);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate) {
        return get(annotationType, predicate, (MergedAnnotationSelector) null);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
        MergedAnnotation<A> result = find(annotationType, predicate, selector);
        return result != null ? result : MergedAnnotation.missing();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    private <A extends Annotation> MergedAnnotation<A> find(Object requiredType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
        if (selector == null) {
            selector = MergedAnnotationSelectors.nearest();
        }
        MergedAnnotation<A> result = null;
        for (int i = 0; i < this.annotations.length; i++) {
            MissingMergedAnnotation missingMergedAnnotation = this.annotations[i];
            AnnotationTypeMappings mappings = this.mappings[i];
            int mappingIndex = 0;
            while (mappingIndex < mappings.size()) {
                AnnotationTypeMapping mapping = mappings.get(mappingIndex);
                if (isMappingForType(mapping, requiredType)) {
                    MergedAnnotation<A> candidate = mappingIndex == 0 ? missingMergedAnnotation : TypeMappedAnnotation.createIfPossible(mapping, missingMergedAnnotation, IntrospectionFailureLogger.INFO);
                    if (candidate != null && (predicate == null || predicate.test(candidate))) {
                        if (selector.isBestCandidate(candidate)) {
                            return candidate;
                        }
                        result = result != null ? selector.select(result, candidate) : candidate;
                    }
                }
                mappingIndex++;
            }
        }
        return result;
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType) {
        return StreamSupport.stream(spliterator(annotationType), false);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType) {
        return StreamSupport.stream(spliterator(annotationType), false);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public Stream<MergedAnnotation<Annotation>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private static boolean isMappingForType(AnnotationTypeMapping mapping, @Nullable Object requiredType) {
        Class<? extends Annotation> actualType;
        return requiredType == null || (actualType = mapping.getAnnotationType()) == requiredType || actualType.getName().equals(requiredType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
        Assert.notNull(annotations, "Annotations must not be null");
        if (annotations.isEmpty()) {
            return TypeMappedAnnotations.NONE;
        }
        return new MergedAnnotationsCollection(annotations);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationsCollection$AnnotationsSpliterator.class */
    public class AnnotationsSpliterator<A extends Annotation> implements Spliterator<MergedAnnotation<A>> {

        @Nullable
        private final Object requiredType;
        private final int[] mappingCursors;

        public AnnotationsSpliterator(@Nullable Object requiredType) {
            this.mappingCursors = new int[MergedAnnotationsCollection.this.annotations.length];
            this.requiredType = requiredType;
        }

        @Override // java.util.Spliterator
        public boolean tryAdvance(Consumer<? super MergedAnnotation<A>> action) {
            int lowestDistance = Integer.MAX_VALUE;
            int annotationResult = -1;
            for (int annotationIndex = 0; annotationIndex < MergedAnnotationsCollection.this.annotations.length; annotationIndex++) {
                AnnotationTypeMapping mapping = getNextSuitableMapping(annotationIndex);
                if (mapping != null && mapping.getDistance() < lowestDistance) {
                    annotationResult = annotationIndex;
                    lowestDistance = mapping.getDistance();
                }
                if (lowestDistance == 0) {
                    break;
                }
            }
            if (annotationResult != -1) {
                MergedAnnotation<A> mergedAnnotation = createMergedAnnotationIfPossible(annotationResult, this.mappingCursors[annotationResult]);
                int[] iArr = this.mappingCursors;
                int i = annotationResult;
                iArr[i] = iArr[i] + 1;
                if (mergedAnnotation == null) {
                    return tryAdvance(action);
                }
                action.accept(mergedAnnotation);
                return true;
            }
            return false;
        }

        @Nullable
        private AnnotationTypeMapping getNextSuitableMapping(int annotationIndex) {
            AnnotationTypeMapping mapping;
            do {
                mapping = getMapping(annotationIndex, this.mappingCursors[annotationIndex]);
                if (mapping != null && MergedAnnotationsCollection.isMappingForType(mapping, this.requiredType)) {
                    return mapping;
                }
                int[] iArr = this.mappingCursors;
                iArr[annotationIndex] = iArr[annotationIndex] + 1;
            } while (mapping != null);
            return null;
        }

        @Nullable
        private AnnotationTypeMapping getMapping(int annotationIndex, int mappingIndex) {
            AnnotationTypeMappings mappings = MergedAnnotationsCollection.this.mappings[annotationIndex];
            if (mappingIndex < mappings.size()) {
                return mappings.get(mappingIndex);
            }
            return null;
        }

        @Nullable
        private MergedAnnotation<A> createMergedAnnotationIfPossible(int i, int i2) {
            MergedAnnotation<A> mergedAnnotation = (MergedAnnotation<A>) MergedAnnotationsCollection.this.annotations[i];
            if (i2 == 0) {
                return mergedAnnotation;
            }
            return TypeMappedAnnotation.createIfPossible(MergedAnnotationsCollection.this.mappings[i].get(i2), mergedAnnotation, this.requiredType != null ? IntrospectionFailureLogger.INFO : IntrospectionFailureLogger.DEBUG);
        }

        @Override // java.util.Spliterator
        @Nullable
        public Spliterator<MergedAnnotation<A>> trySplit() {
            return null;
        }

        @Override // java.util.Spliterator
        public long estimateSize() {
            int size = 0;
            for (int i = 0; i < MergedAnnotationsCollection.this.annotations.length; i++) {
                AnnotationTypeMappings mappings = MergedAnnotationsCollection.this.mappings[i];
                int numberOfMappings = mappings.size();
                size += numberOfMappings - Math.min(this.mappingCursors[i], mappings.size());
            }
            return size;
        }

        @Override // java.util.Spliterator
        public int characteristics() {
            return 1280;
        }
    }
}
