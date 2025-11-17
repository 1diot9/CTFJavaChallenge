package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeNotMatchedWhereStep.class */
public interface MergeNotMatchedWhereStep<R extends Record> extends MergeFinalStep<R> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    MergeFinalStep<R> where(Condition condition);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2})
    @CheckReturnValue
    @NotNull
    MergeFinalStep<R> where(Field<Boolean> field);
}
