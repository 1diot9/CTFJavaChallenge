package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeMatchedStep.class */
public interface MergeMatchedStep<R extends Record> extends MergeNotMatchedStep<R> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedSetStep<R> whenMatchedThenUpdate();

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedStep<R> whenMatchedThenDelete();

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedThenStep<R> whenMatchedAnd(Condition condition);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedThenStep<R> whenMatchedAnd(Field<Boolean> field);

    @PlainSQL
    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedThenStep<R> whenMatchedAnd(SQL sql);

    @PlainSQL
    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedThenStep<R> whenMatchedAnd(String str);

    @PlainSQL
    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedThenStep<R> whenMatchedAnd(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeMatchedThenStep<R> whenMatchedAnd(String str, QueryPart... queryPartArr);
}
