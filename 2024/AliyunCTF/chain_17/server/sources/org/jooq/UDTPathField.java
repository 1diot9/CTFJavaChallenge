package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.UDTRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UDTPathField.class */
public interface UDTPathField<R extends Record, U extends UDTRecord<U>, T> extends UDTField<U, T> {
    @NotNull
    RecordQualifier<R> getQualifier();

    @NotNull
    RecordQualifier<U> asQualifier();
}
