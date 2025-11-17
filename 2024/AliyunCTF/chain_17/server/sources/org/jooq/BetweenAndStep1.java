package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep1.class */
public interface BetweenAndStep1<T1> {
    @Support
    @NotNull
    Condition and(Field<T1> field);

    @Support
    @NotNull
    Condition and(T1 t1);

    @Support
    @NotNull
    Condition and(Row1<T1> row1);

    @Support
    @NotNull
    Condition and(Record1<T1> record1);
}
