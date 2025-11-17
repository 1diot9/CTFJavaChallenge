package org.jooq.impl;

import java.util.Collection;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Coalesce.class */
public final class Coalesce<T> extends AbstractField<T> implements QOM.Coalesce<T> {
    private final Field<T>[] fields;

    Coalesce(Collection<? extends Field<?>> fields) {
        this((Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Coalesce(Field<?>[] fields) {
        this(fields, Tools.anyNotNull(SQLDataType.OTHER, fields));
    }

    /* JADX WARN: Multi-variable type inference failed */
    Coalesce(Field<?>[] fieldArr, DataType<T> type) {
        super(Names.N_COALESCE, type);
        this.fields = fieldArr;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.fields.length == 0) {
            ctx.visit(DSL.inline((Object) null, getDataType()));
            return;
        }
        if (this.fields.length == 1) {
            ctx.visit((Field<?>) this.fields[0]);
            return;
        }
        switch (ctx.family()) {
            case DERBY:
                ctx.visit(DSL.function(Names.N_COALESCE, getDataType(), (Field<?>[]) Tools.map(this.fields, f -> {
                    return f.getDataType().isBoolean() ? new ParenthesisedField(f) : f;
                }, x$0 -> {
                    return new Field[x$0];
                })));
                return;
            default:
                ctx.visit(DSL.function(Names.N_COALESCE, (DataType) getDataType(), (Field<?>[]) this.fields));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends Field<T>> $arg1() {
        return QOM.unmodifiable(this.fields);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends Field<T>>, ? extends QOM.Coalesce<T>> $constructor() {
        return l -> {
            if (l.isEmpty()) {
                return new Coalesce(Tools.EMPTY_FIELD, getDataType());
            }
            return new Coalesce((Field<?>[]) l.toArray(Tools.EMPTY_FIELD));
        };
    }
}
