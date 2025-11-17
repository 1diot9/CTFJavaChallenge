package cn.hutool.core.comparator;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.Comparator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/ComparableComparator.class */
public class ComparableComparator<E extends Comparable<? super E>> implements Comparator<E>, Serializable {
    private static final long serialVersionUID = 3020871676147289162L;
    public static final ComparableComparator INSTANCE = new ComparableComparator();

    @Override // java.util.Comparator
    public int compare(E obj1, E obj2) {
        return obj1.compareTo(obj2);
    }

    public int hashCode() {
        return "ComparableComparator".hashCode();
    }

    @Override // java.util.Comparator
    public boolean equals(Object object) {
        return this == object || (null != object && object.getClass().equals(getClass()));
    }
}
