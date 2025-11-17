package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/OrderedAggregateFunctionOfDeferredType.class */
public interface OrderedAggregateFunctionOfDeferredType {
    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    <T> AggregateFilterStep<T> withinGroupOrderBy(OrderField<T> orderField);
}
