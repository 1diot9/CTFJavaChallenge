package org.jooq;

import org.jooq.UDTRecord;
import org.jooq.impl.UDTImpl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RenamedUDT.class */
public final class RenamedUDT<R extends UDTRecord<R>> extends UDTImpl<R> implements RenamedSchemaElement {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RenamedUDT(Schema schema, UDT<R> delegate, String rename) {
        super(rename, schema, delegate.getPackage(), delegate.isSynthetic());
        for (Field<?> field : delegate.fields()) {
            createField(field.getUnqualifiedName(), field.getDataType(), this);
        }
    }
}
