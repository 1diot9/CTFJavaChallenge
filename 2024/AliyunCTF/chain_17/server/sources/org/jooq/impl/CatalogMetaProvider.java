package org.jooq.impl;

import java.util.Collection;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Meta;
import org.jooq.MetaProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CatalogMetaProvider.class */
public class CatalogMetaProvider implements MetaProvider {
    private final Configuration configuration;
    private final Catalog[] catalogs;

    public CatalogMetaProvider(Configuration configuration, Catalog... catalogs) {
        this.configuration = configuration;
        this.catalogs = catalogs;
    }

    public CatalogMetaProvider(Configuration configuration, Collection<? extends Catalog> catalogs) {
        this(configuration, (Catalog[]) catalogs.toArray(Tools.EMPTY_CATALOG));
    }

    @Override // org.jooq.MetaProvider
    public Meta provide() {
        return CatalogMetaImpl.filterCatalogs(this.configuration, this.catalogs);
    }
}
