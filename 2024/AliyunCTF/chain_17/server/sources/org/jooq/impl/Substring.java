package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Substring.class */
public final class Substring extends AbstractField<String> implements QOM.Substring {
    final Field<String> string;
    final Field<? extends Number> startingPosition;
    final Field<? extends Number> length;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Substring(Field<String> string, Field<? extends Number> startingPosition) {
        super(Names.N_SUBSTRING, Tools.allNotNull(SQLDataType.VARCHAR, string, startingPosition));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.startingPosition = Tools.nullSafeNotNull(startingPosition, SQLDataType.INTEGER);
        this.length = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Substring(Field<String> string, Field<? extends Number> startingPosition, Field<? extends Number> length) {
        super(Names.N_SUBSTRING, Tools.allNotNull(SQLDataType.VARCHAR, string, startingPosition, length));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.startingPosition = Tools.nullSafeNotNull(startingPosition, SQLDataType.INTEGER);
        this.length = Tools.nullSafeNotNull(length, SQLDataType.INTEGER);
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Name functionName = Names.N_SUBSTRING;
        switch (ctx.family()) {
            case FIREBIRD:
                if (this.length == null) {
                    ctx.visit(Names.N_SUBSTRING).sql('(').visit((Field<?>) this.string).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.startingPosition).sql(')');
                    return;
                } else {
                    ctx.visit(Names.N_SUBSTRING).sql('(').visit((Field<?>) this.string).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.startingPosition).sql(' ').visit(Keywords.K_FOR).sql(' ').visit((Field<?>) this.length).sql(')');
                    return;
                }
            case DERBY:
            case SQLITE:
                functionName = Names.N_SUBSTR;
                break;
        }
        if (this.length == null) {
            ctx.visit(DSL.function(functionName, getDataType(), (Field<?>[]) new Field[]{this.string, this.startingPosition}));
        } else {
            ctx.visit(DSL.function(functionName, getDataType(), (Field<?>[]) new Field[]{this.string, this.startingPosition, this.length}));
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg1() {
        return this.string;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg2() {
        return this.startingPosition;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg3() {
        return this.length;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Substring $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Substring $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Substring $arg3(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<String>, ? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.Substring> $constructor() {
        return (a1, a2, a3) -> {
            return new Substring(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Substring)) {
            return super.equals(that);
        }
        QOM.Substring o = (QOM.Substring) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($startingPosition(), o.$startingPosition()) && StringUtils.equals($length(), o.$length());
    }
}
