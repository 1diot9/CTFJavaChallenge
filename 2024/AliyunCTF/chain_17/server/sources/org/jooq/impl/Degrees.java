package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.asm.Opcodes;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Degrees.class */
public final class Degrees extends AbstractField<BigDecimal> implements QOM.Degrees {
    final Field<? extends Number> radians;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Degrees(Field<? extends Number> radians) {
        super(Names.N_DEGREES, Tools.allNotNull(SQLDataType.NUMERIC, radians));
        this.radians = Tools.nullSafeNotNull(radians, SQLDataType.INTEGER);
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
                ctx.visit(Internal.idiv(Internal.imul(Tools.castIfNeeded(this.radians, BigDecimal.class), DSL.inline(Opcodes.GETFIELD)), DSL.pi()));
                return;
            case SQLITE:
                ctx.visit(DSL.function(Names.N_DEGREES, getDataType(), this.radians));
                return;
            default:
                ctx.visit(DSL.function(Names.N_DEGREES, getDataType(), this.radians));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.radians;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Degrees $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Degrees> $constructor() {
        return a1 -> {
            return new Degrees(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Degrees) {
            QOM.Degrees o = (QOM.Degrees) that;
            return StringUtils.equals($radians(), o.$radians());
        }
        return super.equals(that);
    }
}
