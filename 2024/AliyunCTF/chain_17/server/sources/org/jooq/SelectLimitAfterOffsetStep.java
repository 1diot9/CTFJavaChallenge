package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectLimitAfterOffsetStep.class */
public interface SelectLimitAfterOffsetStep<R extends Record> extends SelectForUpdateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitPercentAfterOffsetStep<R> limit(Number number);

    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitPercentAfterOffsetStep<R> limit(Field<? extends Number> field);
}
