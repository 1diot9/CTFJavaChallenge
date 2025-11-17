package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.JSON;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONSet.class */
public final class JSONSet extends AbstractField<JSON> implements QOM.JSONSet {
    final Field<JSON> field;
    final Field<String> path;
    final Field<?> value;

    /* renamed from: org.jooq.impl.JSONSet$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONSet$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONSet(Field<JSON> field, Field<String> path, Field<?> value) {
        super(Names.N_JSON_SET, Tools.allNotNull(SQLDataType.JSON, field, path, value));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSON);
        this.path = Tools.nullSafeNotNull(path, SQLDataType.VARCHAR);
        this.value = Tools.nullSafeNoConvertValNotNull(value, SQLDataType.VARCHAR);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(DSL.function(Names.N_JSON_SET, SQLDataType.JSON, (Field<?>[]) new Field[]{this.field, this.path, JSONEntryImpl.jsonCast(ctx, this.value, true)}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<JSON> $arg1() {
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
    public final QOM.JSONSet $arg1(Field<JSON> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.JSONSet $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.JSONSet $arg3(Field<?> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<JSON>, ? super Field<String>, ? super Field<?>, ? extends QOM.JSONSet> $constructor() {
        return (a1, a2, a3) -> {
            return new JSONSet(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONSet)) {
            return super.equals(that);
        }
        QOM.JSONSet o = (QOM.JSONSet) that;
        return StringUtils.equals($field(), o.$field()) && StringUtils.equals($path(), o.$path()) && StringUtils.equals($value(), o.$value());
    }
}
