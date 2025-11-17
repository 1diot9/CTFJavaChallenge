package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.conf.TransformUnneededArithmeticExpressions;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Sub.class */
public final class Sub<T> extends AbstractTransformable<T> implements QOM.Sub<T> {
    final Field<T> arg1;
    final Field<T> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Sub(Field<T> arg1, Field<T> arg2) {
        super(Names.N_SUB, Tools.allNotNull(Tools.dataType(arg1), arg1, arg2));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER);
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.OTHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractTransformable
    public final void accept0(Context<?> ctx) {
        ctx.sql('(').visit(this.arg1).sql(" - ").visit((Field<?>) this.arg2).sql(')');
    }

    @Override // org.jooq.impl.Transformable
    public final Field<?> transform(TransformUnneededArithmeticExpressions transform) {
        return Expression.transform(this, this.arg1, ExpressionOperator.SUBTRACT, this.arg2, false, transform);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTypedNamed
    public final DataType<?> getExpressionDataType() {
        return Expression.getExpressionDataType((AbstractField) this.arg1, ExpressionOperator.SUBTRACT, (AbstractField) this.arg2);
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
    public final QOM.Sub<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Sub<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.Sub<T>> $constructor() {
        return (a1, a2) -> {
            return new Sub(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Sub)) {
            return super.equals(that);
        }
        QOM.Sub<?> o = (QOM.Sub) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}
