package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldCondition.class */
public final class FieldCondition extends AbstractCondition implements QOM.FieldCondition {
    final Field<Boolean> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldCondition(Field<Boolean> field) {
        this.field = Tools.nullSafeNotNull(field, SQLDataType.BOOLEAN);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case FIREBIRD:
                ctx.visit(this.field.eq(DSL.inline((Object) true, (DataType) this.field.getDataType())));
                return;
            default:
                ctx.visit(Tools.hasDefaultConverter(this.field) ? this.field : this.field.eq(DSL.inline((Object) true, (DataType) this.field.getDataType())));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<Boolean> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.FieldCondition $arg1(Field<Boolean> newValue) {
        return new FieldCondition(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<Boolean>, ? extends QOM.FieldCondition> $constructor() {
        return a1 -> {
            return new FieldCondition(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.FieldCondition) {
            QOM.FieldCondition o = (QOM.FieldCondition) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}
