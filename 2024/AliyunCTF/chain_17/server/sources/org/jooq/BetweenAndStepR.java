package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BetweenAndStepR.class */
public interface BetweenAndStepR<R extends Record> {
    @Support
    @NotNull
    Condition and(R r);
}
