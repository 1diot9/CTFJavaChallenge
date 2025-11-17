package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cardinality.class */
public final class Cardinality extends AbstractField<Integer> implements QOM.Cardinality {
    final Field<? extends Object[]> array;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cardinality(Field<? extends Object[]> array) {
        super(Names.N_CARDINALITY, Tools.allNotNull(SQLDataType.INTEGER, array));
        this.array = Tools.nullSafeNotNull(array, SQLDataType.OTHER.array());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                ctx.visit(DSL.function(Names.N_ARRAY_LENGTH, getDataType(), this.array));
                return;
            default:
                ctx.visit(DSL.function(Names.N_CARDINALITY, getDataType(), this.array));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Object[]> $arg1() {
        return this.array;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Cardinality $arg1(Field<? extends Object[]> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Object[]>, ? extends QOM.Cardinality> $constructor() {
        return a1 -> {
            return new Cardinality(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Cardinality) {
            QOM.Cardinality o = (QOM.Cardinality) that;
            return StringUtils.equals($array(), o.$array());
        }
        return super.equals(that);
    }
}
