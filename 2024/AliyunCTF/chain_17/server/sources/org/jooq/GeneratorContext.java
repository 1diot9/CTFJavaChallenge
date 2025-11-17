package org.jooq;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/GeneratorContext.class */
public interface GeneratorContext<R extends Record, X extends Table<R>, T> extends Scope {
    @NotNull
    Instant renderTime();

    @Nullable
    X table();

    @Nullable
    Field<T> field();

    @Nullable
    GeneratorStatementType statementType();
}
