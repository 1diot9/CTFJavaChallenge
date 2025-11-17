package org.jooq.impl;

import org.jooq.CaseValueStep;
import org.jooq.CaseWhenStep;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Choose.class */
public final class Choose<T> extends AbstractField<T> implements QOM.Choose<T> {
    private Field<Integer> index;
    private Field<T>[] values;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Choose(Field<Integer> index, Field<T>[] values) {
        this(index, values, Tools.nullSafeDataType((Field<?>[]) values));
    }

    Choose(Field<Integer> index, Field<T>[] values, DataType<T> type) {
        super(Names.N_CHOOSE, type);
        this.index = index;
        this.values = values;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        CaseWhenStep<Integer, T> when;
        if (this.values.length == 0) {
            ctx.visit((Field<?>) DSL.inline((Object) null, getDataType()));
            return;
        }
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case DUCKDB:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case POSTGRES:
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
                CaseValueStep<Integer> s = DSL.choose((Field) this.index);
                CaseWhenStep<Integer, T> when2 = null;
                for (int i = 0; i < this.values.length; i++) {
                    if (when2 == null) {
                        when = s.when(DSL.inline(i + 1), (Field) this.values[i]);
                    } else {
                        when = when2.when(DSL.inline(i + 1), (Field) this.values[i]);
                    }
                    when2 = when;
                }
                ctx.visit((Field<?>) when2);
                return;
            case MARIADB:
            case MYSQL:
                ctx.visit(DSL.function(Names.N_ELT, getDataType(), Tools.combine((Field<?>) this.index, (Field<?>[]) this.values)));
                return;
            default:
                ctx.visit(DSL.function(Names.N_CHOOSE, getDataType(), Tools.combine((Field<?>) this.index, (Field<?>[]) this.values)));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<Integer> $arg1() {
        return this.index;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.UnmodifiableList<? extends Field<T>> $arg2() {
        return QOM.unmodifiable(this.values);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<Integer>, ? super QOM.UnmodifiableList<? extends Field<T>>, ? extends QOM.Choose<T>> $constructor() {
        return (i, v) -> {
            if (v.isEmpty()) {
                return new Choose(i, Tools.EMPTY_FIELD, getDataType());
            }
            return new Choose(i, (Field[]) v.toArray(Tools.EMPTY_FIELD));
        };
    }
}
