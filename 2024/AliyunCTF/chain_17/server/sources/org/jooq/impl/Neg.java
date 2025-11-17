package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.conf.TransformUnneededArithmeticExpressions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Neg.class */
public final class Neg<T> extends AbstractTransformable<T> implements QOM.Neg<T> {
    private final Field<T> field;
    private final boolean internal;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Neg(Field<T> field, boolean internal) {
        super(Names.N_NEG, field.getDataType());
        this.field = field;
        this.internal = internal;
    }

    @Override // org.jooq.impl.Transformable
    public final Field<?> transform(TransformUnneededArithmeticExpressions transform) {
        return this;
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractTransformable
    public final void accept0(Context<?> ctx) {
        if ((this.field instanceof AbstractField) && ((AbstractField) this.field).parenthesised(ctx)) {
            ctx.sql('-').visit(this.field);
        } else {
            ctx.sql("-(").visit(this.field).sql(')');
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.Neg<T>> $constructor() {
        return f -> {
            return new Neg(f, this.internal);
        };
    }
}
