package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Replace.class */
public final class Replace extends AbstractField<String> implements QOM.Replace {
    final Field<String> string;
    final Field<String> search;
    final Field<String> replace;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Replace(Field<String> string, Field<String> search) {
        super(Names.N_REPLACE, Tools.allNotNull(SQLDataType.VARCHAR, string, search));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.search = Tools.nullSafeNotNull(search, SQLDataType.VARCHAR);
        this.replace = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Replace(Field<String> string, Field<String> search, Field<String> replace) {
        super(Names.N_REPLACE, Tools.allNotNull(SQLDataType.VARCHAR, string, search, replace));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.search = Tools.nullSafeNotNull(search, SQLDataType.VARCHAR);
        this.replace = Tools.nullSafeNotNull(replace, SQLDataType.VARCHAR);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case FIREBIRD:
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case YUGABYTEDB:
                if (this.replace == null) {
                    ctx.visit(DSL.function(Names.N_REPLACE, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{this.string, this.search, DSL.inline("")}));
                    return;
                } else {
                    ctx.visit(DSL.function(Names.N_REPLACE, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{this.string, this.search, this.replace}));
                    return;
                }
            default:
                if (this.replace == null) {
                    ctx.visit(DSL.function(Names.N_REPLACE, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{this.string, this.search}));
                    return;
                } else {
                    ctx.visit(DSL.function(Names.N_REPLACE, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{this.string, this.search, this.replace}));
                    return;
                }
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
        return this.search;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg3() {
        return this.replace;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Replace $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Replace $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Replace $arg3(Field<String> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<String>, ? super Field<String>, ? super Field<String>, ? extends QOM.Replace> $constructor() {
        return (a1, a2, a3) -> {
            return new Replace(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Replace)) {
            return super.equals(that);
        }
        QOM.Replace o = (QOM.Replace) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($search(), o.$search()) && StringUtils.equals($replace(), o.$replace());
    }
}
