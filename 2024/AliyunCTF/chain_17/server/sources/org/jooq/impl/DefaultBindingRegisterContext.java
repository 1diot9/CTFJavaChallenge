package org.jooq.impl;

import java.sql.CallableStatement;
import org.jooq.BindingRegisterContext;
import org.jooq.Converter;
import org.jooq.ExecuteContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindingRegisterContext.class */
class DefaultBindingRegisterContext<U> extends AbstractExecuteScope implements BindingRegisterContext<U> {
    private final CallableStatement statement;
    private final int index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingRegisterContext(ExecuteContext ctx, CallableStatement statement, int index) {
        super(ctx);
        this.statement = statement;
        this.index = index;
    }

    @Override // org.jooq.BindingRegisterContext
    public final CallableStatement statement() {
        return this.statement;
    }

    @Override // org.jooq.BindingRegisterContext
    public final int index() {
        return this.index;
    }

    @Override // org.jooq.BindingRegisterContext
    public final <T> BindingRegisterContext<T> convert(Converter<? super T, ? extends U> converter) {
        return new DefaultBindingRegisterContext(this.ctx, this.statement, this.index);
    }

    public String toString() {
        return "DefaultBindingRegisterContext [index=" + this.index + "]";
    }
}
