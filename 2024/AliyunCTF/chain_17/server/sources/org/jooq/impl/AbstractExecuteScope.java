package org.jooq.impl;

import org.jooq.ConverterContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteScope;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractExecuteScope.class */
abstract class AbstractExecuteScope extends AbstractScope implements ExecuteScope {
    final ExecuteContext ctx;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractExecuteScope(ExecuteContext ctx) {
        super(ctx.configuration(), ctx.data());
        this.ctx = ctx;
    }

    @Override // org.jooq.ExecuteScope
    public final ConverterContext converterContext() {
        return this.ctx.converterContext();
    }

    @Override // org.jooq.ExecuteScope
    public ExecuteContext executeContext() {
        return this.ctx;
    }
}
