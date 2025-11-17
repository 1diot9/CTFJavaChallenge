package org.jooq.impl;

import java.io.Serializable;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.RecordType;
import org.jooq.RecordUnmapper;
import org.jooq.RecordUnmapperProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordUnmapperProvider.class */
public class DefaultRecordUnmapperProvider implements RecordUnmapperProvider, Serializable {
    private final Configuration configuration;

    public DefaultRecordUnmapperProvider() {
        this(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DefaultRecordUnmapperProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // org.jooq.RecordUnmapperProvider
    public final <E, R extends Record> RecordUnmapper<E, R> provide(Class<? extends E> type, RecordType<R> rowType) {
        return new DefaultRecordUnmapper(type, rowType, this.configuration);
    }
}
