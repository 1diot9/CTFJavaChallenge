package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataCount.class */
final class AggregateDataCount extends AggregateData {
    private final boolean all;
    private long count;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataCount(boolean z) {
        this.all = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public void add(SessionLocal sessionLocal, Value value) {
        if (this.all || value != ValueNull.INSTANCE) {
            this.count++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        return ValueBigint.get(this.count);
    }
}
