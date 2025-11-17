package org.springframework.util.comparator;

import java.util.Comparator;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/comparator/Comparators.class */
public abstract class Comparators {
    public static <T> Comparator<T> comparable() {
        return Comparator.naturalOrder();
    }

    public static <T> Comparator<T> nullsLow() {
        return nullsLow(comparable());
    }

    public static <T> Comparator<T> nullsLow(Comparator<T> comparator) {
        return Comparator.nullsFirst(comparator);
    }

    public static <T> Comparator<T> nullsHigh() {
        return nullsHigh(comparable());
    }

    public static <T> Comparator<T> nullsHigh(Comparator<T> comparator) {
        return Comparator.nullsLast(comparator);
    }
}
