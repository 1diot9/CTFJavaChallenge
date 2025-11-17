package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStep.class */
public interface BetweenAndStep<T> {
    @Support
    @NotNull
    Condition and(T t);

    @Support
    @NotNull
    Condition and(Field<T> field);
}
