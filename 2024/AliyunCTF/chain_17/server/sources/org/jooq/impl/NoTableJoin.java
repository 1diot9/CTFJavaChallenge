package org.jooq.impl;

import java.util.Collection;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOnConditionStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NoTableJoin.class */
public final class NoTableJoin extends AbstractJoinTable<NoTableJoin> implements QOM.UTransient {
    final Table<?> table;

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ AbstractJoinTable using(Collection collection) {
        return using((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractJoinTable
    /* bridge */ /* synthetic */ NoTableJoin partitionBy0(Collection collection) {
        return partitionBy0((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ Table using(Collection collection) {
        return using((Collection<? extends Field<?>>) collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NoTableJoin(Table<?> table) {
        super(table.getOptions(), table.getQualifiedName());
        this.table = table;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.table);
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return this.table.getRecordType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        return ((AbstractTable) this.table).fields0();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<Record> as(Name alias) {
        return this.table.as(alias);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<Record> as(Name alias, Name... fieldAliases) {
        return this.table.as(alias, fieldAliases);
    }

    @Override // org.jooq.TableOnStep
    public final TableOnConditionStep<Record> onKey() {
        return this;
    }

    @Override // org.jooq.TableOnStep
    public final TableOnConditionStep<Record> onKey(TableField<?, ?>... keyFields) {
        return this;
    }

    @Override // org.jooq.TableOnStep
    public final TableOnConditionStep<Record> onKey(ForeignKey<?, ?> key) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.AbstractJoinTable
    final NoTableJoin partitionBy0(Collection<? extends Field<?>> fields) {
        return this;
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public final NoTableJoin on(Condition conditions) {
        return this;
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public final NoTableJoin on(Condition... conditions) {
        return this;
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public final NoTableJoin using(Collection<? extends Field<?>> fields) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnConditionStep
    /* renamed from: and */
    public final TableOnConditionStep<Record> and2(Condition c) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnConditionStep
    /* renamed from: or */
    public final TableOnConditionStep<Record> or2(Condition c) {
        return this;
    }
}
