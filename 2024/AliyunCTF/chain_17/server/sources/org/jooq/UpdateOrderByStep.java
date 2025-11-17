package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UpdateOrderByStep.class */
public interface UpdateOrderByStep<R extends Record> extends UpdateLimitStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    UpdateLimitStep<R> orderBy(OrderField<?>... orderFieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateLimitStep<R> orderBy(Collection<? extends OrderField<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateLimitStep<R> orderBy(int... iArr);
}
