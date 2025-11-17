package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectLimitPercentAfterOffsetStep.class */
public interface SelectLimitPercentAfterOffsetStep<R extends Record> extends SelectWithTiesAfterOffsetStep<R> {
    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    SelectWithTiesAfterOffsetStep<R> percent();
}
