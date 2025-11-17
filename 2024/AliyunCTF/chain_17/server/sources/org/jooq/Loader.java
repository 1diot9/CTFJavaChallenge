package org.jooq;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Loader.class */
public interface Loader<R extends Record> {
    @CheckReturnValue
    @NotNull
    List<LoaderError> errors();

    int processed();

    int executed();

    int ignored();

    int stored();

    @CheckReturnValue
    @NotNull
    LoaderContext result();
}
