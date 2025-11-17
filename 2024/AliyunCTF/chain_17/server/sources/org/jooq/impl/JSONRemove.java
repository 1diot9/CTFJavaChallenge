package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.JSON;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONRemove.class */
public final class JSONRemove extends AbstractField<JSON> implements QOM.JSONRemove {
    final Field<JSON> field;
    final Field<String> path;

    /* renamed from: org.jooq.impl.JSONRemove$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONRemove$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONRemove(Field<JSON> field, Field<String> path) {
        super(Names.N_JSON_REMOVE, Tools.allNotNull(SQLDataType.JSON, field, path));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSON);
        this.path = Tools.nullSafeNotNull(path, SQLDataType.VARCHAR);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(DSL.function(Names.N_JSON_REMOVE, SQLDataType.JSON, (Field<?>[]) new Field[]{this.field, this.path}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<JSON> $arg1() {
        return this.field;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg2() {
        return this.path;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONRemove $arg1(Field<JSON> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONRemove $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<JSON>, ? super Field<String>, ? extends QOM.JSONRemove> $constructor() {
        return (a1, a2) -> {
            return new JSONRemove(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONRemove)) {
            return super.equals(that);
        }
        QOM.JSONRemove o = (QOM.JSONRemove) that;
        return StringUtils.equals($field(), o.$field()) && StringUtils.equals($path(), o.$path());
    }
}
