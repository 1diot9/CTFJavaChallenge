package org.jooq;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UniqueKey.class */
public interface UniqueKey<R extends Record> extends Key<R> {
    @NotNull
    List<ForeignKey<?, R>> getReferences();

    boolean isPrimary();
}
