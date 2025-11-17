package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultMetaProvider.class */
public class DefaultMetaProvider implements MetaProvider {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) DefaultMetaProvider.class);
    private final Configuration configuration;

    public DefaultMetaProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // org.jooq.MetaProvider
    public Meta provide() {
        return new MetaImpl(this.configuration, null);
    }

    static Meta meta(Configuration configuration) {
        Meta meta = configuration.metaProvider().provide();
        if ((meta instanceof MetaImpl) && (configuration.connectionProvider() instanceof NoConnectionProvider)) {
            log.debug("No MetaProvider configured. For best results when looking up meta data, configure a MetaProvider, or connect to a database");
            return configuration.dsl().meta("");
        }
        return meta;
    }
}
