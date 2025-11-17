package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.asm.Opcodes;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Radians.class */
public final class Radians extends AbstractField<BigDecimal> implements QOM.Radians {
    final Field<? extends Number> degrees;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Radians(Field<? extends Number> degrees) {
        super(Names.N_RADIANS, Tools.allNotNull(SQLDataType.NUMERIC, degrees));
        this.degrees = Tools.nullSafeNotNull(degrees, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                return false;
            case SQLITE:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                ctx.visit(Internal.idiv(Internal.imul(Tools.castIfNeeded(this.degrees, BigDecimal.class), DSL.pi()), DSL.inline(Opcodes.GETFIELD)));
                return;
            case SQLITE:
                ctx.visit(DSL.function(Names.N_RADIANS, getDataType(), this.degrees));
                return;
            default:
                ctx.visit(DSL.function(Names.N_RADIANS, getDataType(), this.degrees));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.degrees;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Radians $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Radians> $constructor() {
        return a1 -> {
            return new Radians(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Radians) {
            QOM.Radians o = (QOM.Radians) that;
            return StringUtils.equals($degrees(), o.$degrees());
        }
        return super.equals(that);
    }
}
