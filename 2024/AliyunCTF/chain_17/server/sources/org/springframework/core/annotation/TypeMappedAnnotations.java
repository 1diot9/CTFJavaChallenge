package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/TypeMappedAnnotations.class */
public final class TypeMappedAnnotations implements MergedAnnotations {
    static final MergedAnnotations NONE = new TypeMappedAnnotations(null, new Annotation[0], RepeatableContainers.none(), AnnotationFilter.ALL);

    @Nullable
    private final Object source;

    @Nullable
    private final AnnotatedElement element;

    @Nullable
    private final MergedAnnotations.SearchStrategy searchStrategy;
    private final Predicate<Class<?>> searchEnclosingClass;

    @Nullable
    private final Annotation[] annotations;
    private final RepeatableContainers repeatableContainers;
    private final AnnotationFilter annotationFilter;

    @Nullable
    private volatile List<Aggregate> aggregates;

    private TypeMappedAnnotations(AnnotatedElement element, MergedAnnotations.SearchStrategy searchStrategy, Predicate<Class<?>> searchEnclosingClass, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        this.source = element;
        this.element = element;
        this.searchStrategy = searchStrategy;
        this.searchEnclosingClass = searchEnclosingClass;
        this.annotations = null;
        this.repeatableContainers = repeatableContainers;
        this.annotationFilter = annotationFilter;
    }

