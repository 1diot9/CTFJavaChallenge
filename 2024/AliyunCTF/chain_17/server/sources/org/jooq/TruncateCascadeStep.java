package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TruncateCascadeStep.class */
public interface TruncateCascadeStep<R extends Record> extends TruncateFinalStep<R> {
    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    TruncateFinalStep<R> cascade();

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    TruncateFinalStep<R> restrict();
}
