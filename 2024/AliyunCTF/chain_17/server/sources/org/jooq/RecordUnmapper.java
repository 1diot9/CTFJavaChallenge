package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.exception.MappingException;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordUnmapper.class */
public interface RecordUnmapper<E, R extends Record> {
    @NotNull
    R unmap(E e) throws MappingException;
}
