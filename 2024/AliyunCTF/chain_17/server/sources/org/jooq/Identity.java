package org.jooq;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Identity.class */
public interface Identity<R extends Record, T> extends Serializable {
    @NotNull
    Table<R> getTable();

    @NotNull
    TableField<R, T> getField();
}
