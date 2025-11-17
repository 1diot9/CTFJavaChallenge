package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Nvl2.class */
public final class Nvl2<T> extends AbstractField<T> implements QOM.Nvl2<T> {
    private final Field<?> arg1;
    private final Field<T> arg2;
    private final Field<T> arg3;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Nvl2(Field<?> arg1, Field<T> arg2, Field<T> arg3) {
        super(Names.N_NVL2, !arg1.getDataType().nullable() ? arg2.getDataType() : Tools.allNotNull(arg2.getDataType(), arg2, arg3));
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
                acceptDefault(ctx);
                return;
            case CUBRID:
            case DERBY:
            case DUCKDB:
            case FIREBIRD:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
                acceptCase(ctx);
                return;
            default:
                acceptDefault(ctx);
                return;
        }
    }

    private void acceptCase(Context<?> ctx) {
        ctx.visit(DSL.when(this.arg1.isNotNull(), (Field) this.arg2).otherwise((Field) this.arg3));
    }

    private final void acceptDefault(Context<?> ctx) {
        ctx.visit(DSL.function(Names.N_NVL2, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2, this.arg3}));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<?> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg3() {
        return this.arg3;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<?>, ? super Field<T>, ? super Field<T>, ? extends QOM.Nvl2<T>> $constructor() {
        return (a1, a2, a3) -> {
            return new Nvl2(a1, a2, a3);
        };
    }
}
