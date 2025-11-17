package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Excluded.class */
public final class Excluded<T> extends AbstractField<T> implements QOM.Excluded<T> {
    final Field<T> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Excluded(Field<T> field) {
        super(Names.N_EXCLUDED, Tools.allNotNull((DataType) Tools.dataType(field), (Field<?>) field));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.OTHER);
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Name name;
        switch (ctx.family()) {
            case MARIADB:
                ctx.visit(Keywords.K_VALUES).sql('(').qualify(false, c -> {
                    c.visit((Field<?>) this.field);
                }).sql(')');
                return;
            case MYSQL:
                ctx.visit(DSL.name("t")).sql('.').qualify(false, c2 -> {
                    c2.visit((Field<?>) this.field);
                });
                return;
            default:
                if (ctx.data(Tools.ExtendedDataKey.DATA_INSERT_ON_DUPLICATE_KEY_UPDATE) != null) {
                    name = DSL.name("t");
                } else {
                    name = Names.N_EXCLUDED;
                }
                ctx.visit(name).sql('.').qualify(false, c3 -> {
                    c3.visit((Field<?>) this.field);
                });
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Excluded<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.Excluded<T>> $constructor() {
        return a1 -> {
            return new Excluded(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Excluded) {
            QOM.Excluded<?> o = (QOM.Excluded) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}
