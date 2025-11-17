package org.jooq;

import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RenamedTable.class */
public final class RenamedTable<R extends Record> extends TableImpl<R> implements RenamedSchemaElement {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RenamedTable(Schema schema, Table<R> delegate, String rename) {
        super(DSL.name(rename), schema);
        for (Field<?> field : delegate.fields()) {
            createField(field.getUnqualifiedName(), field.getDataType(), this);
        }
    }
}
