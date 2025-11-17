package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.XML;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLPi.class */
public final class XMLPi extends AbstractField<XML> implements QOM.XMLPi {
    final Name target;
    final Field<?> content;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLPi(Name target) {
        super(Names.N_XMLPI, Tools.allNotNull(SQLDataType.XML));
        this.target = target;
        this.content = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLPi(Name target, Field<?> content) {
        super(Names.N_XMLPI, Tools.allNotNull(SQLDataType.XML, content));
        this.target = target;
        this.content = Tools.nullSafeNotNull(content, SQLDataType.OTHER);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Names.N_XMLPI).sql('(').visit(Keywords.K_NAME).sql(' ').visit(this.target);
        if (this.content != null) {
            ctx.sql(", ").visit(this.content);
        }
        ctx.sql(')');
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Name $arg1() {
        return this.target;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<?> $arg2() {
        return this.content;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.XMLPi $arg1(Name newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.XMLPi $arg2(Field<?> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Name, ? super Field<?>, ? extends QOM.XMLPi> $constructor() {
        return (a1, a2) -> {
            return new XMLPi(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.XMLPi)) {
            return super.equals(that);
        }
        QOM.XMLPi o = (QOM.XMLPi) that;
        return StringUtils.equals($target(), o.$target()) && StringUtils.equals($content(), o.$content());
    }
}
