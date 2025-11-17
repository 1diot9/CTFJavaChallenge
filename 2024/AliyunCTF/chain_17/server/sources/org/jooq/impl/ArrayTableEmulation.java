package org.jooq.impl;

import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayTableEmulation.class */
public final class ArrayTableEmulation extends AbstractQueryPart implements QOM.UTransient {
    private final Object[] array;
    private final DataType<?> type;
    private final Name fieldAlias;
    private transient Select<?> table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayTableEmulation(Object[] array, Name[] fieldAliases) {
        if (Tools.isEmpty(fieldAliases)) {
            this.fieldAlias = Names.N_COLUMN_VALUE;
        } else if (fieldAliases.length == 1) {
            this.fieldAlias = fieldAliases[0];
        } else {
            throw new IllegalArgumentException("Array table simulations can only have a single field alias");
        }
        this.array = array;
        this.type = Tools.componentDataType(array);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Tools.visitSubquery(ctx, table(), 1, true);
    }

    private final Select<?> table() {
        Select<?> unionAll;
        if (this.table == null) {
            Select<?> select = null;
            for (Object element : this.array) {
                Field<?> val = DSL.val(element, this.type);
                Select<Record> subselect = DSL.select(val.as(this.fieldAlias)).select(new SelectFieldOrAsterisk[0]);
                if (select == null) {
                    unionAll = subselect;
                } else {
                    unionAll = select.unionAll(subselect);
                }
                select = unionAll;
            }
            if (select == null) {
                select = DSL.select(DSL.one().as(this.fieldAlias)).select(new SelectFieldOrAsterisk[0]).where((Condition) DSL.falseCondition());
            }
            this.table = select;
        }
        return this.table;
    }
}
