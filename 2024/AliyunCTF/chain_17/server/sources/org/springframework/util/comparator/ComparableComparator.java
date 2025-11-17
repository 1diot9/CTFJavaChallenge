package org.springframework.util.comparator;

import java.lang.Comparable;
import java.util.Comparator;

@Deprecated(since = "6.1")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/comparator/ComparableComparator.class */
public class ComparableComparator<T extends Comparable<T>> implements Comparator<T> {
    public static final ComparableComparator INSTANCE = new ComparableComparator();

    @Override // java.util.Comparator
    public int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }
}
