package org.jooq.impl;

import java.sql.PreparedStatement;
import org.jooq.BindingSetStatementContext;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ExecuteContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindingSetStatementContext.class */
class DefaultBindingSetStatementContext<U> extends AbstractExecuteScope implements BindingSetStatementContext<U>, ResourceManagingScopeTrait {
    private final PreparedStatement statement;
    private final int index;
    private final U value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingSetStatementContext(ExecuteContext ctx, PreparedStatement statement, int index, U value) {
        super(ctx);
        this.statement = statement;
        this.index = index;
        this.value = value;
    }

    @Override // org.jooq.BindingSetStatementContext
    public final PreparedStatement statement() {
        return this.statement;
    }

    @Override // org.jooq.BindingSetStatementContext
    public final int index() {
        return this.index;
    }

    @Override // org.jooq.BindingSetStatementContext
    public final U value() {
        return this.value;
    }

    @Override // org.jooq.BindingSetStatementContext
    public final <T> BindingSetStatementContext<T> convert(Converter<? extends T, ? super U> converter) {
        return new DefaultBindingSetStatementContext(this.ctx, this.statement, this.index, ContextConverter.scoped(converter).to(this.value, converterContext()));
    }

    public String toString() {
        return "DefaultBindingSetStatementContext [index=" + this.index + ", value=" + String.valueOf(this.value) + "]";
    }
}
