package org.jooq.impl;

import java.util.Collection;
import java.util.Collections;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NaturalFullJoin.class */
public final class NaturalFullJoin extends JoinTable<NaturalFullJoin> implements QOM.NaturalFullJoin<Record> {
    @Override // org.jooq.impl.JoinTable
    /* bridge */ /* synthetic */ NaturalFullJoin construct(Table table, Collection collection, Collection collection2, Table table2, Condition condition, Collection collection3, QOM.JoinHint joinHint) {
        return construct((Table<?>) table, (Collection<? extends Field<?>>) collection, (Collection<? extends Field<?>>) collection2, (Table<?>) table2, condition, (Collection<? extends Field<?>>) collection3, joinHint);
    }

    @Override // org.jooq.impl.QOM.NaturalFullJoin
    public /* bridge */ /* synthetic */ QOM.NaturalFullJoin<Record> $partitionBy2(Collection collection) {
        return (QOM.NaturalFullJoin) super.$partitionBy2((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.impl.QOM.NaturalFullJoin
    public /* bridge */ /* synthetic */ QOM.NaturalFullJoin<Record> $partitionBy1(Collection collection) {
        return (QOM.NaturalFullJoin) super.$partitionBy1((Collection<? extends Field<?>>) collection);
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
    public NaturalFullJoin(TableLike<?> lhs, TableLike<?> rhs, QOM.JoinHint hint) {
        super(lhs, rhs, JoinType.NATURAL_FULL_OUTER_JOIN, hint, Collections.emptyList());
    }

    NaturalFullJoin(TableLike<?> lhs, TableLike<?> rhs, QOM.JoinHint hint, Collection<? extends Field<?>> lhsPartitionBy) {
        super(lhs, rhs, JoinType.NATURAL_FULL_OUTER_JOIN, hint, lhsPartitionBy);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.JoinTable
    NaturalFullJoin construct(Table<?> table1, Collection<? extends Field<?>> partitionBy1, Collection<? extends Field<?>> partitionBy2, Table<?> table2, Condition o, Collection<? extends Field<?>> u, QOM.JoinHint h) {
        return new NaturalFullJoin(table1, table2, h, partitionBy1).partitionBy0(partitionBy2);
    }
}
