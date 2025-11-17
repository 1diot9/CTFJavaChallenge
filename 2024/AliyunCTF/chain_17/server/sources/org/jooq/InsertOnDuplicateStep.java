package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertOnDuplicateStep.class */
public interface InsertOnDuplicateStep<R extends Record> extends InsertReturningStep<R> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> onConflictOnConstraint(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> onConflictOnConstraint(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnConflictDoUpdateStep<R> onConflictOnConstraint(UniqueKey<R> uniqueKey);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnConflictWhereIndexPredicateStep<R> onConflict(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnConflictWhereIndexPredicateStep<R> onConflict(Collection<? extends Field<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertReturningStep<R> onConflictDoNothing();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateSetStep<R> onDuplicateKeyUpdate();

    @Support
    @CheckReturnValue
    @NotNull
    InsertReturningStep<R> onDuplicateKeyIgnore();
}
