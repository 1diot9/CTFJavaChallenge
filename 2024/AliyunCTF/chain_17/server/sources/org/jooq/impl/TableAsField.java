package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableAsField.class */
public final class TableAsField<R extends Record> extends AbstractRowAsField<R> implements QOM.TableAsField<R>, ScopeMappableWrapper<TableAsField<R>, Table<R>> {
    final Table<R> table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableAsField(Table<R> table) {
        this(table, table.getQualifiedName());
    }

    TableAsField(Table<R> table, Name as) {
        super(as, new RecordDataType(((AbstractTable) table).fieldsRow(), table.getRecordType(), table.getName()));
        this.table = table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractRowAsField
    public final Table<R> fields0() {
        return this.table;
    }

    @Override // org.jooq.impl.AbstractRowAsField
    final Class<R> getRecordType() {
        return this.table.getRecordType();
    }

    @Override // org.jooq.impl.AbstractRowAsField
    final void acceptDefault(Context<?> ctx) {
        if (RowAsField.NO_NATIVE_SUPPORT.contains(ctx.dialect())) {
            ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, true, c -> {
                c.visit(new SelectFieldList(emulatedFields(ctx.configuration()).fields.fields));
            });
        } else {
            ctx.visit((Field<?>) new RowAsField(this.table.fieldsRow(), getQualifiedName()));
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
    public Field<R> as(Name alias) {
        return new TableAsField(this.table, alias);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public final Field<?> $aliased() {
        return new TableAsField(this.table);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public final Name $alias() {
        return getQualifiedName();
    }

    @Override // org.jooq.impl.QOM.TableAsField
    public final Table<R> $table() {
        return this.table;
    }

    @Override // org.jooq.impl.ScopeMappableWrapper
    public final TableAsField<R> wrap(Table<R> wrapped) {
        return new TableAsField<>(wrapped, getQualifiedName());
    }
}
