package org.jooq.impl;

import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import org.jooq.CommonTableExpression;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.Select;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SplitPart.class */
public final class SplitPart extends AbstractField<String> implements QOM.SplitPart {
    final Field<String> string;
    final Field<String> delimiter;
    final Field<? extends Number> n;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitPart(Field<String> string, Field<String> delimiter, Field<? extends Number> n) {
        super(Names.N_SPLIT_PART, Tools.allNotNull(SQLDataType.VARCHAR, string, delimiter, n));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.delimiter = Tools.nullSafeNotNull(delimiter, SQLDataType.VARCHAR);
        this.n = Tools.nullSafeNotNull(n, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                return false;
            case MARIADB:
            case MYSQL:
                return false;
            case HSQLDB:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                ctx.visit(DSL.arrayGet(DSL.function(Names.N_STR_SPLIT, getDataType().array(), (Field<?>[]) new Field[]{this.string, this.delimiter}), (Field<Integer>) this.n));
                return;
            case MARIADB:
            case MYSQL:
                ctx.visit((Field<?>) DSL.substring(DSL.substringIndex(this.string, this.delimiter, this.n), (Field<? extends Number>) DSL.case_((Field) this.n).when((Field) DSL.one(), (Field) DSL.one()).else_((Field) DSL.length(DSL.substringIndex(this.string, this.delimiter, this.n.minus(DSL.one()))).plus(DSL.length(this.delimiter)).plus(DSL.one()))));
                return;
            case HSQLDB:
                Field<String> rS = DSL.field(DSL.name("s"), String.class);
                Field<Integer> rN = DSL.field(DSL.name("n"), Integer.TYPE);
                Field<String> rD = DSL.field(DSL.name(DateTokenConverter.CONVERTER_KEY), String.class);
                Field<Integer> rPos = DSL.position(rS, rD);
                Field<?> rLen = DSL.length(rD);
                Field<Integer> rPosN = DSL.nullif((Field) rPos, (Field) DSL.zero());
                Field<String> rStr = DSL.substring(rS, rPosN.plus(rLen));
                Field<String> rRes = DSL.coalesce((Field) DSL.substring(rS, DSL.one(), rPosN.minus(DSL.one())), (Field<?>[]) new Field[]{rS});
                CommonTableExpression<?> s1 = DSL.name("s1").fields("s", DateTokenConverter.CONVERTER_KEY).as(DSL.select(this.string, this.delimiter));
                CommonTableExpression<?> s2 = DSL.name("s2").fields("s", DateTokenConverter.CONVERTER_KEY, "x", "n").as(DSL.select(rStr, rD, rRes, DSL.one()).from(s1).unionAll((Select) DSL.select(rStr, rD, rRes, rN.plus(DSL.one())).from(DSL.name("s2")).where(rS.isNotNull())));
                Tools.visitSubquery(ctx, DSL.withRecursive((CommonTableExpression<?>[]) new CommonTableExpression[]{s1, s2}).select(DSL.coalesce(DSL.max(DSL.field(DSL.name("x"))), DSL.inline(""))).from(s2).where(s2.field("n").eq((Field<?>) this.n)), 1);
                return;
            default:
                ctx.visit(DSL.function(Names.N_SPLIT_PART, getDataType(), (Field<?>[]) new Field[]{this.string, this.delimiter, this.n}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg1() {
        return this.string;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg2() {
        return this.delimiter;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg3() {
        return this.n;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SplitPart $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SplitPart $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SplitPart $arg3(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<String>, ? super Field<String>, ? super Field<? extends Number>, ? extends QOM.SplitPart> $constructor() {
        return (a1, a2, a3) -> {
            return new SplitPart(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.SplitPart)) {
            return super.equals(that);
        }
        QOM.SplitPart o = (QOM.SplitPart) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($delimiter(), o.$delimiter()) && StringUtils.equals($n(), o.$n());
    }
}
