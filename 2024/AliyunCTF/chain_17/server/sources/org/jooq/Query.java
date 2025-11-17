package org.jooq;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Query.class */
public interface Query extends Statement, AttachableQueryPart {
    @Blocking
    int execute() throws DataAccessException;

    @NotNull
    CompletionStage<Integer> executeAsync();

    @NotNull
    CompletionStage<Integer> executeAsync(Executor executor);

    boolean isExecutable();

    @NotNull
    Query bind(String str, Object obj) throws IllegalArgumentException, DataTypeException;

    @NotNull
    Query bind(int i, Object obj) throws IllegalArgumentException, DataTypeException;

    @NotNull
    Query poolable(boolean z);

    @NotNull
    Query queryTimeout(int i);

    @NotNull
    CloseableQuery keepStatement(boolean z);

    void cancel() throws DataAccessException;
}
