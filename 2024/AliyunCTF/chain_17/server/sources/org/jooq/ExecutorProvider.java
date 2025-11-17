package org.jooq;

import java.util.concurrent.Executor;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ExecutorProvider.class */
public interface ExecutorProvider {
    @NotNull
    Executor provide();
}
