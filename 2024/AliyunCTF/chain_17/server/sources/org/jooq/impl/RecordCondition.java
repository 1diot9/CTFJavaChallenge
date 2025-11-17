package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordCondition.class */
public final class RecordCondition extends AbstractCondition implements QOM.UEmpty {
    private final Record record;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordCondition(Record record) {
        this.record = record;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        Condition c = DSL.noCondition();
        int size = this.record.size();
        for (int i = 0; i < size; i++) {
            Object value = this.record.get(i);
            if (value != null) {
                Field f1 = this.record.field(i);
                c = c.and(f1.eq((Field) DSL.val(value, f1.getDataType())));
            }
        }
        ctx.visit(c);
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }
}
