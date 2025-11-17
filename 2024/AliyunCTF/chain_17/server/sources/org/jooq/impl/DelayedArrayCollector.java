package org.jooq.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.jooq.Fields;
import org.jooq.Record;
import org.jooq.Records;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DelayedArrayCollector.class */
final class DelayedArrayCollector<R extends Record, E> implements Collector<R, List<E>, E[]> {
    private final java.util.function.Function<Fields, E[]> array;
    private final Collector<R, List<E>, List<E>> delegate;
    Fields fields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DelayedArrayCollector(java.util.function.Function<Fields, E[]> array, java.util.function.Function<R, E> mapper) {
        this.array = array;
        this.delegate = Records.intoList(mapper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <F extends Fields> F patch(Collector<?, ?, ?> collector, F fields) {
        if (collector instanceof DelayedArrayCollector) {
            DelayedArrayCollector<?, ?> d = (DelayedArrayCollector) collector;
            d.fields = fields;
        }
        return fields;
    }

    @Override // java.util.stream.Collector
    public final Supplier<List<E>> supplier() {
        return this.delegate.supplier();
    }

    @Override // java.util.stream.Collector
    public final BiConsumer<List<E>, R> accumulator() {
        return this.delegate.accumulator();
    }

    @Override // java.util.stream.Collector
    public final BinaryOperator<List<E>> combiner() {
        return this.delegate.combiner();
    }

    @Override // java.util.stream.Collector
    public final java.util.function.Function<List<E>, E[]> finisher() {
        return (java.util.function.Function<List<E>, E[]>) this.delegate.finisher().andThen(l -> {
            return l.toArray(this.array.apply(this.fields));
        });
    }

    @Override // java.util.stream.Collector
    public final Set<Collector.Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
