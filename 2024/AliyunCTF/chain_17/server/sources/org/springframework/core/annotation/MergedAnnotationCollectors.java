package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationCollectors.class */
public abstract class MergedAnnotationCollectors {
    private static final Collector.Characteristics[] NO_CHARACTERISTICS = new Collector.Characteristics[0];
    private static final Collector.Characteristics[] IDENTITY_FINISH_CHARACTERISTICS = {Collector.Characteristics.IDENTITY_FINISH};

    private MergedAnnotationCollectors() {
    }

    public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, Set<A>> toAnnotationSet() {
        return Collector.of(LinkedHashSet::new, (set, annotation) -> {
            set.add(annotation.synthesize());
        }, (v0, v1) -> {
            return combiner(v0, v1);
        }, new Collector.Characteristics[0]);
    }

    public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, Annotation[]> toAnnotationArray() {
        return toAnnotationArray(x$0 -> {
            return new Annotation[x$0];
        });
    }

    public static <R extends Annotation, A extends R> Collector<MergedAnnotation<A>, ?, R[]> toAnnotationArray(IntFunction<R[]> generator) {
        return Collector.of(ArrayList::new, (list, annotation) -> {
            list.add(annotation.synthesize());
        }, (v0, v1) -> {
            return combiner(v0, v1);
        }, list2 -> {
            return (Annotation[]) list2.toArray((Annotation[]) generator.apply(list2.size()));
        }, new Collector.Characteristics[0]);
    }

    public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, MultiValueMap<String, Object>> toMultiValueMap(MergedAnnotation.Adapt... adaptations) {
        return toMultiValueMap(Function.identity(), adaptations);
    }

    public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, MultiValueMap<String, Object>> toMultiValueMap(Function<MultiValueMap<String, Object>, MultiValueMap<String, Object>> finisher, MergedAnnotation.Adapt... adaptations) {
        Collector.Characteristics[] characteristics = isSameInstance(finisher, Function.identity()) ? IDENTITY_FINISH_CHARACTERISTICS : NO_CHARACTERISTICS;
        return Collector.of(LinkedMultiValueMap::new, (map, annotation) -> {
            Map<String, Object> asMap = annotation.asMap(adaptations);
            Objects.requireNonNull(map);
            asMap.forEach((v1, v2) -> {
                r1.add(v1, v2);
            });
        }, MergedAnnotationCollectors::combiner, finisher, characteristics);
    }

    private static boolean isSameInstance(Object instance, Object candidate) {
        return instance == candidate;
    }

    private static <E, C extends Collection<E>> C combiner(C collection, C additions) {
        collection.addAll(additions);
        return collection;
    }

    private static <K, V> MultiValueMap<K, V> combiner(MultiValueMap<K, V> map, MultiValueMap<K, V> additions) {
        map.addAll(additions);
        return map;
    }
}
