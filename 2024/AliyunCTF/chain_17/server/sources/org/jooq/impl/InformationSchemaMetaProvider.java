package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.Source;
import org.jooq.util.jaxb.tools.MiniJAXB;
import org.jooq.util.xml.jaxb.InformationSchema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InformationSchemaMetaProvider.class */
public class InformationSchemaMetaProvider implements MetaProvider {
    private final Configuration configuration;
    private final InformationSchema schema;
    private final Source[] sources;

    public InformationSchemaMetaProvider(Configuration configuration, Source... sources) {
        this.configuration = configuration;
        this.schema = null;
        this.sources = sources;
    }

    public InformationSchemaMetaProvider(Configuration configuration, InformationSchema schema) {
        this.configuration = configuration;
        this.schema = schema;
        this.sources = null;
    }

    @Override // org.jooq.MetaProvider
    public Meta provide() {
        InformationSchema s = this.schema;
        if (s == null) {
            s = new InformationSchema();
            for (Source source : this.sources) {
                MiniJAXB.append(s, (InformationSchema) MiniJAXB.unmarshal(source.reader(), InformationSchema.class));
            }
        }
        return new InformationSchemaMetaImpl(this.configuration, s);
    }
}
