package org.jooq.impl;

import java.util.function.BiFunction;
import org.jooq.Clause;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Function3;
import org.jooq.RowN;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Eq.class */
public final class Eq<T> extends AbstractCondition implements QOM.Eq<T> {
    final Field<T> arg1;
    final Field<T> arg2;
    static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_COMPARISON};

    /* JADX INFO: Access modifiers changed from: package-private */
    public Eq(Field<T> arg1, Field<T> arg2) {
        this.arg1 = Tools.nullableIf(false, Tools.nullSafe((Field) arg1, (DataType<?>) arg2.getDataType()));
        this.arg2 = Tools.nullableIf(false, Tools.nullSafe((Field) arg2, (DataType<?>) arg1.getDataType()));
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        acceptCompareCondition(ctx, this, this.arg1, Comparator.EQUALS, this.arg2, (v0, v1) -> {
            return v0.eq(v1);
        }, (v0, v1) -> {
            return v0.eq(v1);
        }, (c, a1, a2) -> {
            return c.visit((Field<?>) a1).sql(" = ").visit((Field<?>) a2);
        });
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static final <T> void acceptCompareCondition(Context<?> ctx, AbstractCondition condition, Field<T> arg1, Comparator op, Field<T> arg2, BiFunction<RowN, Select<?>, Condition> compareRowSubquery, BiFunction<RowN, RowN, Condition> compareRowRow, Function3<? super Context<?>, ? super Field<T>, ? super Field<T>, ? extends Context<?>> acceptDefault) {
        SelectQueryImpl<?> s;
        boolean field1Embeddable = arg1.getDataType().isEmbeddable();
        if (field1Embeddable && (arg2 instanceof ScalarSubquery)) {
            ctx.visit(compareRowSubquery.apply(DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) arg1)), ((ScalarSubquery) arg2).query));
            return;
        }
        if (field1Embeddable && arg2.getDataType().isEmbeddable()) {
            ctx.visit(compareRowRow.apply(DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) arg1)), DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) arg2))));
            return;
        }
        if ((op == Comparator.IN || op == Comparator.NOT_IN) && (s = Transformations.subqueryWithLimit(arg2)) != null && Transformations.NO_SUPPORT_IN_LIMIT.contains(ctx.dialect())) {
            ctx.visit(arg1.compare(op, (Select) DSL.select(DSL.asterisk()).from(s.asTable("t"))));
            return;
        }
        if (arg1.getDataType().isMultiset() && arg2.getDataType().isMultiset() && !Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION))) {
            ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION, true, c -> {
                c.visit((Condition) condition);
            });
            return;
        }
        if ((arg1 instanceof Array) && ((Array) arg1).fields.fields.length == 0) {
            ctx.data(Tools.ExtendedDataKey.DATA_EMPTY_ARRAY_BASE_TYPE, arg2.getDataType().getArrayComponentDataType(), c2 -> {
                acceptDefault.apply(c2, arg1, arg2);
            });
        } else if ((arg2 instanceof Array) && ((Array) arg2).fields.fields.length == 0) {
            ctx.data(Tools.ExtendedDataKey.DATA_EMPTY_ARRAY_BASE_TYPE, arg1.getDataType().getArrayComponentDataType(), c3 -> {
                acceptDefault.apply(c3, arg1, arg2);
            });
        } else {
            acceptDefault.apply(ctx, arg1, arg2);
        }
    }

    @Deprecated
    static final Comparator comparator(Condition condition) {
        if (condition instanceof Eq) {
            return Comparator.EQUALS;
        }
        if (condition instanceof Ne) {
            return Comparator.NOT_EQUALS;
        }
        if (condition instanceof Gt) {
            return Comparator.GREATER;
        }
        if (condition instanceof Ge) {
            return Comparator.GREATER_OR_EQUAL;
        }
        if (condition instanceof Lt) {
            return Comparator.LESS;
        }
        if (condition instanceof Le) {
            return Comparator.LESS_OR_EQUAL;
        }
        return null;
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
    public final QOM.Eq<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Eq<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.Eq<T>> $constructor() {
        return (a1, a2) -> {
            return new Eq(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Eq)) {
            return super.equals(that);
        }
        QOM.Eq<?> o = (QOM.Eq) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}
