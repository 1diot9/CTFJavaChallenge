package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectGroupByStep.class */
public interface SelectGroupByStep<R extends Record> extends SelectHavingStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingStep<R> groupBy(GroupField... groupFieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingStep<R> groupBy(Collection<? extends GroupField> collection);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    SelectHavingStep<R> groupByDistinct(GroupField... groupFieldArr);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    SelectHavingStep<R> groupByDistinct(Collection<? extends GroupField> collection);
}
