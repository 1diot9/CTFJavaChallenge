package org.jooq.impl;

import java.sql.CallableStatement;
import org.jooq.BindingGetStatementContext;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ExecuteContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindingGetStatementContext.class */
class DefaultBindingGetStatementContext<U> extends AbstractExecuteScope implements BindingGetStatementContext<U> {
    private final CallableStatement statement;
    private final int index;
    private U value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingGetStatementContext(ExecuteContext ctx, CallableStatement statement, int index) {
        super(ctx);
        this.statement = statement;
        this.index = index;
    }

    @Override // org.jooq.BindingGetStatementContext
    public final CallableStatement statement() {
        return this.statement;
    }

    @Override // org.jooq.BindingGetStatementContext
    public final int index() {
        return this.index;
    }

    @Override // org.jooq.BindingGetStatementContext
    public void value(U v) {
        this.value = v;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final U value() {
        return this.value;
    }

    @Override // org.jooq.BindingGetStatementContext
    public final <T> BindingGetStatementContext<T> convert(final Converter<? super T, ? extends U> converter) {
        return new DefaultBindingGetStatementContext<T>(this.ctx, this.statement, this.index) { // from class: org.jooq.impl.DefaultBindingGetStatementContext.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // org.jooq.impl.DefaultBindingGetStatementContext, org.jooq.BindingGetStatementContext
            public void value(T v) {
                this.value(ContextConverter.scoped(converter).from(v, converterContext()));
            }
        };
    }

    public String toString() {
        return "DefaultBindingGetStatementContext [index=" + this.index + ", value=" + String.valueOf(this.value) + "]";
    }
}
