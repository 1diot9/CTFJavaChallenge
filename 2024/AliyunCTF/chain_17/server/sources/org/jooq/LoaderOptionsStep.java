package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderOptionsStep.class */
public interface LoaderOptionsStep<R extends Record> extends LoaderSourceStep<R> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> onDuplicateKeyUpdate();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> onDuplicateKeyIgnore();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> onDuplicateKeyError();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> onErrorIgnore();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> onErrorAbort();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> commitEach();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> commitAfter(int i);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> commitAll();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> commitNone();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> batchAll();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> batchNone();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> batchAfter(int i);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> bulkAll();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> bulkNone();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderOptionsStep<R> bulkAfter(int i);
}
