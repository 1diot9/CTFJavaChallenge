package org.jooq.impl;

import java.sql.SQLOutput;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ExecuteContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindingSetSQLOutputContext.class */
class DefaultBindingSetSQLOutputContext<U> extends AbstractExecuteScope implements BindingSetSQLOutputContext<U>, ResourceManagingScopeTrait {
    private final SQLOutput output;
    private final U value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingSetSQLOutputContext(ExecuteContext ctx, SQLOutput output, U value) {
        super(ctx);
        this.output = output;
        this.value = value;
    }

    @Override // org.jooq.BindingSetSQLOutputContext
    public final SQLOutput output() {
        return this.output;
    }

    @Override // org.jooq.BindingSetSQLOutputContext
    public final U value() {
        return this.value;
    }

    @Override // org.jooq.BindingSetSQLOutputContext
    public final <T> BindingSetSQLOutputContext<T> convert(Converter<? extends T, ? super U> converter) {
        return new DefaultBindingSetSQLOutputContext(this.ctx, this.output, ContextConverter.scoped(converter).to(this.value, converterContext()));
    }

    public String toString() {
        return "DefaultBindingSetSQLOutputContext [value=" + String.valueOf(this.value) + "]";
    }
}
