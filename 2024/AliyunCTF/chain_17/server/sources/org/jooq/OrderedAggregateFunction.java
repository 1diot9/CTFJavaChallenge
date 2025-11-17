package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/OrderedAggregateFunction.class */
public interface OrderedAggregateFunction<T> {
    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    AggregateFilterStep<T> withinGroupOrderBy(OrderField<?>... orderFieldArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    AggregateFilterStep<T> withinGroupOrderBy(Collection<? extends OrderField<?>> collection);
}
