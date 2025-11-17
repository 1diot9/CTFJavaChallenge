package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep5.class */
public interface BetweenAndStep5<T1, T2, T3, T4, T5> {
    @Support
    @NotNull
    Condition and(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition and(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition and(Record5<T1, T2, T3, T4, T5> record5);
}
