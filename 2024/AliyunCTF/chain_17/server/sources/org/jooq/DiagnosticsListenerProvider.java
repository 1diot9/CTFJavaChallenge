package org.jooq;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DiagnosticsListenerProvider.class */
public interface DiagnosticsListenerProvider {
    @NotNull
    DiagnosticsListener provide();
}
