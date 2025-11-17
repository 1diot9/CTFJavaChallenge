package org.jooq.impl;

import org.jooq.Record;
import org.jooq.RecordMapper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DelayedRecordMapper.class */
public final class DelayedRecordMapper<R extends Record, E> implements RecordMapper<R, E> {
    final java.util.function.Function<FieldsImpl<R>, RecordMapper<R, E>> init;
    RecordMapper<R, E> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DelayedRecordMapper(java.util.function.Function<FieldsImpl<R>, RecordMapper<R, E>> init) {
        this.init = init;
    }

    @Override // org.jooq.RecordMapper
    public final E map(R record) {
        if (this.delegate == null) {
            this.delegate = this.init.apply(((AbstractRecord) record).fields.fields);
        }
        return this.delegate.map(record);
    }
}
