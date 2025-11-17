package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Concat.class */
public final class Concat extends AbstractField<String> implements QOM.Concat {
    private final Field<?>[] arguments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Concat(Field<?>... arguments) {
        super(Names.N_CONCAT, SQLDataType.VARCHAR);
        this.arguments = arguments;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.arguments.length == 0) {
            ctx.visit((Field<?>) DSL.inline((Object) null, getDataType()));
            return;
        }
        if (this.arguments.length == 1) {
            ctx.visit(this.arguments[0]);
            return;
        }
        Field<String>[] cast = Tools.castAllIfNeeded(this.arguments, String.class);
        if (!Boolean.TRUE.equals(ctx.settings().isRenderCoalesceToEmptyStringInConcat()) || ctx.configuration().commercial(() -> {
            return "Auto-coalescing of CONCAT arguments is available in the jOOQ 3.15 Professional Edition and jOOQ Enterprise Edition, see https://github.com/jOOQ/jOOQ/issues/11757";
        })) {
        }
        ExpressionOperator op = ExpressionOperator.CONCAT;
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                ctx.visit(DSL.function(DSL.systemName("concat"), SQLDataType.VARCHAR, cast));
                return;
            default:
                Field<?> expression = new Expression<>(op, false, cast[0], cast[1]);
                for (int i = 2; i < cast.length; i++) {
                    expression = new Expression<>(op, false, expression, cast[i]);
                }
                ctx.visit(expression);
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends Field<?>> $arg1() {
        return QOM.unmodifiable(this.arguments);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends Field<?>>, ? extends QOM.Concat> $constructor() {
        return l -> {
            return new Concat((Field[]) l.toArray(Tools.EMPTY_FIELD));
        };
    }
}
