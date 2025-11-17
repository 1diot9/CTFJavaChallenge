package org.jooq;

import org.jetbrains.annotations.Nullable;
import org.jooq.UDTRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UDTField.class */
public interface UDTField<R extends UDTRecord<R>, T> extends Field<T> {
    @Nullable
    UDT<R> getUDT();
}
