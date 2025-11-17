package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.conf.TransformUnneededArithmeticExpressions;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IMul.class */
public final class IMul<T> extends AbstractTransformable<T> implements QOM.Mul<T> {
    final Field<T> arg1;
    final Field<T> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IMul(Field<T> arg1, Field<T> arg2) {
        super(Names.N_MUL, Tools.allNotNull(Tools.dataType(arg1), arg1, arg2));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER);
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.OTHER);
    }

    @Override // org.jooq.impl.AbstractTransformable
    public final void accept0(Context<?> ctx) {
        ctx.visit((Field<?>) new Mul($arg1(), $arg2()));
    }

    @Override // org.jooq.impl.Transformable
    public final Field<?> transform(TransformUnneededArithmeticExpressions transform) {
        return Expression.transform(this, this.arg1, ExpressionOperator.MULTIPLY, this.arg2, true, transform);
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
    public final QOM.Mul<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Mul<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.Mul<T>> $constructor() {
        return (a1, a2) -> {
            return new IMul(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Mul)) {
            return super.equals(that);
        }
        QOM.Mul<?> o = (QOM.Mul) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}
