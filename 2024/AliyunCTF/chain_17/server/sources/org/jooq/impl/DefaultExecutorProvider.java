package org.jooq.impl;

import java.io.Serializable;
import java.util.concurrent.Executor;
import org.jooq.ExecutorProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultExecutorProvider.class */
public class DefaultExecutorProvider implements ExecutorProvider, Serializable {
    @Override // org.jooq.ExecutorProvider
    public final Executor provide() {
        return new DefaultExecutor();
    }
}
