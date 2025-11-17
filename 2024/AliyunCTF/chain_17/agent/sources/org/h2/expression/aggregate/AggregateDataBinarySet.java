package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataBinarySet.class */
abstract class AggregateDataBinarySet extends AggregateData {
    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void add(SessionLocal sessionLocal, Value value, Value value2);

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public final void add(SessionLocal sessionLocal, Value value) {
        throw DbException.getInternalError();
    }
}
