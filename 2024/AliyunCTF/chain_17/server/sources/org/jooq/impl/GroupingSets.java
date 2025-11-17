package org.jooq.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/GroupingSets.class */
public final class GroupingSets extends AbstractField<Object> implements QOM.GroupingSets {
    private final QueryPartList<QueryPartList<Field<?>>> fieldSets;

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public GroupingSets(Collection<? extends Field<?>>... fieldSets) {
        super(Names.N_GROUPING_SETS, SQLDataType.OTHER);
        this.fieldSets = new QueryPartList<>();
        for (Collection<? extends Field<?>> fieldSet : fieldSets) {
            this.fieldSets.add((QueryPartList<QueryPartList<Field<?>>>) new QueryPartList<>(fieldSet));
        }
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        QueryPartList<WrappedList> arg = new QueryPartList<>();
        Iterator<QueryPartList<Field<?>>> it = this.fieldSets.iterator();
        while (it.hasNext()) {
            Collection<? extends Field<?>> fieldsSet = it.next();
            arg.add((QueryPartList<WrappedList>) new WrappedList(new QueryPartList(fieldsSet)));
        }
        ctx.visit(Keywords.K_GROUPING_SETS).sql(" (").visit(arg).sql(')');
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends QOM.UnmodifiableList<? extends FieldOrRow>> $arg1() {
        return QOM.unmodifiable((List) this.fieldSets);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends QOM.UnmodifiableList<? extends FieldOrRow>>, ? extends QOM.GroupingSets> $constructor() {
        return l -> {
            return new GroupingSets((Collection[]) l.toArray(Tools.EMPTY_COLLECTION));
        };
    }
}
