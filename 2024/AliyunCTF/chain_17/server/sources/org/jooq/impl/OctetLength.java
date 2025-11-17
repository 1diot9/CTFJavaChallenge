package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/OctetLength.class */
public final class OctetLength extends AbstractField<Integer> implements QOM.OctetLength {
    final Field<String> string;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OctetLength(Field<String> string) {
        super(Names.N_OCTET_LENGTH, Tools.allNotNull(SQLDataType.INTEGER, string));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case DUCKDB:
            case SQLITE:
            case TRINO:
                ctx.visit(DSL.function(Names.N_LENGTH, getDataType(), this.string));
                return;
            default:
                ctx.visit(DSL.function(Names.N_OCTET_LENGTH, getDataType(), this.string));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<String> $arg1() {
        return this.string;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.OctetLength $arg1(Field<String> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<String>, ? extends QOM.OctetLength> $constructor() {
        return a1 -> {
            return new OctetLength(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.OctetLength) {
            QOM.OctetLength o = (QOM.OctetLength) that;
            return StringUtils.equals($string(), o.$string());
        }
        return super.equals(that);
    }
}
