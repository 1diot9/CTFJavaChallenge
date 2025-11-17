package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.ExecuteType;
import org.jooq.Record;
import org.jooq.RecordContext;
import org.jooq.RecordType;
import org.jooq.Result;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordContext.class */
public class DefaultRecordContext extends AbstractScope implements RecordContext {
    private final ExecuteType type;
    private final Record record;
    Exception exception;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultRecordContext(Configuration configuration, ExecuteType type, Record record) {
        super(configuration);
        this.type = type;
        this.record = record;
    }

    @Override // org.jooq.RecordContext
    public final ExecuteType type() {
        return this.type;
    }

    @Override // org.jooq.RecordContext
    public final Record record() {
        return this.record;
    }

    @Override // org.jooq.RecordContext
    public final Record[] batchRecords() {
        return new Record[]{this.record};
    }

    @Override // org.jooq.RecordContext
    public final RecordType<?> recordType() {
        return new FieldsImpl(this.record.fields());
    }

    @Override // org.jooq.RecordContext
    public final Exception exception() {
        return this.exception;
    }

    public String toString() {
        Result<Record> result = DSL.using(this.configuration).newResult(this.record.fields());
        result.add(this.record);
        return result.toString();
    }
}
