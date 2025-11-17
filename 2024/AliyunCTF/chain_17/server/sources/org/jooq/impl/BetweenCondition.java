package org.jooq.impl;

import java.util.Set;
import org.jooq.BetweenAndStep;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.RowN;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BetweenCondition.class */
public final class BetweenCondition<T> extends AbstractCondition implements BetweenAndStep<T>, QOM.Between<T> {
    private static final Clause[] CLAUSES_BETWEEN = {Clause.CONDITION, Clause.CONDITION_BETWEEN};
    private static final Clause[] CLAUSES_BETWEEN_SYMMETRIC = {Clause.CONDITION, Clause.CONDITION_BETWEEN_SYMMETRIC};
    private static final Clause[] CLAUSES_NOT_BETWEEN = {Clause.CONDITION, Clause.CONDITION_NOT_BETWEEN};
    private static final Clause[] CLAUSES_NOT_BETWEEN_SYMMETRIC = {Clause.CONDITION, Clause.CONDITION_NOT_BETWEEN_SYMMETRIC};
    private static final Set<SQLDialect> NO_SUPPORT_SYMMETRIC = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    private final boolean symmetric;
    private final boolean not;
    final Field<T> field;
    final Field<T> minValue;
    Field<T> maxValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BetweenCondition(Field<T> field, Field<T> minValue, boolean not, boolean symmetric) {
        this.field = Tools.nullableIf(false, Tools.nullSafe((Field) field, (DataType<?>) minValue.getDataType()));
        this.minValue = Tools.nullableIf(false, Tools.nullSafe((Field) minValue, (DataType<?>) field.getDataType()));
        this.not = not;
        this.symmetric = symmetric;
    }

    @Override // org.jooq.BetweenAndStep
    public final Condition and(T value) {
        return and((Field) DSL.val(value, this.field.getDataType()));
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.Condition
    public final Condition and(Field f) {
        if (this.maxValue == null) {
            this.maxValue = Tools.nullableIf(false, Tools.nullSafe(f, (DataType<?>) this.field.getDataType()));
            return this;
        }
        return super.and((Field<Boolean>) f);
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return this.not ? this.symmetric ? CLAUSES_NOT_BETWEEN_SYMMETRIC : CLAUSES_NOT_BETWEEN : this.symmetric ? CLAUSES_BETWEEN_SYMMETRIC : CLAUSES_BETWEEN;
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Condition or;
        Condition and;
        if (this.field.getDataType().isEmbeddable() && this.minValue.getDataType().isEmbeddable() && this.maxValue.getDataType().isEmbeddable()) {
            RowN f = DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) this.field));
            RowN min = DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) this.minValue));
            RowN max = DSL.row((SelectField<?>[]) Tools.embeddedFields((Field<?>) this.maxValue));
            if (this.not) {
                if (this.symmetric) {
                    and = f.notBetweenSymmetric(min).and(max);
                } else {
                    and = f.notBetween(min).and(max);
                }
            } else if (this.symmetric) {
                and = f.betweenSymmetric(min).and(max);
            } else {
                and = f.between(min).and(max);
            }
            ctx.visit(and);
            return;
        }
        if (this.symmetric && NO_SUPPORT_SYMMETRIC.contains(ctx.dialect())) {
            if (this.not) {
                or = this.field.notBetween((Field) this.minValue, (Field) this.maxValue).and(this.field.notBetween((Field) this.maxValue, (Field) this.minValue));
            } else {
                or = this.field.between((Field) this.minValue, (Field) this.maxValue).or(this.field.between((Field) this.maxValue, (Field) this.minValue));
            }
            ctx.visit(or);
            return;
        }
        ctx.visit((Field<?>) this.field);
        if (this.not) {
            ctx.sql(' ').visit(Keywords.K_NOT);
        }
        ctx.sql(' ').visit(Keywords.K_BETWEEN);
        if (this.symmetric) {
            ctx.sql(' ').visit(Keywords.K_SYMMETRIC);
        }
        ctx.sql(' ').visit(this.minValue);
        ctx.sql(' ').visit(Keywords.K_AND);
        ctx.sql(' ').visit(this.maxValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<T>, ? super Field<T>, ? super Field<T>, ? extends QOM.Between<T>> $constructor() {
        return (f, min, max) -> {
            return (QOM.Between) new BetweenCondition(f, min, this.not, this.symmetric).and(max);
        };
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg2() {
        return this.minValue;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg3() {
        return this.maxValue;
    }

    @Override // org.jooq.impl.QOM.Between
    public final boolean $symmetric() {
        return this.symmetric;
    }

    @Override // org.jooq.impl.QOM.Between
    public final QOM.Between<T> $symmetric(boolean s) {
        return (QOM.Between) new BetweenCondition($arg1(), $arg2(), this.not, s).and((Field) $arg3());
    }
}
