package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.XML;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLSerialize.class */
public final class XMLSerialize<T> extends AbstractField<T> implements QOM.XMLSerialize<T> {
    final boolean content;
    final Field<XML> value;
    final DataType<T> type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLSerialize(boolean content, Field<XML> value, DataType<T> type) {
        super(Names.N_XMLSERIALIZE, type);
        this.content = content;
        this.value = Tools.nullSafeNotNull(value, SQLDataType.XML);
        this.type = type;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Names.N_XMLSERIALIZE).sql('(');
        if (this.content) {
            ctx.visit(Keywords.K_CONTENT).sql(' ');
        } else {
            ctx.visit(Keywords.K_DOCUMENT).sql(' ');
        }
        ctx.visit((Field<?>) this.value).sql(' ').visit(Keywords.K_AS).sql(' ').sql(this.type.getCastTypeName(ctx.configuration())).sql(')');
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Boolean $arg1() {
        return Boolean.valueOf(this.content);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<XML> $arg2() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final DataType<T> $arg3() {
        return this.type;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.XMLSerialize<T> $arg1(Boolean newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.XMLSerialize<T> $arg2(Field<XML> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.XMLSerialize<T> $arg3(DataType<T> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Boolean, ? super Field<XML>, ? super DataType<T>, ? extends QOM.XMLSerialize<T>> $constructor() {
        return (a1, a2, a3) -> {
            return new XMLSerialize(a1.booleanValue(), a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.XMLSerialize)) {
            return super.equals(that);
        }
        QOM.XMLSerialize<?> o = (QOM.XMLSerialize) that;
        return $content() == o.$content() && StringUtils.equals($value(), o.$value()) && StringUtils.equals($type(), o.$type());
    }
}
