package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/FilterIter.class */
public class FilterIter<E> implements Iterator<E> {
    private final Iterator<? extends E> iterator;
    private final Filter<? super E> filter;
    private E nextObject;
    private boolean nextObjectSet = false;

    public FilterIter(Iterator<? extends E> iterator, Filter<? super E> filter) {
        this.iterator = (Iterator) Assert.notNull(iterator);
        this.filter = filter;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.nextObjectSet || setNextObject();
    }

    @Override // java.util.Iterator
    public E next() {
        if (false == this.nextObjectSet && false == setNextObject()) {
            throw new NoSuchElementException();
        }
        this.nextObjectSet = false;
        return this.nextObject;
    }

    @Override // java.util.Iterator
    public void remove() {
        if (this.nextObjectSet) {
            throw new IllegalStateException("remove() cannot be called");
        }
        this.iterator.remove();
    }

    public Iterator<? extends E> getIterator() {
        return this.iterator;
    }

    public Filter<? super E> getFilter() {
        return this.filter;
    }

    private boolean setNextObject() {
        while (this.iterator.hasNext()) {
            E object = this.iterator.next();
            if (null == this.filter || this.filter.accept(object)) {
                this.nextObject = object;
                this.nextObjectSet = true;
                return true;
            }
        }
        return false;
    }
}
