package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectForUpdateOfStep.class */
public interface SelectForUpdateOfStep<R extends Record> extends SelectForUpdateWaitStep<R> {
    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateWaitStep<R> of(Field<?>... fieldArr);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateWaitStep<R> of(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateWaitStep<R> of(Table<?>... tableArr);
}
