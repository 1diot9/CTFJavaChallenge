package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CountTable.class */
final class CountTable extends AbstractAggregateFunction<Integer> implements QOM.CountTable {
    private final Table<?> table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CountTable(Table<?> table, boolean distinct) {
        super(distinct, "count", SQLDataType.INTEGER, (Field<?>[]) new Field[]{DSL.field(DSL.name(table.getName()))});
        this.table = table;
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                super.accept(ctx);
                return;
            default:
                UniqueKey<?> pk = this.table.getPrimaryKey();
                if (pk != null) {
                    ctx.visit((Field<?>) new DefaultAggregateFunction(this.distinct, "count", SQLDataType.INTEGER, this.table.fields(pk.getFieldsArray())));
                    return;
                } else {
                    super.accept(ctx);
                    return;
                }
        }
    }

    @Override // org.jooq.impl.QOM.CountTable
    public final Table<?> $table() {
        return this.table;
    }
}
