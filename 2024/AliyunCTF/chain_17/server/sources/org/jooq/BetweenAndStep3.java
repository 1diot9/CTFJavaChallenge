package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep3.class */
public interface BetweenAndStep3<T1, T2, T3> {
    @Support
    @NotNull
    Condition and(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition and(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition and(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition and(Record3<T1, T2, T3> record3);
}
