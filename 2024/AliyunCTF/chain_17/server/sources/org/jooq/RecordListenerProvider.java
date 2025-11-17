package org.jooq;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordListenerProvider.class */
public interface RecordListenerProvider {
    @NotNull
    RecordListener provide();
}
