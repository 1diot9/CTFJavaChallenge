package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IsDistinctFrom.class */
public final class IsDistinctFrom<T> extends AbstractCondition implements QOM.IsDistinctFrom<T> {
    final Field<T> arg1;
    final Field<T> arg2;
    static final Set<SQLDialect> EMULATE_DISTINCT_PREDICATE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY);
    static final Set<SQLDialect> SUPPORT_DISTINCT_WITH_ARROW = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);

    /* JADX INFO: Access modifiers changed from: package-private */
    public IsDistinctFrom(Field<T> arg1, Field<T> arg2) {
        this.arg1 = Tools.nullableIf(true, Tools.nullSafe((Field) arg1, (DataType<?>) arg2.getDataType()));
        this.arg2 = Tools.nullableIf(true, Tools.nullSafe((Field) arg2, (DataType<?>) arg1.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.arg1.getDataType().isEmbeddable() && this.arg2.getDataType().isEmbeddable()) {
            ctx.visit(DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) this.arg1)).isDistinctFrom(DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) this.arg2))));
            return;
        }
        if (EMULATE_DISTINCT_PREDICATE.contains(ctx.dialect())) {
            ctx.visit(DSL.notExists(DSL.select(this.arg1.as("x")).intersect((Select) DSL.select(this.arg2.as("x")))));
            return;
        }
        if (SUPPORT_DISTINCT_WITH_ARROW.contains(ctx.dialect())) {
            ctx.visit(DSL.condition("{not}({0} <=> {1})", this.arg1, this.arg2));
        } else if (SQLDialect.SQLITE == ctx.family()) {
            ctx.visit(DSL.condition("{0} {is not} {1}", this.arg1, this.arg2));
        } else {
            ctx.visit((Field<?>) this.arg1).sql(' ').visit(Keywords.K_IS).sql(' ').visit(Keywords.K_DISTINCT).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.arg2);
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.IsDistinctFrom<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.IsDistinctFrom<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.IsDistinctFrom<T>> $constructor() {
        return (a1, a2) -> {
            return new IsDistinctFrom(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.IsDistinctFrom)) {
            return super.equals(that);
        }
        QOM.IsDistinctFrom<?> o = (QOM.IsDistinctFrom) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}
