package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderJSONStep.class */
public interface LoaderJSONStep<R extends Record> {
    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONOptionsStep<R> fields(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONOptionsStep<R> fields(Collection<? extends Field<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONOptionsStep<R> fields(LoaderFieldMapper loaderFieldMapper);

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
