package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AggregateFunction.class */
public interface AggregateFunction<T> extends AggregateFilterStep<T> {
    @ApiStatus.Experimental
    @Nullable
    Condition $filterWhere();
}
