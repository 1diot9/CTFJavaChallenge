package cn.hutool.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/ComputeIter.class */
public abstract class ComputeIter<T> implements Iterator<T> {
    private T next;
    private boolean finished;

    protected abstract T computeNext();

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (null != this.next) {
            return true;
        }
        if (this.finished) {
            return false;
        }
        T result = computeNext();
        if (null == result) {
            this.finished = true;
            return false;
        }
        this.next = result;
        return true;
    }

    @Override // java.util.Iterator
    public T next() {
        if (false == hasNext()) {
            throw new NoSuchElementException("No more lines");
        }
        T result = this.next;
        this.next = null;
        return result;
    }

    public void finish() {
        this.finished = true;
        this.next = null;
    }
}
