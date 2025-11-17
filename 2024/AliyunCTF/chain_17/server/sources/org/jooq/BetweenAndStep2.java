package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep2.class */
public interface BetweenAndStep2<T1, T2> {
    @Support
    @NotNull
    Condition and(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition and(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition and(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition and(Record2<T1, T2> record2);
}
