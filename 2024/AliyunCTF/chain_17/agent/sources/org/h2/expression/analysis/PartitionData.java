package org.h2.expression.analysis;

import java.util.HashMap;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/PartitionData.class */
public final class PartitionData {
    private Object data;
    private Value result;
    private HashMap<Integer, Value> orderedResult;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PartitionData(Object obj) {
        this.data = obj;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getData() {
        return this.data;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Value getResult() {
        return this.result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setResult(Value value) {
        this.result = value;
        this.data = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashMap<Integer, Value> getOrderedResult() {
        return this.orderedResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOrderedResult(HashMap<Integer, Value> hashMap) {
        this.orderedResult = hashMap;
        this.data = null;
    }
}
