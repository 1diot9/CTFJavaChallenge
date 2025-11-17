package cn.hutool.core.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/ArrayIter.class */
public class ArrayIter<E> implements IterableIter<E>, ResettableIter<E>, Serializable {
    private static final long serialVersionUID = 1;
    private final Object array;
    private int startIndex;
    private int endIndex;
    private int index;

    public ArrayIter(E[] array) {
        this((Object) array);
    }

    public ArrayIter(Object array) {
        this(array, 0);
    }

    public ArrayIter(Object array, int startIndex) {
        this(array, startIndex, -1);
    }

    public ArrayIter(Object array, int startIndex, int endIndex) {
        this.endIndex = Array.getLength(array);
        if (endIndex > 0 && endIndex < this.endIndex) {
            this.endIndex = endIndex;
        }
        if (startIndex >= 0 && startIndex < this.endIndex) {
            this.startIndex = startIndex;
        }
        this.array = array;
        this.index = this.startIndex;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.index < this.endIndex;
    }

    @Override // java.util.Iterator
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Object obj = this.array;
        int i = this.index;
        this.index = i + 1;
        return (E) Array.get(obj, i);
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported");
    }

    public Object getArray() {
        return this.array;
    }

    @Override // cn.hutool.core.collection.ResettableIter
    public void reset() {
        this.index = this.startIndex;
    }
}
