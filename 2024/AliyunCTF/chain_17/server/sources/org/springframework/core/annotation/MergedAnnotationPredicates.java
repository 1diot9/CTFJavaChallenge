package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationPredicates.class */
public abstract class MergedAnnotationPredicates {
    private MergedAnnotationPredicates() {
    }

    public static <A extends Annotation> Predicate<MergedAnnotation<? extends A>> typeIn(String... typeNames) {
        return annotation -> {
            return ObjectUtils.containsElement(typeNames, annotation.getType().getName());
        };
    }

    public static <A extends Annotation> Predicate<MergedAnnotation<? extends A>> typeIn(Class<?>... types) {
        return annotation -> {
            return ObjectUtils.containsElement(types, annotation.getType());
        };
    }

    public static <A extends Annotation> Predicate<MergedAnnotation<? extends A>> typeIn(Collection<?> types) {
        return annotation -> {
            return types.stream().map(type -> {
                if (!(type instanceof Class)) {
                    return type.toString();
                }
                Class<?> clazz = (Class) type;
                return clazz.getName();
            }).anyMatch(typeName -> {
                return typeName.equals(annotation.getType().getName());
            });
        };
    }

    public static <A extends Annotation> Predicate<MergedAnnotation<A>> firstRunOf(Function<? super MergedAnnotation<A>, ?> valueExtractor) {
        return new FirstRunOfPredicate(valueExtractor);
    }

    public static <A extends Annotation, K> Predicate<MergedAnnotation<A>> unique(Function<? super MergedAnnotation<A>, K> keyExtractor) {
        return new UniquePredicate(keyExtractor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationPredicates$FirstRunOfPredicate.class */
    public static class FirstRunOfPredicate<A extends Annotation> implements Predicate<MergedAnnotation<A>> {
        private final Function<? super MergedAnnotation<A>, ?> valueExtractor;
        private boolean hasLastValue;

        @Nullable
        private Object lastValue;

        FirstRunOfPredicate(Function<? super MergedAnnotation<A>, ?> valueExtractor) {
            Assert.notNull(valueExtractor, "Value extractor must not be null");
            this.valueExtractor = valueExtractor;
        }

        @Override // java.util.function.Predicate
        public boolean test(@Nullable MergedAnnotation<A> annotation) {
            if (!this.hasLastValue) {
                this.hasLastValue = true;
                this.lastValue = this.valueExtractor.apply(annotation);
            }
            Object value = this.valueExtractor.apply(annotation);
            return ObjectUtils.nullSafeEquals(value, this.lastValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationPredicates$UniquePredicate.class */
    public static class UniquePredicate<A extends Annotation, K> implements Predicate<MergedAnnotation<A>> {
        private final Function<? super MergedAnnotation<A>, K> keyExtractor;
        private final Set<K> seen = new HashSet();

        UniquePredicate(Function<? super MergedAnnotation<A>, K> keyExtractor) {
            Assert.notNull(keyExtractor, "Key extractor must not be null");
            this.keyExtractor = keyExtractor;
        }

        @Override // java.util.function.Predicate
        public boolean test(@Nullable MergedAnnotation<A> annotation) {
            K key = this.keyExtractor.apply(annotation);
            return this.seen.add(key);
        }
    }
}
