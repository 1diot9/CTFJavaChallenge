package org.jooq;

import java.io.IOException;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderLoadStep.class */
public interface LoaderLoadStep<R extends Record> {
    @Blocking
    @Support
    @NotNull
    Loader<R> execute() throws IOException;
}
