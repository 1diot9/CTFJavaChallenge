package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordQualifier.class */
public interface RecordQualifier<R extends Record> extends Qualified, Fields {
    @Nullable
    Package getPackage();

    @NotNull
    Class<? extends R> getRecordType();

    @NotNull
    DataType<R> getDataType();

    @NotNull
    R newRecord();
}
