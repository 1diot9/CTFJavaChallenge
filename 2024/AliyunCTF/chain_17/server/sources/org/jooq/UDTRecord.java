package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.UDTRecord;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UDTRecord.class */
public interface UDTRecord<R extends UDTRecord<R>> extends QualifiedRecord<R> {
    @NotNull
    UDT<R> getUDT();
}
