package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeKeyStepN.class */
public interface MergeKeyStepN<R extends Record> extends MergeValuesStepN<R> {
    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    MergeValuesStepN<R> key(Field<?>... fieldArr);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    MergeValuesStepN<R> key(Collection<? extends Field<?>> collection);
}
