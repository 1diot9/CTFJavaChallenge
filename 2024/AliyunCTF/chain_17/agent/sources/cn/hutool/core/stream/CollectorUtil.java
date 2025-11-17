package cn.hutool.core.stream;

import cn.hutool.core.lang.Opt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/stream/CollectorUtil.class */
public class CollectorUtil {
    public static final Set<Collector.Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    public static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    public static <T> Collector<T, ?, String> joining(CharSequence delimiter) {
        return joining(delimiter, (v0) -> {
            return v0.toString();
        });
    }

    public static <T> Collector<T, ?, String> joining(CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunc) {
        return joining(delimiter, "", "", toStringFunc);
    }

    public static <T> Collector<T, ?, String> joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix, Function<T, ? extends CharSequence> toStringFunc) {
        return new SimpleCollector(() -> {
            return new StringJoiner(delimiter, prefix, suffix);
        }, (joiner, ele) -> {
            joiner.add((CharSequence) toStringFunc.apply(ele));
        }, (v0, v1) -> {
            return v0.merge(v1);
        }, (v0) -> {
            return v0.toString();
        }, Collections.emptySet());
    }

    public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier, Supplier<M> mapFactory, Collector<? super T, A, D> downstream) {
        Supplier<A> downstreamSupplier = downstream.supplier();
        BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        BiConsumer<Map<K, A>, T> accumulator = (m, t) -> {
            downstreamAccumulator.accept(m.computeIfAbsent(Opt.ofNullable(t).map(classifier).orElse(null), k -> {
                return downstreamSupplier.get();
            }), t);
        };
        BinaryOperator<Map<K, A>> merger = mapMerger(downstream.combiner());
        if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new SimpleCollector(mapFactory, accumulator, merger, CH_ID);
        }
        Function<A, D> finisher = downstream.finisher();
        Function<Map<K, A>, M> finisher2 = intermediate -> {
            intermediate.replaceAll((k, v) -> {
                return finisher.apply(v);
            });
            return intermediate;
        };
        return new SimpleCollector(mapFactory, accumulator, merger, finisher2, CH_NOID);
    }

    public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
        return groupingBy(classifier, HashMap::new, downstream);
    }

    public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {
        return groupingBy(classifier, Collectors.toList());
    }

    public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {
        return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
    }

    public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction, Supplier<M> mapSupplier) {
        BiConsumer<M, T> accumulator = (map, element) -> {
            map.put(Opt.ofNullable(element).map(keyMapper).get(), Opt.ofNullable(element).map(valueMapper).get());
        };
        return new SimpleCollector(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
    }

    public static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry entry : m2.entrySet()) {
                m1.merge(entry.getKey(), entry.getValue(), mergeFunction);
            }
            return m1;
        };
    }

    public static <K, V> Collector<Map<K, V>, ?, Map<K, List<V>>> reduceListMap() {
        return reduceListMap(HashMap::new);
    }

    public static <K, V, R extends Map<K, List<V>>> Collector<Map<K, V>, ?, R> reduceListMap(Supplier<R> mapSupplier) {
        return Collectors.reducing(mapSupplier.get(), value -> {
            Map map = (Map) mapSupplier.get();
            value.forEach((k, v) -> {
                ((List) map.computeIfAbsent(k, i -> {
                    return new ArrayList();
                })).add(v);
            });
            return map;
        }, (l, r) -> {
            r.forEach((k, v) -> {
                ((List) l.computeIfAbsent(k, i -> {
                    return new ArrayList();
                })).addAll(v);
            });
            return l;
        });
    }

    public static <T, K, R, C extends Collection<R>, M extends Map<K, C>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier, Function<? super T, ? extends R> valueMapper, Supplier<C> valueCollFactory, Supplier<M> mapFactory) {
        return groupingBy(classifier, mapFactory, Collectors.mapping(valueMapper, Collectors.toCollection(valueCollFactory)));
    }

    public static <T, K, R, C extends Collection<R>> Collector<T, ?, Map<K, C>> groupingBy(Function<? super T, ? extends K> classifier, Function<? super T, ? extends R> valueMapper, Supplier<C> valueCollFactory) {
        return groupingBy(classifier, valueMapper, valueCollFactory, HashMap::new);
    }

    public static <T, K, R> Collector<T, ?, Map<K, List<R>>> groupingBy(Function<? super T, ? extends K> classifier, Function<? super T, ? extends R> valueMapper) {
        return groupingBy(classifier, valueMapper, ArrayList::new, HashMap::new);
    }
}
