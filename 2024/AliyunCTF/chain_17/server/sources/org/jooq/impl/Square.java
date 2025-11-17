package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Square.class */
public final class Square<T extends Number> extends AbstractField<T> implements QOM.Square<T> {
    final Field<T> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Square(Field<T> value) {
        super(Names.N_SQUARE, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), (Field<?>) value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                return false;
            case CUBRID:
            case DERBY:
            case DUCKDB:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case TRINO:
            case YUGABYTEDB:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                ctx.visit(Internal.imul(this.value, this.value));
                return;
            case CUBRID:
            case DERBY:
            case DUCKDB:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case TRINO:
            case YUGABYTEDB:
                if (Tools.isSimple(ctx, this.value)) {
                    ctx.visit(Internal.imul(this.value, this.value));
                    return;
                } else {
                    ctx.visit((Field<?>) DSL.power((Field<? extends Number>) this.value, (Field<? extends Number>) DSL.inline(2)));
                    return;
                }
            default:
                ctx.visit(DSL.function(Names.N_SQUARE, (DataType) getDataType(), (Field<?>) this.value));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Square<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.Square<T>> $constructor() {
        return a1 -> {
            return new Square(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Square) {
            QOM.Square<?> o = (QOM.Square) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}
