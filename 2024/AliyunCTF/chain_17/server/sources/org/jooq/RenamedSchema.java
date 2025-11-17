package org.jooq;

import java.util.List;
import org.jooq.impl.SchemaImpl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RenamedSchema.class */
public final class RenamedSchema extends SchemaImpl {
    private final Schema delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RenamedSchema(Catalog catalog, Schema delegate, String rename) {
        super(rename, catalog);
        this.delegate = delegate;
    }

    @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
    public final List<Table<?>> getTables() {
        return this.delegate.getTables();
    }

    @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
    public final List<UDT<?>> getUDTs() {
        return this.delegate.getUDTs();
    }

    @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
    public final List<Sequence<?>> getSequences() {
        return this.delegate.getSequences();
    }
}
