package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderRowsStep.class */
public interface LoaderRowsStep<R extends Record> {
    @Support
    @CheckReturnValue
    @NotNull
    LoaderListenerStep<R> fields(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderListenerStep<R> fields(Collection<? extends Field<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderListenerStep<R> fields(LoaderFieldMapper loaderFieldMapper);

    @Support
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    LoaderCSVOptionsStep<R> fieldsFromSource();

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVOptionsStep<R> fieldsCorresponding();
}
