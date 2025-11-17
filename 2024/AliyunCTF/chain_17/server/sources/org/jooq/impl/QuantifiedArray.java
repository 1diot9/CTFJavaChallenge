package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Param;
import org.jooq.QuantifiedSelect;
import org.jooq.QueryPart;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QuantifiedArray.class */
public final class QuantifiedArray<T> extends AbstractQueryPart implements QuantifiedSelect<Record1<T>>, QOM.QuantifiedArray<T> {
    final QOM.Quantifier quantifier;
    final Field<T[]> array;

    /* JADX INFO: Access modifiers changed from: package-private */
    public QuantifiedArray(QOM.Quantifier quantifier, Field<T[]> array) {
        this.quantifier = quantifier;
        this.array = array;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            default:
                ctx.visit(this.quantifier.keyword);
                ctx.sql(0 != 0 ? " ((" : " (");
                Tools.visitSubquery(ctx, delegate(ctx), 256, false);
                ctx.sql(0 != 0 ? "))" : ")");
                return;
        }
    }

    private final QueryPart delegate(Context<?> ctx) {
        Select<Record1<Object>> unionAll;
        Field<T[]> field = this.array;
        if (field instanceof QOM.Array) {
            QOM.Array<T> a = (QOM.Array) field;
            switch (ctx.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    return this.array;
                default:
                    Select<Record1<T>> select = null;
                    for (Field<T> value : a.$elements()) {
                        if (select == null) {
                            select = DSL.select(value);
                        } else {
                            select = select.unionAll(DSL.select(value));
                        }
                    }
                    return select;
            }
        }
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                return this.array;
            case H2:
            case HSQLDB:
                return DSL.select(new SelectFieldOrAsterisk[0]).from(DSL.table((Field<?>) this.array));
            default:
                if (this.array instanceof Param) {
                    Object[] values0 = (Object[]) ((Param) this.array).getValue();
                    Select<Record1<Object>> select2 = null;
                    for (Object value2 : values0) {
                        if (select2 == null) {
                            unionAll = DSL.select(DSL.val(value2));
                        } else {
                            unionAll = select2.unionAll(DSL.select(DSL.val(value2)));
                        }
                        select2 = unionAll;
                    }
                    return select2;
                }
                return DSL.select(new SelectFieldOrAsterisk[0]).from(DSL.table((Field<?>) this.array));
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super QOM.Quantifier, ? super Field<T[]>, ? extends QuantifiedArray<T>> $constructor() {
        return (q, a) -> {
            return new QuantifiedArray(q, a);
        };
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Quantifier $arg1() {
        return this.quantifier;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T[]> $arg2() {
        return this.array;
    }
}
