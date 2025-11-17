package org.jooq;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InverseForeignKey.class */
public interface InverseForeignKey<PARENT extends Record, CHILD extends Record> extends Key<PARENT> {
    @NotNull
    ForeignKey<CHILD, PARENT> getForeignKey();

    @NotNull
    List<TableField<CHILD, ?>> getForeignKeyFields();

    @NotNull
    TableField<CHILD, ?>[] getForeignKeyFieldsArray();
}
