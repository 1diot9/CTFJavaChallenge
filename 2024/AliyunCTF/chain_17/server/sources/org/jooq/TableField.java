package org.jooq;

import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableField.class */
public interface TableField<R extends Record, T> extends Field<T> {
    @Nullable
    Table<R> getTable();
}
