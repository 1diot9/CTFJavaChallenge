package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TruncateIdentityStep.class */
public interface TruncateIdentityStep<R extends Record> extends TruncateCascadeStep<R> {
    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    TruncateCascadeStep<R> restartIdentity();

    @Support({SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    TruncateCascadeStep<R> continueIdentity();
}