    private TypeMappedAnnotations(@Nullable Object source, Annotation[] annotations, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        this.source = source;
        this.element = null;
        this.searchStrategy = null;
        this.searchEnclosingClass = MergedAnnotations.Search.never;
        this.annotations = annotations;
        this.repeatableContainers = repeatableContainers;
        this.annotationFilter = annotationFilter;
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> boolean isPresent(Class<A> annotationType) {
        if (this.annotationFilter.matches((Class<?>) annotationType)) {
            return false;
        }
        return Boolean.TRUE.equals(scan(annotationType, IsPresent.get(this.repeatableContainers, this.annotationFilter, false)));
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public boolean isPresent(String annotationType) {
        if (this.annotationFilter.matches(annotationType)) {
            return false;
        }
        return Boolean.TRUE.equals(scan(annotationType, IsPresent.get(this.repeatableContainers, this.annotationFilter, false)));
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType) {
        if (this.annotationFilter.matches((Class<?>) annotationType)) {
            return false;
        }
        return Boolean.TRUE.equals(scan(annotationType, IsPresent.get(this.repeatableContainers, this.annotationFilter, true)));
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public boolean isDirectlyPresent(String annotationType) {
        if (this.annotationFilter.matches(annotationType)) {
            return false;
        }
        return Boolean.TRUE.equals(scan(annotationType, IsPresent.get(this.repeatableContainers, this.annotationFilter, true)));
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
        if (this.annotationFilter.matches((Class<?>) annotationType)) {
            return MergedAnnotation.missing();
        }
        MergedAnnotation<A> result = (MergedAnnotation) scan(annotationType, new MergedAnnotationFinder(annotationType, predicate, selector));
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
        if (this.annotationFilter.matches(annotationType)) {
            return MergedAnnotation.missing();
        }
        MergedAnnotation<A> result = (MergedAnnotation) scan(annotationType, new MergedAnnotationFinder(annotationType, predicate, selector));
        return result != null ? result : MergedAnnotation.missing();
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType) {
        if (this.annotationFilter == AnnotationFilter.ALL) {
            return Stream.empty();
        }
        return StreamSupport.stream(spliterator(annotationType), false);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType) {
        if (this.annotationFilter == AnnotationFilter.ALL) {
            return Stream.empty();
        }
        return StreamSupport.stream(spliterator(annotationType), false);
    }

    @Override // org.springframework.core.annotation.MergedAnnotations
    public Stream<MergedAnnotation<Annotation>> stream() {
        if (this.annotationFilter == AnnotationFilter.ALL) {
            return Stream.empty();
        }
        return StreamSupport.stream(spliterator(), false);
    }

    @Override // java.lang.Iterable
    public Iterator<MergedAnnotation<Annotation>> iterator() {
        if (this.annotationFilter == AnnotationFilter.ALL) {
            return Collections.emptyIterator();
        }
        return Spliterators.iterator(spliterator());
    }

    @Override // java.lang.Iterable
    public Spliterator<MergedAnnotation<Annotation>> spliterator() {
        if (this.annotationFilter == AnnotationFilter.ALL) {
            return Spliterators.emptySpliterator();
        }
        return spliterator(null);
    }

    private <A extends Annotation> Spliterator<MergedAnnotation<A>> spliterator(@Nullable Object annotationType) {
        return new AggregatesSpliterator(annotationType, getAggregates());
    }

    private List<Aggregate> getAggregates() {
        List<Aggregate> aggregates = this.aggregates;
        if (aggregates == null) {
            aggregates = (List) scan(this, new AggregatesCollector());
            if (aggregates == null || aggregates.isEmpty()) {
                aggregates = Collections.emptyList();
            }
            this.aggregates = aggregates;
        }
        return aggregates;
    }

    @Nullable
    private <C, R> R scan(C c, AnnotationsProcessor<C, R> annotationsProcessor) {
        if (this.annotations != null) {
            return annotationsProcessor.finish(annotationsProcessor.doWithAnnotations(c, 0, this.source, this.annotations));
        }
        if (this.element != null && this.searchStrategy != null) {
            return (R) AnnotationsScanner.scan(c, this.element, this.searchStrategy, this.searchEnclosingClass, annotationsProcessor);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MergedAnnotations from(AnnotatedElement element, MergedAnnotations.SearchStrategy searchStrategy, Predicate<Class<?>> searchEnclosingClass, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        if (AnnotationsScanner.isKnownEmpty(element, searchStrategy, searchEnclosingClass)) {
            return NONE;
        }
        return new TypeMappedAnnotations(element, searchStrategy, searchEnclosingClass, repeatableContainers, annotationFilter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MergedAnnotations from(@Nullable Object source, Annotation[] annotations, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        if (annotations.length == 0) {
            return NONE;
        }
        return new TypeMappedAnnotations(source, annotations, repeatableContainers, annotationFilter);
    }

    private static boolean isMappingForType(AnnotationTypeMapping mapping, AnnotationFilter annotationFilter, @Nullable Object requiredType) {
        Class<? extends Annotation> actualType = mapping.getAnnotationType();
        return !annotationFilter.matches(actualType) && (requiredType == null || actualType == requiredType || actualType.getName().equals(requiredType));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/TypeMappedAnnotations$IsPresent.class */
    private static final class IsPresent implements AnnotationsProcessor<Object, Boolean> {
        private static final IsPresent[] SHARED = new IsPresent[4];
        private final RepeatableContainers repeatableContainers;
        private final AnnotationFilter annotationFilter;
        private final boolean directOnly;

        static {
            SHARED[0] = new IsPresent(RepeatableContainers.none(), AnnotationFilter.PLAIN, true);
            SHARED[1] = new IsPresent(RepeatableContainers.none(), AnnotationFilter.PLAIN, false);
            SHARED[2] = new IsPresent(RepeatableContainers.standardRepeatables(), AnnotationFilter.PLAIN, true);
            SHARED[3] = new IsPresent(RepeatableContainers.standardRepeatables(), AnnotationFilter.PLAIN, false);
        }

        private IsPresent(RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter, boolean directOnly) {
            this.repeatableContainers = repeatableContainers;
            this.annotationFilter = annotationFilter;
            this.directOnly = directOnly;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.core.annotation.AnnotationsProcessor
        @Nullable
        public Boolean doWithAnnotations(Object requiredType, int aggregateIndex, @Nullable Object source, Annotation[] annotations) {
            Class<? extends Annotation> type;
            Boolean result;
            for (Annotation annotation : annotations) {
                if (annotation != null && (type = annotation.annotationType()) != null && !this.annotationFilter.matches(type)) {
                    if (type == requiredType || type.getName().equals(requiredType)) {
                        return Boolean.TRUE;
                    }
                    Annotation[] repeatedAnnotations = this.repeatableContainers.findRepeatedAnnotations(annotation);
                    if (repeatedAnnotations != null && (result = doWithAnnotations(requiredType, aggregateIndex, source, repeatedAnnotations)) != null) {
                        return result;
                    }
                    if (this.directOnly) {
                        continue;
                    } else {
                        AnnotationTypeMappings mappings = AnnotationTypeMappings.forAnnotationType(type);
                        for (int i = 0; i < mappings.size(); i++) {
                            AnnotationTypeMapping mapping = mappings.get(i);
                            if (TypeMappedAnnotations.isMappingForType(mapping, this.annotationFilter, requiredType)) {
                                return Boolean.TRUE;
                            }
                        }
                    }
                }
            }
            return null;
        }

        static IsPresent get(RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter, boolean directOnly) {
            if (annotationFilter == AnnotationFilter.PLAIN) {
                if (repeatableContainers == RepeatableContainers.none()) {
                    return SHARED[directOnly ? (char) 0 : (char) 1];
                }
                if (repeatableContainers == RepeatableContainers.standardRepeatables()) {
                    return SHARED[directOnly ? (char) 2 : (char) 3];
                }
            }
            return new IsPresent(repeatableContainers, annotationFilter, directOnly);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/TypeMappedAnnotations$MergedAnnotationFinder.class */
    public class MergedAnnotationFinder<A extends Annotation> implements AnnotationsProcessor<Object, MergedAnnotation<A>> {
        private final Object requiredType;

        @Nullable
        private final Predicate<? super MergedAnnotation<A>> predicate;
        private final MergedAnnotationSelector<A> selector;

        @Nullable
        private MergedAnnotation<A> result;

        MergedAnnotationFinder(Object requiredType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
            this.requiredType = requiredType;
            this.predicate = predicate;
            this.selector = selector != null ? selector : MergedAnnotationSelectors.nearest();
        }

        @Override // org.springframework.core.annotation.AnnotationsProcessor
        @Nullable
        public MergedAnnotation<A> doWithAggregate(Object context, int aggregateIndex) {
            return this.result;
        }

        @Override // org.springframework.core.annotation.AnnotationsProcessor
        @Nullable
        public MergedAnnotation<A> doWithAnnotations(Object type, int aggregateIndex, @Nullable Object source, Annotation[] annotations) {
            MergedAnnotation<A> result;
            for (Annotation annotation : annotations) {
                if (annotation != null && !TypeMappedAnnotations.this.annotationFilter.matches(annotation) && (result = process(type, aggregateIndex, source, annotation)) != null) {
                    return result;
                }
            }
            return null;
        }

        @Nullable
        private MergedAnnotation<A> process(Object type, int aggregateIndex, @Nullable Object source, Annotation annotation) {
            MergedAnnotation<A> candidate;
            Annotation[] repeatedAnnotations = TypeMappedAnnotations.this.repeatableContainers.findRepeatedAnnotations(annotation);
            if (repeatedAnnotations != null) {
                return doWithAnnotations(type, aggregateIndex, source, repeatedAnnotations);
            }
            AnnotationTypeMappings mappings = AnnotationTypeMappings.forAnnotationType(annotation.annotationType(), TypeMappedAnnotations.this.repeatableContainers, TypeMappedAnnotations.this.annotationFilter);
            for (int i = 0; i < mappings.size(); i++) {
                AnnotationTypeMapping mapping = mappings.get(i);
                if (TypeMappedAnnotations.isMappingForType(mapping, TypeMappedAnnotations.this.annotationFilter, this.requiredType) && (candidate = TypeMappedAnnotation.createIfPossible(mapping, source, annotation, aggregateIndex, IntrospectionFailureLogger.INFO)) != null && (this.predicate == null || this.predicate.test(candidate))) {
                    if (this.selector.isBestCandidate(candidate)) {
                        return candidate;
                    }
                    updateLastResult(candidate);
                }
            }
            return null;
        }

        private void updateLastResult(MergedAnnotation<A> candidate) {
            MergedAnnotation<A> lastResult = this.result;
            this.result = lastResult != null ? this.selector.select(lastResult, candidate) : candidate;
        }

        @Override // org.springframework.core.annotation.AnnotationsProcessor
        @Nullable
        public MergedAnnotation<A> finish(@Nullable MergedAnnotation<A> result) {
            return result != null ? result : this.result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/TypeMappedAnnotations$AggregatesCollector.class */
    public class AggregatesCollector implements AnnotationsProcessor<Object, List<Aggregate>> {
        private final List<Aggregate> aggregates = new ArrayList();

        private AggregatesCollector() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.core.annotation.AnnotationsProcessor
        @Nullable
        public List<Aggregate> doWithAnnotations(Object criteria, int aggregateIndex, @Nullable Object source, Annotation[] annotations) {
            this.aggregates.add(createAggregate(aggregateIndex, source, annotations));
            return null;
        }

        private Aggregate createAggregate(int aggregateIndex, @Nullable Object source, Annotation[] annotations) {
            List<Annotation> aggregateAnnotations = getAggregateAnnotations(annotations);
            return new Aggregate(aggregateIndex, source, aggregateAnnotations);
        }

        private List<Annotation> getAggregateAnnotations(Annotation[] annotations) {
            List<Annotation> result = new ArrayList<>(annotations.length);
            addAggregateAnnotations(result, annotations);
            return result;
        }

        private void addAggregateAnnotations(List<Annotation> aggregateAnnotations, Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (annotation != null && !TypeMappedAnnotations.this.annotationFilter.matches(annotation)) {
                    Annotation[] repeatedAnnotations = TypeMappedAnnotations.this.repeatableContainers.findRepeatedAnnotations(annotation);
                    if (repeatedAnnotations != null) {
                        addAggregateAnnotations(aggregateAnnotations, repeatedAnnotations);
                    } else {
                        aggregateAnnotations.add(annotation);
                    }
                }
            }
        }

        @Override // org.springframework.core.annotation.AnnotationsProcessor
        public List<Aggregate> finish(@Nullable List<Aggregate> processResult) {
            return this.aggregates;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/TypeMappedAnnotations$Aggregate.class */
    public static class Aggregate {
        private final int aggregateIndex;

        @Nullable
        private final Object source;
        private final List<Annotation> annotations;
        private final AnnotationTypeMappings[] mappings;

        Aggregate(int aggregateIndex, @Nullable Object source, List<Annotation> annotations) {
            this.aggregateIndex = aggregateIndex;
            this.source = source;
            this.annotations = annotations;
            this.mappings = new AnnotationTypeMappings[annotations.size()];
            for (int i = 0; i < annotations.size(); i++) {
                this.mappings[i] = AnnotationTypeMappings.forAnnotationType(annotations.get(i).annotationType());
            }
        }

        int size() {
            return this.annotations.size();
        }

        @Nullable
        AnnotationTypeMapping getMapping(int annotationIndex, int mappingIndex) {
            AnnotationTypeMappings mappings = getMappings(annotationIndex);
            if (mappingIndex < mappings.size()) {
                return mappings.get(mappingIndex);
            }
            return null;
        }

        AnnotationTypeMappings getMappings(int annotationIndex) {
            return this.mappings[annotationIndex];
        }

        @Nullable
        <A extends Annotation> MergedAnnotation<A> createMergedAnnotationIfPossible(int annotationIndex, int mappingIndex, IntrospectionFailureLogger logger) {
            return TypeMappedAnnotation.createIfPossible(this.mappings[annotationIndex].get(mappingIndex), this.source, this.annotations.get(annotationIndex), this.aggregateIndex, logger);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/TypeMappedAnnotations$AggregatesSpliterator.class */
    public class AggregatesSpliterator<A extends Annotation> implements Spliterator<MergedAnnotation<A>> {

        @Nullable
        private final Object requiredType;
        private final List<Aggregate> aggregates;
        private int aggregateCursor = 0;

        @Nullable
        private int[] mappingCursors;

        AggregatesSpliterator(@Nullable Object requiredType, List<Aggregate> aggregates) {
            this.requiredType = requiredType;
            this.aggregates = aggregates;
        }

        @Override // java.util.Spliterator
        public boolean tryAdvance(Consumer<? super MergedAnnotation<A>> action) {
            while (this.aggregateCursor < this.aggregates.size()) {
                Aggregate aggregate = this.aggregates.get(this.aggregateCursor);
                if (tryAdvance(aggregate, action)) {
                    return true;
                }
                this.aggregateCursor++;
                this.mappingCursors = null;
            }
            return false;
        }

        private boolean tryAdvance(Aggregate aggregate, Consumer<? super MergedAnnotation<A>> action) {
            if (this.mappingCursors == null) {
                this.mappingCursors = new int[aggregate.size()];
            }
            int lowestDistance = Integer.MAX_VALUE;
            int annotationResult = -1;
            for (int annotationIndex = 0; annotationIndex < aggregate.size(); annotationIndex++) {
                AnnotationTypeMapping mapping = getNextSuitableMapping(aggregate, annotationIndex);
                if (mapping != null && mapping.getDistance() < lowestDistance) {
                    annotationResult = annotationIndex;
                    lowestDistance = mapping.getDistance();
                }
                if (lowestDistance == 0) {
                    break;
                }
            }
            if (annotationResult != -1) {
                MergedAnnotation<A> mergedAnnotation = aggregate.createMergedAnnotationIfPossible(annotationResult, this.mappingCursors[annotationResult], this.requiredType != null ? IntrospectionFailureLogger.INFO : IntrospectionFailureLogger.DEBUG);
                int[] iArr = this.mappingCursors;
                int i = annotationResult;
                iArr[i] = iArr[i] + 1;
                if (mergedAnnotation == null) {
                    return tryAdvance(aggregate, action);
                }
                action.accept(mergedAnnotation);
                return true;
            }
            return false;
        }

        @Nullable
        private AnnotationTypeMapping getNextSuitableMapping(Aggregate aggregate, int annotationIndex) {
            AnnotationTypeMapping mapping;
            int[] cursors = this.mappingCursors;
            if (cursors == null) {
                return null;
            }
            do {
                mapping = aggregate.getMapping(annotationIndex, cursors[annotationIndex]);
                if (mapping != null && TypeMappedAnnotations.isMappingForType(mapping, TypeMappedAnnotations.this.annotationFilter, this.requiredType)) {
                    return mapping;
                }
                cursors[annotationIndex] = cursors[annotationIndex] + 1;
            } while (mapping != null);
            return null;
        }

        @Override // java.util.Spliterator
        @Nullable
        public Spliterator<MergedAnnotation<A>> trySplit() {
            return null;
        }

        @Override // java.util.Spliterator
        public long estimateSize() {
            int size = 0;
            for (int aggregateIndex = this.aggregateCursor; aggregateIndex < this.aggregates.size(); aggregateIndex++) {
                Aggregate aggregate = this.aggregates.get(aggregateIndex);
                for (int annotationIndex = 0; annotationIndex < aggregate.size(); annotationIndex++) {
                    AnnotationTypeMappings mappings = aggregate.getMappings(annotationIndex);
                    int numberOfMappings = mappings.size();
                    if (aggregateIndex == this.aggregateCursor && this.mappingCursors != null) {
                        numberOfMappings -= Math.min(this.mappingCursors[annotationIndex], mappings.size());
                    }
                    size += numberOfMappings;
                }
            }
            return size;
        }

        @Override // java.util.Spliterator
        public int characteristics() {
            return 1280;
        }
    }
}
