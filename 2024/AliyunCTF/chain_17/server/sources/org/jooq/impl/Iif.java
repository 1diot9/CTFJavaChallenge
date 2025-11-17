package org.jooq.impl;

import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Iif.class */
public final class Iif<T> extends AbstractField<T> implements QOM.Iif<T> {
    private final Condition condition;
    private final Field<T> ifTrue;
    private final Field<T> ifFalse;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Iif(Name name, Condition condition, Field<T> ifTrue, Field<T> ifFalse) {
        super(name, ifTrue.getDataType());
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                ctx.visit(Names.N_IF).sql('(').visit(this.condition).sql(", ").visit((Field<?>) this.ifTrue).sql(", ").visit((Field<?>) this.ifFalse).sql(')');
                return;
            default:
                ctx.visit(DSL.when(this.condition, (Field) this.ifTrue).otherwise((Field) this.ifFalse));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Condition $arg1() {
        return this.condition;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg2() {
        return this.ifTrue;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg3() {
        return this.ifFalse;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Condition, ? super Field<T>, ? super Field<T>, ? extends QOM.Iif<T>> $constructor() {
        return (c, f1, f2) -> {
            return new Iif(getQualifiedName(), c, f1, f2);
        };
    }
}
