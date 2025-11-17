package org.jooq.impl;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.jooq.Batch;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractBatch.class */
public abstract class AbstractBatch implements Batch {
    static final Set<SQLDialect> NO_SUPPORT_BATCH = SQLDialect.supportedBy(SQLDialect.DUCKDB, SQLDialect.TRINO);
    final Configuration configuration;
    final DSLContext dsl;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractBatch(Configuration configuration) {
        this.configuration = configuration;
        this.dsl = DSL.using(configuration);
    }

    @Override // org.jooq.Batch
    public final CompletionStage<int[]> executeAsync() {
        return executeAsync(this.configuration.executorProvider().provide());
    }

    @Override // org.jooq.Batch
    public final CompletionStage<int[]> executeAsync(Executor executor) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(this::execute), executor), () -> {
            return executor;
        });
    }
}
