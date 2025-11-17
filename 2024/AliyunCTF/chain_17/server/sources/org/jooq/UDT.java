package org.jooq;

import org.jooq.UDTRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UDT.class */
public interface UDT<R extends UDTRecord<R>> extends RecordQualifier<R> {
    boolean isSQLUsable();

    boolean isSynthetic();
}
