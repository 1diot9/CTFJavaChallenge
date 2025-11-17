package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotations.class */
public interface MergedAnnotations extends Iterable<MergedAnnotation<Annotation>> {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotations$SearchStrategy.class */
    public enum SearchStrategy {
        DIRECT,
        INHERITED_ANNOTATIONS,
        SUPERCLASS,
        TYPE_HIERARCHY
    }

    <A extends Annotation> boolean isPresent(Class<A> annotationType);

    boolean isPresent(String annotationType);

    <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType);

    boolean isDirectlyPresent(String annotationType);

    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType);

    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate);

    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector);

    <A extends Annotation> MergedAnnotation<A> get(String annotationType);

    <A extends Annotation> MergedAnnotation<A> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate);

    <A extends Annotation> MergedAnnotation<A> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector);

    <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType);

    <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType);

    Stream<MergedAnnotation<Annotation>> stream();

    static MergedAnnotations from(AnnotatedElement element) {
        return from(element, SearchStrategy.DIRECT);
    }

    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy) {
        return from(element, searchStrategy, RepeatableContainers.standardRepeatables());
    }

    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers) {
        return from(element, searchStrategy, repeatableContainers, AnnotationFilter.PLAIN);
    }

    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        return from(element, searchStrategy, Search.never, repeatableContainers, annotationFilter);
    }

    private static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, Predicate<Class<?>> searchEnclosingClass, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        Assert.notNull(element, "AnnotatedElement must not be null");
        Assert.notNull(searchStrategy, "SearchStrategy must not be null");
        Assert.notNull(searchEnclosingClass, "Predicate must not be null");
        Assert.notNull(repeatableContainers, "RepeatableContainers must not be null");
        Assert.notNull(annotationFilter, "AnnotationFilter must not be null");
        return TypeMappedAnnotations.from(element, searchStrategy, searchEnclosingClass, repeatableContainers, annotationFilter);
    }

    static MergedAnnotations from(Annotation... annotations) {
        return from(annotations, annotations);
    }

    static MergedAnnotations from(Object source, Annotation... annotations) {
        return from(source, annotations, RepeatableContainers.standardRepeatables());
    }

    static MergedAnnotations from(Object source, Annotation[] annotations, RepeatableContainers repeatableContainers) {
        return from(source, annotations, repeatableContainers, AnnotationFilter.PLAIN);
    }

    static MergedAnnotations from(Object source, Annotation[] annotations, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        Assert.notNull(repeatableContainers, "RepeatableContainers must not be null");
        Assert.notNull(annotationFilter, "AnnotationFilter must not be null");
        return TypeMappedAnnotations.from(source, annotations, repeatableContainers, annotationFilter);
    }

    static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
        return MergedAnnotationsCollection.of(annotations);
    }

    static Search search(SearchStrategy searchStrategy) {
        Assert.notNull(searchStrategy, "SearchStrategy must not be null");
        return new Search(searchStrategy);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotations$Search.class */
    public static final class Search {
        static final Predicate<Class<?>> always = clazz -> {
            return true;
        };
        static final Predicate<Class<?>> never = clazz -> {
            return false;
        };
        private final SearchStrategy searchStrategy;
        private Predicate<Class<?>> searchEnclosingClass = never;
        private RepeatableContainers repeatableContainers = RepeatableContainers.standardRepeatables();
        private AnnotationFilter annotationFilter = AnnotationFilter.PLAIN;

        private Search(SearchStrategy searchStrategy) {
            this.searchStrategy = searchStrategy;
        }

        public Search withEnclosingClasses(Predicate<Class<?>> searchEnclosingClass) {
            Assert.notNull(searchEnclosingClass, "Predicate must not be null");
            Assert.state(this.searchStrategy == SearchStrategy.TYPE_HIERARCHY, "A custom 'searchEnclosingClass' predicate can only be combined with SearchStrategy.TYPE_HIERARCHY");
            this.searchEnclosingClass = searchEnclosingClass;
            return this;
        }

        public Search withRepeatableContainers(RepeatableContainers repeatableContainers) {
            Assert.notNull(repeatableContainers, "RepeatableContainers must not be null");
            this.repeatableContainers = repeatableContainers;
            return this;
        }

        public Search withAnnotationFilter(AnnotationFilter annotationFilter) {
            Assert.notNull(annotationFilter, "AnnotationFilter must not be null");
            this.annotationFilter = annotationFilter;
            return this;
        }

        public MergedAnnotations from(AnnotatedElement element) {
            return MergedAnnotations.from(element, this.searchStrategy, this.searchEnclosingClass, this.repeatableContainers, this.annotationFilter);
        }
    }
}
