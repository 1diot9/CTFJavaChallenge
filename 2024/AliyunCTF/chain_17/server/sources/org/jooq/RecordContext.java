package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordContext.class */
public interface RecordContext extends Scope {
    @NotNull
    ExecuteType type();

    @NotNull
    Record record();

    @NotNull
    RecordType<?> recordType();

    @NotNull
    Record[] batchRecords();

    @Nullable
    Exception exception();
}
