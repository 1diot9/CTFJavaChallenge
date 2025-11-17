package cn.hutool.core.stream;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/stream/SimpleCollector.class */
public class SimpleCollector<T, A, R> implements Collector<T, A, R> {
    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Function<A, R> finisher;
    private final Set<Collector.Characteristics> characteristics;

    public SimpleCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher, Set<Collector.Characteristics> characteristics) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
        this.characteristics = characteristics;
    }

    public SimpleCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Set<Collector.Characteristics> characteristics) {
        this(supplier, accumulator, combiner, i -> {
            return i;
        }, characteristics);
    }

    @Override // java.util.stream.Collector
    public BiConsumer<A, T> accumulator() {
        return this.accumulator;
    }

    @Override // java.util.stream.Collector
    public Supplier<A> supplier() {
        return this.supplier;
    }

    @Override // java.util.stream.Collector
    public BinaryOperator<A> combiner() {
        return this.combiner;
    }

    @Override // java.util.stream.Collector
    public Function<A, R> finisher() {
        return this.finisher;
    }

    @Override // java.util.stream.Collector
    public Set<Collector.Characteristics> characteristics() {
        return this.characteristics;
    }
}
