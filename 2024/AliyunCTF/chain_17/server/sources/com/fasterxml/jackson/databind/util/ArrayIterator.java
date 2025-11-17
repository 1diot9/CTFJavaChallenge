package com.fasterxml.jackson.databind.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayIterator.class */
public class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
    private final T[] _a;
    private int _index = 0;

    public ArrayIterator(T[] a) {
        this._a = a;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this._index < this._a.length;
    }

    @Override // java.util.Iterator
    public T next() {
        if (this._index >= this._a.length) {
            throw new NoSuchElementException();
        }
        T[] tArr = this._a;
        int i = this._index;
        this._index = i + 1;
        return tArr[i];
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override // java.lang.Iterable
    public Iterator<T> iterator() {
        return this;
    }
}
