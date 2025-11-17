package org.jooq;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordMapperProvider.class */
public interface RecordMapperProvider {
    @NotNull
    <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> cls);
}
