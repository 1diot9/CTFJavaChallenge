package org.jooq.impl;

import java.util.List;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.RowN;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InList.class */
public final class InList<T> extends AbstractInList<T> implements QOM.InList<T> {
    static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_IN};

    @Override // org.jooq.impl.QOM.UOperator2
    public /* bridge */ /* synthetic */ Object $arg2() {
        return super.$arg2();
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public /* bridge */ /* synthetic */ Object $arg1() {
        return super.$arg1();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InList(Field<T> field, List<? extends Field<?>> values) {
        super(field, values);
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractInList
    final Function2<? super RowN, ? super RowN[], ? extends Condition> rowCondition() {
        return (v0, v1) -> {
            return v0.in(v1);
        };
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super QOM.UnmodifiableList<? extends Field<T>>, ? extends QOM.InList<T>> $constructor() {
        return (a1, a2) -> {
            return new InList(a1, a2);
        };
    }
}
