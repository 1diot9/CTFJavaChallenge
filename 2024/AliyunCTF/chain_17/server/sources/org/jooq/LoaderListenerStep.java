package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderListenerStep.class */
public interface LoaderListenerStep<R extends Record> extends LoaderLoadStep<R> {
    @Support
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    LoaderLoadStep<R> onRow(LoaderRowListener loaderRowListener);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderLoadStep<R> onRowStart(LoaderRowListener loaderRowListener);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderLoadStep<R> onRowEnd(LoaderRowListener loaderRowListener);
}
