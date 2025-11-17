package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/IteratorEnumeration.class */
public class IteratorEnumeration<E> implements Enumeration<E>, Serializable {
    private static final long serialVersionUID = 1;
    private final Iterator<E> iterator;

    public IteratorEnumeration(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override // java.util.Enumeration
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    @Override // java.util.Enumeration
    public E nextElement() {
        return this.iterator.next();
    }
}
