package org.jooq.impl;

import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ConditionAsField.class */
public final class ConditionAsField extends AbstractField<Boolean> implements QOM.ConditionAsField {
    final Condition condition;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConditionAsField(Condition condition) {
        super(Names.N_FIELD, Tools.allNotNull(SQLDataType.BOOLEAN));
        this.condition = condition;
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case FIREBIRD:
                if ((this.condition instanceof AbstractCondition) && !((AbstractCondition) this.condition).isNullable()) {
                    ctx.visit(DSL.when(this.condition, (Field) DSL.inline(true)).else_((Field) DSL.inline(false)));
                    return;
                } else {
                    ctx.visit((Field<?>) DSL.when(this.condition, (Field) DSL.inline(true)).when(DSL.not(this.condition), (Field) DSL.inline(false)));
                    return;
                }
            default:
                if ((this.condition instanceof AbstractField) && ((AbstractField) this.condition).parenthesised(ctx)) {
                    ctx.visit(this.condition);
                    return;
                } else {
                    ctx.sql('(').visit(this.condition).sql(')');
                    return;
                }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Condition $arg1() {
        return this.condition;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.ConditionAsField $arg1(Condition newValue) {
        return new ConditionAsField(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Condition, ? extends QOM.ConditionAsField> $constructor() {
        return a1 -> {
            return new ConditionAsField(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.ConditionAsField) {
            QOM.ConditionAsField o = (QOM.ConditionAsField) that;
            return StringUtils.equals($condition(), o.$condition());
        }
        return super.equals(that);
    }
}
