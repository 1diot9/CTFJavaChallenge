package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectSelectStep.class */
public interface SelectSelectStep<R extends Record> extends SelectDistinctOnStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> select(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSelectStep<Record> select(Collection<? extends SelectFieldOrAsterisk> collection);
}
