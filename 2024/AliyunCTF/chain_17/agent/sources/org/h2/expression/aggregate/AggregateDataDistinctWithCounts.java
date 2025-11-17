package org.h2.expression.aggregate;

import java.util.TreeMap;
import org.h2.engine.SessionLocal;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataDistinctWithCounts.class */
public final class AggregateDataDistinctWithCounts extends AggregateData {
    private final boolean ignoreNulls;
    private final int maxDistinctCount;
    private TreeMap<Value, LongDataCounter> values;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataDistinctWithCounts(boolean z, int i) {
        this.ignoreNulls = z;
        this.maxDistinctCount = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public void add(SessionLocal sessionLocal, Value value) {
        if (this.ignoreNulls && value == ValueNull.INSTANCE) {
            return;
        }
        if (this.values == null) {
            this.values = new TreeMap<>(sessionLocal.getDatabase().getCompareMode());
        }
        LongDataCounter longDataCounter = this.values.get(value);
        if (longDataCounter == null) {
            if (this.values.size() >= this.maxDistinctCount) {
                return;
            }
            longDataCounter = new LongDataCounter();
            this.values.put(value, longDataCounter);
        }
        longDataCounter.count++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TreeMap<Value, LongDataCounter> getValues() {
        return this.values;
    }
}
