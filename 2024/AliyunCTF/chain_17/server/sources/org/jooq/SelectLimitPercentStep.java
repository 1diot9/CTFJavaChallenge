package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectLimitPercentStep.class */
public interface SelectLimitPercentStep<R extends Record> extends SelectWithTiesStep<R> {
    @Support({SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    SelectWithTiesStep<R> percent();
}
