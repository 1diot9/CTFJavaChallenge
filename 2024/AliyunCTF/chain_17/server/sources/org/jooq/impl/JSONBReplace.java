package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.JSONB;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONBReplace.class */
public final class JSONBReplace extends AbstractField<JSONB> implements QOM.JSONBReplace {
    final Field<JSONB> field;
    final Field<String> path;
    final Field<?> value;

    /* renamed from: org.jooq.impl.JSONBReplace$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONBReplace$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONBReplace(Field<JSONB> field, Field<String> path, Field<?> value) {
        super(Names.N_JSONB_REPLACE, Tools.allNotNull(SQLDataType.JSONB, field, path, value));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSONB);
        this.path = Tools.nullSafeNotNull(path, SQLDataType.VARCHAR);
        this.value = Tools.nullSafeNoConvertValNotNull(value, SQLDataType.VARCHAR);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(DSL.function(Names.N_JSON_REPLACE, SQLDataType.JSONB, (Field<?>[]) new Field[]{this.field, this.path, JSONEntryImpl.jsonCast(ctx, this.value, true)}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<JSONB> $arg1() {
        return this.field;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg2() {
        return this.path;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<?> $arg3() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.JSONBReplace $arg1(Field<JSONB> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.JSONBReplace $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.JSONBReplace $arg3(Field<?> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<JSONB>, ? super Field<String>, ? super Field<?>, ? extends QOM.JSONBReplace> $constructor() {
        return (a1, a2, a3) -> {
            return new JSONBReplace(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONBReplace)) {
            return super.equals(that);
        }
        QOM.JSONBReplace o = (QOM.JSONBReplace) that;
        return StringUtils.equals($field(), o.$field()) && StringUtils.equals($path(), o.$path()) && StringUtils.equals($value(), o.$value());
    }
}
