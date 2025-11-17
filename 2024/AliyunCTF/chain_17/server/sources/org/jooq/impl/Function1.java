package org.jooq.impl;

import java.util.List;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Function1.class */
public final class Function1<T> extends AbstractFunction<T> {
    private final Field<?> argument;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Function1(Name name, DataType<T> type, Field<?> argument) {
        super(name, type, true);
        this.argument = argument;
    }

    @Override // org.jooq.impl.AbstractFunction
    final QueryPart arguments() {
        return this.argument;
    }

    @Override // org.jooq.impl.QOM.Function
    public final QOM.UnmodifiableList<? extends Field<?>> $args() {
        return QOM.unmodifiable((List) QueryPartListView.wrap(this.argument));
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof Function1) {
            return getQualifiedName().equals(((Function1) that).getQualifiedName()) && this.argument.equals(((Function1) that).argument);
        }
        return super.equals(that);
    }
}
