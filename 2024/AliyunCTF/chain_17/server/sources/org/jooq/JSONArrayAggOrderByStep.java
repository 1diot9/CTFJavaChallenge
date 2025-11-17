package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONArrayAggOrderByStep.class */
public interface JSONArrayAggOrderByStep<J> extends JSONArrayAggNullStep<J> {
    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONArrayAggNullStep<J> orderBy(OrderField<?>... orderFieldArr);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONArrayAggNullStep<J> orderBy(Collection<? extends OrderField<?>> collection);
}
