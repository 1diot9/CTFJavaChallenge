package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStepN.class */
public interface BetweenAndStepN {
    @Support
    @NotNull
    Condition and(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition and(Object... objArr);

    @Support
    @NotNull
    Condition and(RowN rowN);

    @Support
    @NotNull
    Condition and(Record record);
}
