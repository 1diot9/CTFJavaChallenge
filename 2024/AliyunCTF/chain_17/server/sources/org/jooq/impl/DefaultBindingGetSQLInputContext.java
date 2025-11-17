package org.jooq.impl;

import java.sql.SQLInput;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ExecuteContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindingGetSQLInputContext.class */
class DefaultBindingGetSQLInputContext<U> extends AbstractExecuteScope implements BindingGetSQLInputContext<U> {
    private final SQLInput input;
    private U value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingGetSQLInputContext(ExecuteContext ctx, SQLInput input) {
        super(ctx);
        this.input = input;
    }

    @Override // org.jooq.BindingGetSQLInputContext
    public final SQLInput input() {
        return this.input;
    }

    @Override // org.jooq.BindingGetSQLInputContext
    public void value(U v) {
        this.value = v;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final U value() {
        return this.value;
    }

    @Override // org.jooq.BindingGetSQLInputContext
    public final <T> BindingGetSQLInputContext<T> convert(final Converter<? super T, ? extends U> converter) {
        return new DefaultBindingGetSQLInputContext<T>(this.ctx, this.input) { // from class: org.jooq.impl.DefaultBindingGetSQLInputContext.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // org.jooq.impl.DefaultBindingGetSQLInputContext, org.jooq.BindingGetSQLInputContext
            public void value(T v) {
                this.value(ContextConverter.scoped(converter).from(v, converterContext()));
            }
        };
    }

    public String toString() {
        return "DefaultBindingGetSQLInputContext [value=" + String.valueOf(this.value) + "]";
    }
}
