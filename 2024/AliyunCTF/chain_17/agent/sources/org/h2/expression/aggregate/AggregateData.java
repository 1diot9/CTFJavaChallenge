package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateData.class */
abstract class AggregateData {
    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void add(SessionLocal sessionLocal, Value value);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Value getValue(SessionLocal sessionLocal);
}
