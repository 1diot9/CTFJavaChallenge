package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/TransCollection.class */
public class TransCollection<F, T> extends AbstractCollection<T> {
    private final Collection<F> fromCollection;
    private final Function<? super F, ? extends T> function;

    public TransCollection(Collection<F> fromCollection, Function<? super F, ? extends T> function) {
        this.fromCollection = (Collection) Assert.notNull(fromCollection);
        this.function = (Function) Assert.notNull(function);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        return IterUtil.trans(this.fromCollection.iterator(), this.function);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        this.fromCollection.clear();
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return this.fromCollection.isEmpty();
    }

    @Override // java.lang.Iterable
    public void forEach(Consumer<? super T> action) {
        Assert.notNull(action);
        this.fromCollection.forEach(f -> {
            action.accept(this.function.apply(f));
        });
    }

    @Override // java.util.Collection
    public boolean removeIf(Predicate<? super T> filter) {
        Assert.notNull(filter);
        return this.fromCollection.removeIf(element -> {
            return filter.test(this.function.apply(element));
        });
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Spliterator<T> spliterator() {
        return SpliteratorUtil.trans(this.fromCollection.spliterator(), this.function);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public int size() {
        return this.fromCollection.size();
    }
}
