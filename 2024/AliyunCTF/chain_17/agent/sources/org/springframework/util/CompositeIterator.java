package org.springframework.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/CompositeIterator.class */
public class CompositeIterator<E> implements Iterator<E> {
    private final Set<Iterator<E>> iterators = new LinkedHashSet();
    private boolean inUse = false;

    public void add(Iterator<E> iterator) {
        Assert.state(!this.inUse, "You can no longer add iterators to a composite iterator that's already in use");
        if (this.iterators.contains(iterator)) {
            throw new IllegalArgumentException("You cannot add the same iterator twice");
        }
        this.iterators.add(iterator);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        this.inUse = true;
        for (Iterator<E> iterator : this.iterators) {
            if (iterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Iterator
    public E next() {
        this.inUse = true;
        for (Iterator<E> iterator : this.iterators) {
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        throw new NoSuchElementException("All iterators exhausted");
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("CompositeIterator does not support remove()");
    }
}
