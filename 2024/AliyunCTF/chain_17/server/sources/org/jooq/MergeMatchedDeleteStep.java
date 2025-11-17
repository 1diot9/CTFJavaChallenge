package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeMatchedDeleteStep.class */
public interface MergeMatchedDeleteStep<R extends Record> extends MergeMatchedStep<R> {
    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedStep<R> deleteWhere(Condition condition);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedStep<R> deleteWhere(Field<Boolean> field);
}
