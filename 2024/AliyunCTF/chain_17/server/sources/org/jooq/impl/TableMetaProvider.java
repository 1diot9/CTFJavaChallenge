package org.jooq.impl;

import java.util.Collection;
import org.jooq.Configuration;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableMetaProvider.class */
public class TableMetaProvider implements MetaProvider {
    private final Configuration configuration;
    private final Table<?>[] tables;

    public TableMetaProvider(Configuration configuration, Table<?>... tables) {
        this.configuration = configuration;
        this.tables = tables;
    }

    public TableMetaProvider(Configuration configuration, Collection<? extends Table<?>> tables) {
        this(configuration, (Table<?>[]) tables.toArray(Tools.EMPTY_TABLE));
    }

    @Override // org.jooq.MetaProvider
    public Meta provide() {
        return CatalogMetaImpl.filterTables(this.configuration, this.tables);
    }
}
