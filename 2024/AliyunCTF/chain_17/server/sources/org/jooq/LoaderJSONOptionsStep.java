package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderJSONOptionsStep.class */
public interface LoaderJSONOptionsStep<R extends Record> extends LoaderListenerStep<R> {
    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    LoaderJSONOptionsStep<R> ignoreRows(int i);
}
