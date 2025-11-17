package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CreateIndexWhereStep.class */
public interface CreateIndexWhereStep extends CreateIndexFinalStep {
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(Field<Boolean> field);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(Condition... conditionArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(Collection<? extends Condition> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(Condition condition);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(String str);

    @PlainSQL
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep where(SQL sql);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateIndexFinalStep excludeNullKeys();
}
