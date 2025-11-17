package org.jooq.impl;

import java.util.List;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Function.class */
public final class Function<T> extends AbstractFunction<T> {
    private final QueryPartList<Field<?>> arguments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Function(String name, DataType<T> type, Field<?>... arguments) {
        this(DSL.unquotedName(name), type, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Function(Name name, DataType<T> type, Field<?>... arguments) {
        this(name, type, true, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Function(Name name, DataType<T> type, boolean applySchemaMapping, Field<?>... arguments) {
        super(name, type, applySchemaMapping);
        this.arguments = new QueryPartList<>(arguments);
    }

    @Override // org.jooq.impl.AbstractFunction
    final QueryPart arguments() {
        return this.arguments;
    }

    @Override // org.jooq.impl.QOM.Function
    public final QOM.UnmodifiableList<? extends Field<?>> $args() {
        return QOM.unmodifiable((List) this.arguments);
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof Function) {
            return getQualifiedName().equals(((Function) that).getQualifiedName()) && this.arguments.equals(((Function) that).arguments);
        }
        return super.equals(that);
    }
}
