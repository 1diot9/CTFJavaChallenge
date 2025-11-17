package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Mode.class */
final class Mode<T> extends AbstractAggregateFunction<T> implements QOM.Mode<T> {
    private static final Set<SQLDialect> EMULATE_AS_ORDERED_SET_AGG = SQLDialect.supportedBy(SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Mode(Field<T> arg) {
        super(false, Names.N_MODE, (DataType) arg.getDataType(), (Field<?>[]) new Field[]{arg});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        if (EMULATE_AS_ORDERED_SET_AGG.contains(ctx.dialect())) {
            ctx.visit((Field<?>) DSL.mode().withinGroupOrderBy(this.arguments.get(0)));
        } else {
            super.accept(ctx);
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.Mode<T>> $constructor() {
        return f -> {
            return new Mode(f);
        };
    }
}
