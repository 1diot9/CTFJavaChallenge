package org.jooq;

import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordMapper.class */
public interface RecordMapper<R extends Record, E> extends Function<R, E> {
    @Nullable
    E map(R r);

    @Override // java.util.function.Function
    default E apply(R record) {
        return map(record);
    }
}
