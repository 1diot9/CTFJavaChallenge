package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectOptionStep.class */
public interface SelectOptionStep<R extends Record> extends SelectUnionStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectUnionStep<R> option(String str);
}
