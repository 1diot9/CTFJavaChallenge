package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectOffsetStep.class */
public interface SelectOffsetStep<R extends Record> extends SelectForUpdateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectForUpdateStep<R> offset(Number number);

    @Support
    @CheckReturnValue
    @NotNull
    SelectForUpdateStep<R> offset(Field<? extends Number> field);
}
