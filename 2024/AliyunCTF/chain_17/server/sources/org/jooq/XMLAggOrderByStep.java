package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLAggOrderByStep.class */
public interface XMLAggOrderByStep<T> extends AggregateFilterStep<T> {
    @Support({SQLDialect.POSTGRES})
    @NotNull
    AggregateFilterStep<T> orderBy(OrderField<?>... orderFieldArr);

    @Support({SQLDialect.POSTGRES})
    @NotNull
    AggregateFilterStep<T> orderBy(Collection<? extends OrderField<?>> collection);
}
