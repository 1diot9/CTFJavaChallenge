package org.jooq.impl;

import org.jooq.Context;
import org.jooq.EmbeddableRecord;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/EmbeddableTableField.class */
public final class EmbeddableTableField<R extends Record, E extends EmbeddableRecord<E>> extends AbstractField<E> implements TableField<R, E>, QOM.UNotYetImplemented {
    final Class<E> recordType;
    final boolean replacesFields;
    final Table<R> table;

    @Deprecated
    final TableField<R, ?>[] fields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EmbeddableTableField(Name name, Class<E> recordType, boolean replacesFields, Table<R> table, TableField<R, ?>[] fields) {
        super(name, new RecordDataType(Tools.row0(fields), recordType, name.last()));
        this.recordType = recordType;
        this.replacesFields = replacesFields;
        this.table = table;
        this.fields = fields;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (AbstractRowAsField.forceMultisetContent(ctx, () -> {
            return getDataType().getRow().size() > 1;
        })) {
            AbstractRowAsField.acceptMultisetContent(ctx, getDataType().getRow(), this, this::acceptDefault);
        } else if (AbstractRowAsField.forceRowContent(ctx)) {
            ctx.visit((Field<?>) ((AbstractRow) getDataType().getRow()).rf());
        } else {
            acceptDefault(ctx);
        }
    }

    private void acceptDefault(Context<?> ctx) {
        ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, true, c -> {
            c.visit(QueryPartListView.wrap(getDataType().getRow().fields()));
        });
    }

    @Override // org.jooq.TableField
    public final Table<R> getTable() {
        return this.table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public int projectionSize() {
        int result = 0;
        for (Field<?> field : ((AbstractRow) getDataType().getRow()).fields.fields) {
            result += ((AbstractField) field).projectionSize();
        }
        return result;
    }
}
