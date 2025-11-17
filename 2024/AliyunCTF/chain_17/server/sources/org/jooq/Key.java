package org.jooq;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Key.class */
public interface Key<R extends Record> extends Named {
    @NotNull
    Table<R> getTable();

    @NotNull
    List<TableField<R, ?>> getFields();

    @NotNull
    TableField<R, ?>[] getFieldsArray();

    @NotNull
    Constraint constraint();

    boolean enforced();

    boolean nullable();
}
