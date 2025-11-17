package org.jooq;

import java.io.Serializable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Batch.class */
public interface Batch extends Serializable, Publisher<Integer> {
    @Blocking
    int[] execute() throws DataAccessException;

    @NotNull
    CompletionStage<int[]> executeAsync();

    @NotNull
    CompletionStage<int[]> executeAsync(Executor executor);

    int size();
}
