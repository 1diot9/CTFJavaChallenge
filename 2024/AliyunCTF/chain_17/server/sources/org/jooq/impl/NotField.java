package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NotField.class */
final class NotField extends AbstractField<Boolean> implements QOM.NotField {
    final Field<Boolean> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotField(Field<Boolean> field) {
        super(Names.N_NOT, SQLDataType.BOOLEAN);
        this.field = Tools.nullSafeNotNull(field, SQLDataType.BOOLEAN);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case FIREBIRD:
                ctx.visit((Field<?>) DSL.field(DSL.not(DSL.condition(this.field))));
                return;
            default:
                ctx.visit(Keywords.K_NOT).sql(" (").visit(Tools.hasDefaultConverter(this.field) ? this.field : DSL.condition(this.field)).sql(')');
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<Boolean> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.NotField $arg1(Field<Boolean> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<Boolean>, ? extends QOM.NotField> $constructor() {
        return a1 -> {
            return new NotField(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.NotField) {
            QOM.NotField o = (QOM.NotField) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}
