package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SelectField;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IsNull.class */
final class IsNull extends AbstractCondition implements QOM.IsNull {
    final Field<?> field;
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_IS_NULL};

    /* JADX INFO: Access modifiers changed from: package-private */
    public IsNull(Field<?> field) {
        this.field = Tools.nullSafeNotNull(field, SQLDataType.OTHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.field.getDataType().isEmbeddable()) {
            ctx.visit(DSL.row((SelectField<?>[]) Tools.embeddedFields(this.field)).isNull());
        } else {
            ctx.visit(this.field).sql(' ').visit(Keywords.K_IS_NULL);
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<?> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.IsNull $arg1(Field<?> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<?>, ? extends QOM.IsNull> $constructor() {
        return a1 -> {
            return new IsNull(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.IsNull) {
            QOM.IsNull o = (QOM.IsNull) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}
