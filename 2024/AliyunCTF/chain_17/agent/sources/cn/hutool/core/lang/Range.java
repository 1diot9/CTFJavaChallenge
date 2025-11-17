package cn.hutool.core.lang;

import cn.hutool.core.thread.lock.NoLock;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Range.class */
public class Range<T> implements Iterable<T>, Iterator<T>, Serializable {
    private static final long serialVersionUID = 1;
    private Lock lock;
    private final T start;
    private final T end;
    private T next;
    private final Stepper<T> stepper;
    private int index;
    private final boolean includeStart;
    private final boolean includeEnd;

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Range$Stepper.class */
    public interface Stepper<T> {
        T step(T t, T t2, int i);
    }

    public Range(T start, Stepper<T> stepper) {
        this(start, null, stepper);
    }

    public Range(T start, T end, Stepper<T> stepper) {
        this(start, end, stepper, true, true);
    }

    public Range(T start, T end, Stepper<T> stepper, boolean isIncludeStart, boolean isIncludeEnd) {
        this.lock = new ReentrantLock();
        this.index = 0;
        Assert.notNull(start, "First element must be not null!", new Object[0]);
        this.start = start;
        this.end = end;
        this.stepper = stepper;
        this.next = safeStep(this.start);
        this.includeStart = isIncludeStart;
        this.includeEnd = isIncludeEnd;
    }

    public Range<T> disableLock() {
        this.lock = new NoLock();
        return this;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        this.lock.lock();
        try {
            if (0 == this.index && this.includeStart) {
                return true;
            }
            if (null == this.next) {
                return false;
            }
            if (false == this.includeEnd) {
                if (this.next.equals(this.end)) {
                    return false;
                }
            }
            return true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Iterator
    public T next() {
        this.lock.lock();
        try {
            if (false == hasNext()) {
                throw new NoSuchElementException("Has no next range!");
            }
            return nextUncheck();
        } finally {
            this.lock.unlock();
        }
    }

    private T nextUncheck() {
        T current;
        if (0 == this.index) {
            current = this.start;
            if (false == this.includeStart) {
                this.index++;
                return nextUncheck();
            }
        } else {
            current = this.next;
            this.next = safeStep(this.next);
        }
        this.index++;
        return current;
    }

    private T safeStep(T base) {
        int index = this.index;
        T next = null;
        try {
            next = this.stepper.step(base, this.end, index);
        } catch (Exception e) {
        }
        return next;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Can not remove ranged element!");
    }

    @Override // java.lang.Iterable
    public Iterator<T> iterator() {
        return this;
    }

    public Range<T> reset() {
        this.lock.lock();
        try {
            this.index = 0;
            this.next = safeStep(this.start);
            return this;
        } finally {
            this.lock.unlock();
        }
    }
}
