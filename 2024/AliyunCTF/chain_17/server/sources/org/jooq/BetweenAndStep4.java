package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep4.class */
public interface BetweenAndStep4<T1, T2, T3, T4> {
    @Support
    @NotNull
    Condition and(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition and(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition and(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition and(Record4<T1, T2, T3, T4> record4);
}
