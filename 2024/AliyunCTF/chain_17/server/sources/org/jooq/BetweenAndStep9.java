package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep9.class */
public interface BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    @Support
    @NotNull
    Condition and(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition and(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition and(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);
}
