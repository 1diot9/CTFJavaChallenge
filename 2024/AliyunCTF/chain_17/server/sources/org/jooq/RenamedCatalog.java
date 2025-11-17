package org.jooq;

import java.util.List;
import org.jooq.impl.CatalogImpl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RenamedCatalog.class */
public final class RenamedCatalog extends CatalogImpl {
    private final Catalog delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RenamedCatalog(Catalog delegate, String rename) {
        super(rename, delegate.getComment());
        this.delegate = delegate;
    }

    @Override // org.jooq.impl.CatalogImpl, org.jooq.Catalog
    public final List<Schema> getSchemas() {
        return this.delegate.getSchemas();
    }
}
