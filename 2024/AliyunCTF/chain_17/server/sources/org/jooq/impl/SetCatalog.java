package org.jooq.impl;

import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SetCatalog.class */
final class SetCatalog extends AbstractDDLQuery implements QOM.SetCatalog {
    final Catalog catalog;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SetCatalog(Configuration configuration, Catalog catalog) {
        super(configuration);
        this.catalog = catalog;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case H2:
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(DSL.setSchema(this.catalog.getName()));
                return;
            default:
                ctx.visit(Keywords.K_SET).sql(' ').visit(Keywords.K_CATALOG).sql(' ').visit(this.catalog);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.SetCatalog
    public final Catalog $catalog() {
        return this.catalog;
    }

    @Override // org.jooq.impl.QOM.SetCatalog
    public final QOM.SetCatalog $catalog(Catalog newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Catalog, ? extends QOM.SetCatalog> $constructor() {
        return a1 -> {
            return new SetCatalog(configuration(), a1);
        };
    }
}
