package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.XML;
import org.jooq.XMLAggOrderByStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLAgg.class */
public final class XMLAgg extends AbstractAggregateFunction<XML> implements XMLAggOrderByStep<XML>, QOM.XMLAgg {
    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLAgg(Field<XML> arg) {
        super(false, Names.N_XMLAGG, (DataType) SQLDataType.XML, (Field<?>[]) new Field[]{arg});
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        ctx.visit(Names.N_XMLAGG).sql('(');
        acceptArguments0(ctx);
        acceptOrderBy(ctx);
        ctx.sql(')');
        acceptFilterClause(ctx);
        acceptOverClause(ctx);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<XML> $arg1() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<XML>, ? extends QOM.XMLAgg> $constructor() {
        return f -> {
            return new XMLAgg(f);
        };
    }
}
