package org.jooq;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordUnmapperProvider.class */
public interface RecordUnmapperProvider {
    @NotNull
    <E, R extends Record> RecordUnmapper<E, R> provide(Class<? extends E> cls, RecordType<R> recordType);
}
