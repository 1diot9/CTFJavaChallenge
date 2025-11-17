package cn.hutool.core.collection;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/TransSpliterator.class */
public class TransSpliterator<F, T> implements Spliterator<T> {
    private final Spliterator<F> fromSpliterator;
    private final Function<? super F, ? extends T> function;

    public TransSpliterator(Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
        this.fromSpliterator = fromSpliterator;
        this.function = function;
    }

    @Override // java.util.Spliterator
    public boolean tryAdvance(Consumer<? super T> action) {
        return this.fromSpliterator.tryAdvance(fromElement -> {
            action.accept(this.function.apply(fromElement));
        });
    }

    @Override // java.util.Spliterator
    public void forEachRemaining(Consumer<? super T> action) {
        this.fromSpliterator.forEachRemaining(fromElement -> {
            action.accept(this.function.apply(fromElement));
        });
    }

    @Override // java.util.Spliterator
    public Spliterator<T> trySplit() {
        Spliterator<F> fromSplit = this.fromSpliterator.trySplit();
        if (fromSplit != null) {
            return new TransSpliterator(fromSplit, this.function);
        }
        return null;
    }

    @Override // java.util.Spliterator
    public long estimateSize() {
        return this.fromSpliterator.estimateSize();
    }

    @Override // java.util.Spliterator
    public int characteristics() {
        return this.fromSpliterator.characteristics() & (-262);
    }
}
