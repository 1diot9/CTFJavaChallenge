package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/CopiedIter.class */
public class CopiedIter<E> implements IterableIter<E>, Serializable {
    private static final long serialVersionUID = 1;
    private final Iterator<E> listIterator;

    public static <E> CopiedIter<E> copyOf(Iterator<E> iterator) {
        return new CopiedIter<>(iterator);
    }

    public CopiedIter(Iterator<E> iterator) {
        List<E> eleList = ListUtil.toList(iterator);
        this.listIterator = eleList.iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.listIterator.hasNext();
    }

    @Override // java.util.Iterator
    public E next() {
        return this.listIterator.next();
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This is a read-only iterator.");
    }
}
