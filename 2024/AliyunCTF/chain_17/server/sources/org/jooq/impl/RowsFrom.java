package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowsFrom.class */
final class RowsFrom extends AbstractTable<Record> implements QOM.RowsFrom {
    private final TableList tables;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowsFrom(Table<?>... tables) {
        super(TableOptions.expression(), Names.N_ROWSFROM);
        this.tables = new TableList((List<? extends Table<?>>) Arrays.asList(tables));
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return RecordImplN.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        List<Field<?>> fields = new ArrayList<>();
        Iterator<T> it = this.tables.iterator();
        while (it.hasNext()) {
            Table<?> table = (Table) it.next();
            for (Field<?> field : table.fields()) {
                fields.add(DSL.field(DSL.name(field.getName()), field.getDataType()));
            }
        }
        return new FieldsImpl<>(fields);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Keywords.K_ROWS_FROM).sql(" (").declareTables(true, c -> {
            c.visit(this.tables);
        }).sql(')');
    }

    @Override // org.jooq.impl.QOM.RowsFrom
    public final QOM.UnmodifiableList<? extends Table<?>> $tables() {
        return QOM.unmodifiable((List) this.tables);
    }
}
