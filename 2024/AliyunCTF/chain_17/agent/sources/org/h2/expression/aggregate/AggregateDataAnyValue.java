package org.h2.expression.aggregate;

import java.util.ArrayList;
import java.util.Random;
import org.h2.engine.SessionLocal;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataAnyValue.class */
final class AggregateDataAnyValue extends AggregateData {
    private static final int MAX_VALUES = 256;
    ArrayList<Value> values = new ArrayList<>();
    private long filter = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public void add(SessionLocal sessionLocal, Value value) {
        if (value == ValueNull.INSTANCE) {
            return;
        }
        long j = this.filter;
        if (j == Long.MIN_VALUE || (sessionLocal.getRandom().nextLong() | j) == j) {
            this.values.add(value);
            if (this.values.size() == 256) {
                compact(sessionLocal);
            }
        }
    }

    private void compact(SessionLocal sessionLocal) {
        this.filter <<= 1;
        Random random = sessionLocal.getRandom();
        int i = 0;
        for (int i2 = 0; i2 < 128; i2++) {
            int i3 = i;
            if (random.nextBoolean()) {
                i3++;
            }
            this.values.set(i2, this.values.get(i3));
            i += 2;
        }
        this.values.subList(128, 256).clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        int size = this.values.size();
        if (size == 0) {
            return ValueNull.INSTANCE;
        }
        return this.values.get(sessionLocal.getRandom().nextInt(size));
    }
}
