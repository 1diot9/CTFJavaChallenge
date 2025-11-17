package org.jooq.impl;

import java.io.Serializable;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapperProvider.class */
public class DefaultRecordMapperProvider implements RecordMapperProvider, Serializable {
    private final Configuration configuration;

    public DefaultRecordMapperProvider() {
        this(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DefaultRecordMapperProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // org.jooq.RecordMapperProvider
    public final <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> rowType, Class<? extends E> type) {
        if (this.configuration != null && Boolean.TRUE.equals(this.configuration.settings().isCacheRecordMappers())) {
            return (RecordMapper) Cache.run(this.configuration, () -> {
                return new DefaultRecordMapper(rowType, type, this.configuration);
            }, CacheType.CACHE_RECORD_MAPPERS, () -> {
                return Cache.key(rowType, type);
            });
        }
        return new DefaultRecordMapper(rowType, type, this.configuration);
    }
}
