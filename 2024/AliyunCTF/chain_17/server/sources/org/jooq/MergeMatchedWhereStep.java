package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeMatchedWhereStep.class */
public interface MergeMatchedWhereStep<R extends Record> extends MergeMatchedDeleteStep<R> {
    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedDeleteStep<R> where(Condition condition);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedDeleteStep<R> where(Field<Boolean> field);
}
