package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.XML;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLComment.class */
public final class XMLComment extends AbstractField<XML> implements QOM.XMLComment {
    final Field<String> comment;

    /* renamed from: org.jooq.impl.XMLComment$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLComment$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLComment(Field<String> comment) {
        super(Names.N_XMLCOMMENT, Tools.allNotNull(SQLDataType.XML, comment));
        this.comment = Tools.nullSafeNotNull(comment, SQLDataType.VARCHAR);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Names.N_XMLCOMMENT).sql('(').visit((Field<?>) this.comment).sql(')');
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<String> $arg1() {
        return this.comment;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.XMLComment $arg1(Field<String> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<String>, ? extends QOM.XMLComment> $constructor() {
        return a1 -> {
            return new XMLComment(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.XMLComment) {
            QOM.XMLComment o = (QOM.XMLComment) that;
            return StringUtils.equals($comment(), o.$comment());
        }
        return super.equals(that);
    }
}
