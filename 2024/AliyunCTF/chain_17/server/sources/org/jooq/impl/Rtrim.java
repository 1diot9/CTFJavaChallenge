package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Rtrim.class */
public final class Rtrim extends AbstractField<String> implements QOM.Rtrim {
    final Field<String> string;
    final Field<String> characters;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rtrim(Field<String> string) {
        super(Names.N_RTRIM, Tools.allNotNull(SQLDataType.VARCHAR, string));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.characters = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rtrim(Field<String> string, Field<String> characters) {
        super(Names.N_RTRIM, Tools.allNotNull(SQLDataType.VARCHAR, string, characters));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.characters = Tools.nullSafeNotNull(characters, SQLDataType.VARCHAR);
    }

    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.characters == null) {
            switch (ctx.family()) {
                case FIREBIRD:
                    ctx.visit(Names.N_TRIM).sql('(').visit(Keywords.K_TRAILING).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.string).sql(')');
                    return;
                default:
                    ctx.visit(DSL.function(Names.N_RTRIM, getDataType(), this.string));
                    return;
            }
        }
        switch (ctx.family()) {
            case SQLITE:
                ctx.visit(DSL.function(Names.N_RTRIM, getDataType(), (Field<?>[]) new Field[]{this.string, this.characters}));
                return;
            default:
                ctx.visit(Names.N_TRIM).sql('(').visit(Keywords.K_TRAILING).sql(' ').visit((Field<?>) this.characters).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.string).sql(')');
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg1() {
        return this.string;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg2() {
        return this.characters;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Rtrim $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Rtrim $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<String>, ? super Field<String>, ? extends QOM.Rtrim> $constructor() {
        return (a1, a2) -> {
            return new Rtrim(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Rtrim)) {
            return super.equals(that);
        }
        QOM.Rtrim o = (QOM.Rtrim) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($characters(), o.$characters());
    }
}
