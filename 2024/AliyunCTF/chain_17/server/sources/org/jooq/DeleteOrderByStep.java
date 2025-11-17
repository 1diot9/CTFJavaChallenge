package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DeleteOrderByStep.class */
public interface DeleteOrderByStep<R extends Record> extends DeleteLimitStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    DeleteLimitStep<R> orderBy(OrderField<?>... orderFieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteLimitStep<R> orderBy(Collection<? extends OrderField<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteLimitStep<R> orderBy(int... iArr);
}
