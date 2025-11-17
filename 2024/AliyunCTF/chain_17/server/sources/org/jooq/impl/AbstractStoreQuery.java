package org.jooq.impl;

import java.util.Map;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.Record;
import org.jooq.StoreQuery;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractStoreQuery.class */
abstract class AbstractStoreQuery<R extends Record, K extends FieldOrRow, V extends FieldOrRowOrSelect> extends AbstractDMLQuery<R> implements StoreQuery<R> {
    /* renamed from: getValues */
    protected abstract Map<K, V> getValues2();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractStoreQuery(Configuration configuration, WithImpl with, Table<R> table) {
        super(configuration, with, table);
    }

    @Override // org.jooq.StoreQuery
    public final void setRecord(R record) {
        for (int i = 0; i < record.size(); i++) {
            if (record.changed(i)) {
                addValue((Field<Field>) record.field(i), (Field) record.get(i));
            }
        }
    }

    @Override // org.jooq.StoreQuery
    public final <T> void addValue(Field<T> field, T value) {
        addValue((Field<int>) field, -1, (int) value);
    }

    @Override // org.jooq.StoreQuery
    public final <T> void addValue(Field<T> field, Field<T> value) {
        addValue((Field) field, -1, (Field) value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <T> void addValue(Field<T> field, int index, T value) {
        if (field == null) {
            if (index >= 0) {
                addValue((Field<UnknownField>) new UnknownField(index), (UnknownField) value);
                return;
            } else {
                addValue((Field<UnknownField>) new UnknownField(getValues2().size()), (UnknownField) value);
                return;
            }
        }
        getValues2().put(field, Tools.field(value, field));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <T> void addValue(Field<T> field, int index, Field<T> value) {
        if (field == null) {
            if (index >= 0) {
                addValue((Field) new UnknownField(index), (Field) value);
                return;
            } else {
                addValue((Field) new UnknownField(getValues2().size()), (Field) value);
                return;
            }
        }
        getValues2().put(field, Tools.field(value, field));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractStoreQuery$UnknownField.class */
    public static class UnknownField<T> extends AbstractField<T> implements QOM.UEmpty {
        private final int index;

        UnknownField(int index) {
            super(DSL.name("unknown field " + index), SQLDataType.OTHER);
            this.index = index;
        }

        @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
        public void accept(Context<?> ctx) {
            ctx.visit(getUnqualifiedName());
        }

        @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
        public int hashCode() {
            return this.index;
        }

        @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
        public boolean equals(Object that) {
            if (!(that instanceof UnknownField)) {
                return false;
            }
            UnknownField<?> f = (UnknownField) that;
            return this.index == f.index;
        }
    }
}
