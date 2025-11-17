package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.UDTRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UDTPathTableField.class */
public interface UDTPathTableField<R extends Record, U extends UDTRecord<U>, T> extends TableField<R, T>, UDTPathField<R, U, T> {
    @Override // org.jooq.UDTPathField
    @NotNull
    RecordQualifier<R> getQualifier();
}
