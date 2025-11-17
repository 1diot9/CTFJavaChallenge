package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Function2;
import org.jooq.Record;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableEq.class */
public final class TableEq<R extends Record> extends AbstractCondition implements QOM.TableEq<R> {
    final Table<R> arg1;
    final Table<R> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableEq(Table<R> arg1, Table<R> arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                ctx.sql('(').visit(this.arg1).sql(" = ").visit(this.arg2).sql(')');
                return;
            default:
                ctx.visit(DSL.row((SelectField<?>[]) this.arg1.fields()).eq(DSL.row((SelectField<?>[]) this.arg2.fields())));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Table<R> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Table<R> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.TableEq<R> $arg1(Table<R> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.TableEq<R> $arg2(Table<R> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Table<R>, ? super Table<R>, ? extends QOM.TableEq<R>> $constructor() {
        return (a1, a2) -> {
            return new TableEq(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.TableEq)) {
            return super.equals(that);
        }
        QOM.TableEq<?> o = (QOM.TableEq) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}
