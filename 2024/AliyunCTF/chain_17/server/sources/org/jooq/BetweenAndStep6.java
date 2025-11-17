package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep6.class */
public interface BetweenAndStep6<T1, T2, T3, T4, T5, T6> {
    @Support
    @NotNull
    Condition and(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition and(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition and(Record6<T1, T2, T3, T4, T5, T6> record6);
}
