package org.h2.expression.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataCollecting.class */
public final class AggregateDataCollecting extends AggregateData implements Iterable<Value> {
    private final boolean distinct;
    private final boolean orderedWithOrder;
    private final NullCollectionMode nullCollectionMode;
    Collection<Value> values;
    private Value shared;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataCollecting$NullCollectionMode.class */
    public enum NullCollectionMode {
        IGNORED,
        EXCLUDED,
        USED_OR_IMPOSSIBLE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataCollecting(boolean z, boolean z2, NullCollectionMode nullCollectionMode) {
        this.distinct = z;
        this.orderedWithOrder = z2;
        this.nullCollectionMode = nullCollectionMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v22, types: [java.util.Comparator] */
    @Override // org.h2.expression.aggregate.AggregateData
    public void add(SessionLocal sessionLocal, Value value) {
        if (this.nullCollectionMode == NullCollectionMode.IGNORED && isNull(value)) {
            return;
        }
        Collection<Value> collection = this.values;
        if (collection == null) {
            if (this.distinct) {
                CompareMode compareMode = sessionLocal.getDatabase().getCompareMode();
                if (this.orderedWithOrder) {
                    compareMode = Comparator.comparing(value2 -> {
                        return ((ValueRow) value2).getList()[0];
                    }, compareMode);
                }
                collection = new TreeSet(compareMode);
            } else {
                collection = new ArrayList();
            }
            this.values = collection;
        }
        if (this.nullCollectionMode == NullCollectionMode.EXCLUDED && isNull(value)) {
            return;
        }
        collection.add(value);
    }

    private boolean isNull(Value value) {
        return (this.orderedWithOrder ? ((ValueRow) value).getList()[0] : value) == ValueNull.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCount() {
        if (this.values != null) {
            return this.values.size();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Value[] getArray() {
        Collection<Value> collection = this.values;
        if (collection == null) {
            return null;
        }
        return (Value[]) collection.toArray(Value.EMPTY_VALUES);
    }

    @Override // java.lang.Iterable
    public Iterator<Value> iterator() {
        return this.values != null ? this.values.iterator() : Collections.emptyIterator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSharedArgument(Value value) {
        if (this.shared == null) {
            this.shared = value;
        } else if (!this.shared.equals(value)) {
            throw DbException.get(ErrorCode.INVALID_VALUE_2, "Inverse distribution function argument", this.shared.getTraceSQL() + "<>" + value.getTraceSQL());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Value getSharedArgument() {
        return this.shared;
    }
}
