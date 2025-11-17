package cn.hutool.core.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/NullComparator.class */
public class NullComparator<T> implements Comparator<T>, Serializable {
    private static final long serialVersionUID = 1;
    protected final boolean nullGreater;
    protected final Comparator<T> comparator;

    /* JADX WARN: Multi-variable type inference failed */
    public NullComparator(boolean nullGreater, Comparator<? super T> comparator) {
        this.nullGreater = nullGreater;
        this.comparator = comparator;
    }

    @Override // java.util.Comparator
    public int compare(T a, T b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return this.nullGreater ? 1 : -1;
        }
        if (b == null) {
            return this.nullGreater ? -1 : 1;
        }
        return doCompare(a, b);
    }

    @Override // java.util.Comparator
    public Comparator<T> thenComparing(Comparator<? super T> other) {
        Objects.requireNonNull(other);
        return new NullComparator(this.nullGreater, this.comparator == null ? other : this.comparator.thenComparing(other));
    }

    protected int doCompare(T a, T b) {
        if (null == this.comparator) {
            if ((a instanceof Comparable) && (b instanceof Comparable)) {
                return ((Comparable) a).compareTo(b);
            }
            return 0;
        }
        return this.comparator.compare(a, b);
    }
}
