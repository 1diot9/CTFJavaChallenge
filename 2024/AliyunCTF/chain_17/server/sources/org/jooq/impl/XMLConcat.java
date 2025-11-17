package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.XML;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLConcat.class */
public final class XMLConcat extends AbstractField<XML> implements QOM.XMLConcat {
    final QueryPartListView<? extends Field<?>> args;

    /* renamed from: org.jooq.impl.XMLConcat$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLConcat$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLConcat(Collection<? extends Field<?>> args) {
        super(Names.N_XMLCONCAT, Tools.allNotNull(SQLDataType.XML));
        this.args = new QueryPartList(args);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Names.N_XMLCONCAT).sql('(').visit(QueryPartCollectionView.wrap(this.args)).sql(')');
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends Field<?>> $arg1() {
        return QOM.unmodifiable((List) this.args);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.XMLConcat $arg1(QOM.UnmodifiableList<? extends Field<?>> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends Field<?>>, ? extends QOM.XMLConcat> $constructor() {
        return a1 -> {
            return new XMLConcat(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.XMLConcat) {
            QOM.XMLConcat o = (QOM.XMLConcat) that;
            return StringUtils.equals($args(), o.$args());
        }
        return super.equals(that);
    }
}
