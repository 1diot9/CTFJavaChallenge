package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ArrayAggOrderByStep.class */
public interface ArrayAggOrderByStep<T> extends AggregateFilterStep<T> {
    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    AggregateFilterStep<T> orderBy(OrderField<?>... orderFieldArr);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    AggregateFilterStep<T> orderBy(Collection<? extends OrderField<?>> collection);
}
