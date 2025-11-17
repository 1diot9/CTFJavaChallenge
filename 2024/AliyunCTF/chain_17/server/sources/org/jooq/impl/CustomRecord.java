package org.jooq.impl;

import org.jooq.Table;
import org.jooq.TableRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CustomRecord.class */
public abstract class CustomRecord<R extends TableRecord<R>> extends TableRecordImpl<R> {
    protected CustomRecord(Table<R> table) {
        super(table);
    }
}
