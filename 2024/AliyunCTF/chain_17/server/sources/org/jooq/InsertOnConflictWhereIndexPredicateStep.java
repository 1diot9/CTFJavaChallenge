package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertOnConflictWhereIndexPredicateStep.class */
public interface InsertOnConflictWhereIndexPredicateStep<R extends Record> extends InsertOnConflictDoUpdateStep<R> {
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(Condition condition);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(Condition... conditionArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(Collection<? extends Condition> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(Field<Boolean> field);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(SQL sql);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(String str);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> where(String str, QueryPart... queryPartArr);
}
