package org.jooq.impl;

import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NoTable.class */
public final class NoTable extends AbstractTable<Record> implements QOM.UEmptyTable<Record> {
    static final NoTable INSTANCE = new NoTable();

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(new Dual());
    }

    private NoTable() {
        super(TableOptions.expression(), Names.N_DUAL);
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return Record.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        return new FieldsImpl<>((SelectField<?>[]) new SelectField[0]);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<Record> as(Name alias) {
        return this;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<Record> as(Name alias, Name... fieldAliases) {
        return this;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<Record> where(Condition condition) {
        return this;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<Record> withOrdinality() {
        return this;
    }

    @Override // org.jooq.impl.AbstractTable
    final Table<Record> hintedTable(String keywords, String... indexes) {
        return this;
    }
}
