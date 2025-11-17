package org.jooq.impl;

import java.util.Collection;
import org.jooq.Configuration;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.Schema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SchemaMetaProvider.class */
public class SchemaMetaProvider implements MetaProvider {
    private final Configuration configuration;
    private final Schema[] schemas;

    public SchemaMetaProvider(Configuration configuration, Schema... schemas) {
        this.configuration = configuration;
        this.schemas = schemas;
    }

    public SchemaMetaProvider(Configuration configuration, Collection<? extends Schema> schemas) {
        this(configuration, (Schema[]) schemas.toArray(Tools.EMTPY_SCHEMA));
    }

    @Override // org.jooq.MetaProvider
    public Meta provide() {
        return CatalogMetaImpl.filterSchemas(this.configuration, this.schemas);
    }
}
