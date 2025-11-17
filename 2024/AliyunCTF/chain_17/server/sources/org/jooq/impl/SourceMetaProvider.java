package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.SQLDialect;
import org.jooq.Source;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SourceMetaProvider.class */
final class SourceMetaProvider implements MetaProvider {
    private final Configuration configuration;
    private final Source[] sources;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SourceMetaProvider(Configuration configuration, Source... sources) {
        this.configuration = configuration;
        this.sources = sources;
    }

    @Override // org.jooq.MetaProvider
    public final Meta provide() {
        if (this.sources.length > 0) {
            String s = this.sources[0].readString();
            this.sources[0] = Source.of(s);
            if (s.startsWith("<?xml") || s.startsWith("<information_schema") || s.startsWith("<!--")) {
                return new InformationSchemaMetaProvider(this.configuration, this.sources).provide();
            }
        }
        SQLDialect dialect = this.configuration.settings().getInterpreterDialect();
        switch (((SQLDialect) StringUtils.defaultIfNull(dialect, SQLDialect.DEFAULT)).family()) {
            case DEFAULT:
                return new InterpreterMetaProvider(this.configuration, this.sources).provide();
            case DERBY:
            case H2:
            case HSQLDB:
            case SQLITE:
                return new TranslatingMetaProvider(this.configuration, this.sources).provide();
            default:
                throw new UnsupportedOperationException("Interpreter dialect not yet supported: " + String.valueOf(dialect));
        }
    }
}
