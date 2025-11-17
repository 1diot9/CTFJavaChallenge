package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.XML;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLForest.class */
public final class XMLForest extends AbstractField<XML> implements QOM.XMLForest {
    final QueryPartListView<? extends Field<?>> fields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLForest(Collection<? extends Field<?>> fields) {
        super(Names.N_XMLFOREST, Tools.allNotNull(SQLDataType.XML));
        this.fields = new QueryPartList(fields);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.data(Tools.BooleanDataKey.DATA_AS_REQUIRED, true, c -> {
            c.visit(Names.N_XMLFOREST).sql('(').declareFields(true, x -> {
                x.visit(new SelectFieldList(this.fields));
            }).sql(')');
        });
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends Field<?>> $arg1() {
        return QOM.unmodifiable((List) this.fields);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.XMLForest $arg1(QOM.UnmodifiableList<? extends Field<?>> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends Field<?>>, ? extends QOM.XMLForest> $constructor() {
        return a1 -> {
            return new XMLForest(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.XMLForest) {
            QOM.XMLForest o = (QOM.XMLForest) that;
            return StringUtils.equals($fields(), o.$fields());
        }
        return super.equals(that);
    }
}
