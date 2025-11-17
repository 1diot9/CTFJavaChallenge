package org.jooq.impl;

import java.sql.ResultSet;
import org.jooq.BindingGetResultSetContext;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ExecuteContext;
import org.jooq.Field;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindingGetResultSetContext.class */
public class DefaultBindingGetResultSetContext<U> extends AbstractExecuteScope implements BindingGetResultSetContext<U> {
    private final ResultSet resultSet;
    private int index;
    private Field<U> field;
    private U value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingGetResultSetContext(ExecuteContext ctx, ResultSet resultSet, int index) {
        super(ctx);
        this.resultSet = resultSet;
        this.index = index;
    }

    @Override // org.jooq.BindingGetResultSetContext
    public final ResultSet resultSet() {
        return this.resultSet;
    }

    @Override // org.jooq.BindingGetResultSetContext
    public final int index() {
        return this.index;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void index(int i) {
        this.index = i;
    }

    @Override // org.jooq.BindingGetResultSetContext
    public final Field<U> field() {
        return this.field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void field(Field<U> f) {
        this.field = f;
    }

    @Override // org.jooq.BindingGetResultSetContext
    public void value(U v) {
        this.value = v;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final U value() {
        return this.value;
    }

    @Override // org.jooq.BindingGetResultSetContext
    public final <T> BindingGetResultSetContext<T> convert(final Converter<? super T, ? extends U> converter) {
        return new DefaultBindingGetResultSetContext<T>(this.ctx, this.resultSet, this.index) { // from class: org.jooq.impl.DefaultBindingGetResultSetContext.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // org.jooq.impl.DefaultBindingGetResultSetContext, org.jooq.BindingGetResultSetContext
            public void value(T v) {
                this.value(ContextConverter.scoped(converter).from(v, converterContext()));
            }
        };
    }

    public String toString() {
        return "DefaultBindingGetResultSetContext [index=" + this.index + ", value=" + String.valueOf(this.value) + "]";
    }
}
