package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.Iterator;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/TransIter.class */
public class TransIter<F, T> implements Iterator<T> {
    private final Iterator<? extends F> backingIterator;
    private final Function<? super F, ? extends T> func;

    public TransIter(Iterator<? extends F> backingIterator, Function<? super F, ? extends T> func) {
        this.backingIterator = (Iterator) Assert.notNull(backingIterator);
        this.func = (Function) Assert.notNull(func);
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.backingIterator.hasNext();
    }

    @Override // java.util.Iterator
    public final T next() {
        return this.func.apply(this.backingIterator.next());
    }

    @Override // java.util.Iterator
    public final void remove() {
        this.backingIterator.remove();
    }
}
