package org.jooq.impl;

import java.util.Collection;
import org.jooq.Field;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordImplN.class */
public final class RecordImplN extends AbstractRecord implements InternalRecord {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordImplN(AbstractRow<?> row) {
        super(row);
    }

    @Deprecated(forRemoval = true, since = "3.14")
    RecordImplN(Collection<? extends Field<?>> fields) {
        super(fields);
    }

    RecordImplN(RowImplN fields) {
        super(fields);
    }

    @Override // org.jooq.Fields
    public RowImplN fieldsRow() {
        return new RowImplN(this.fields.fields);
    }

    @Override // org.jooq.Record
    public final RowImplN valuesRow() {
        return new RowImplN(Tools.fieldsArray(this.values, this.fields.fields.fields));
    }
}
