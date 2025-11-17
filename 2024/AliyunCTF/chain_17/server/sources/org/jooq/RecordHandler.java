package org.jooq;

import java.util.function.Consumer;
import org.jooq.Record;

@FunctionalInterface
@Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordHandler.class */
public interface RecordHandler<R extends Record> extends Consumer<R> {
    void next(R r);

    @Override // java.util.function.Consumer
    default void accept(R record) {
        next(record);
    }
}
