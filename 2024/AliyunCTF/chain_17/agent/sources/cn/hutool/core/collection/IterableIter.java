package cn.hutool.core.collection;

import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/IterableIter.class */
public interface IterableIter<T> extends Iterable<T>, Iterator<T> {
    @Override // java.lang.Iterable
    default Iterator<T> iterator() {
        return this;
    }
}
