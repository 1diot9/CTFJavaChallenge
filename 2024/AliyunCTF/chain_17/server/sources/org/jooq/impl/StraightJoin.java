package org.jooq.impl;

import java.util.Collection;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/StraightJoin.class */
public final class StraightJoin extends JoinTable<StraightJoin> implements QOM.StraightJoin<Record> {
    @Override // org.jooq.impl.JoinTable
    /* bridge */ /* synthetic */ StraightJoin construct(Table table, Collection collection, Collection collection2, Table table2, Condition condition, Collection collection3, QOM.JoinHint joinHint) {
        return construct((Table<?>) table, (Collection<? extends Field<?>>) collection, (Collection<? extends Field<?>>) collection2, (Table<?>) table2, condition, (Collection<? extends Field<?>>) collection3, joinHint);
    }

    @Override // org.jooq.impl.QOM.QualifiedJoin
    public /* bridge */ /* synthetic */ QOM.QualifiedJoin $using(Collection collection) {
        return (QOM.QualifiedJoin) super.$using((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.impl.QOM.QualifiedJoin
    public /* bridge */ /* synthetic */ QOM.QualifiedJoin $on(Condition condition) {
        return (QOM.QualifiedJoin) super.$on(condition);
    }

    @Override // org.jooq.impl.QOM.JoinTable
    public /* bridge */ /* synthetic */ QOM.JoinTable $hint(QOM.JoinHint joinHint) {
        return (QOM.JoinTable) super.$hint(joinHint);
    }

    @Override // org.jooq.impl.QOM.JoinTable
    public /* bridge */ /* synthetic */ QOM.JoinTable $table2(Table table) {
        return (QOM.JoinTable) super.$table2((Table<?>) table);
    }

    @Override // org.jooq.impl.QOM.JoinTable
    public /* bridge */ /* synthetic */ QOM.JoinTable $table1(Table table) {
        return (QOM.JoinTable) super.$table1((Table<?>) table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StraightJoin(TableLike<?> lhs, TableLike<?> rhs, QOM.JoinHint hint) {
        super(lhs, rhs, JoinType.STRAIGHT_JOIN, hint);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.JoinTable
    StraightJoin construct(Table<?> table1, Collection<? extends Field<?>> partitionBy1, Collection<? extends Field<?>> partitionBy2, Table<?> table2, Condition o, Collection<? extends Field<?>> u, QOM.JoinHint h) {
        return o != null ? new StraightJoin(table1, table2, h).on(o) : new StraightJoin(table1, table2, h).using(u);
    }
}
