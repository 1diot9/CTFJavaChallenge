package org.jooq.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultExecutor.class */
public class DefaultExecutor implements Executor {
    private static final Executor EXECUTOR;

    static {
        Executor executor;
        if (ForkJoinPool.getCommonPoolParallelism() > 1) {
            executor = ForkJoinPool.commonPool();
        } else {
            executor = command -> {
                new Thread(command).start();
            };
        }
        EXECUTOR = executor;
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable command) {
        EXECUTOR.execute(command);
    }
}
