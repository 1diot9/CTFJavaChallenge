package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CreateTypeStep.class */
public interface CreateTypeStep {
    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeFinalStep asEnum(String... strArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeFinalStep asEnum(Field<String>... fieldArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeFinalStep asEnum(Collection<? extends Field<String>> collection);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeFinalStep asEnum();

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeFinalStep as(Field<?>... fieldArr);

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTypeFinalStep as(Collection<? extends Field<?>> collection);
}
