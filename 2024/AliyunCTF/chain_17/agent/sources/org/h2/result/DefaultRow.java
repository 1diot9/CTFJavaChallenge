package org.h2.result;

import org.h2.value.Value;
import org.h2.value.ValueBigint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/DefaultRow.class */
public class DefaultRow extends Row {
    public static final int MEMORY_CALCULATE = -1;
    protected final Value[] data;
    private int memory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultRow(int i) {
        this.data = new Value[i];
        this.memory = -1;
    }

    public DefaultRow(Value[] valueArr) {
        this.data = valueArr;
        this.memory = -1;
    }

    public DefaultRow(Value[] valueArr, int i) {
        this.data = valueArr;
        this.memory = i;
    }

    @Override // org.h2.result.SearchRow
    public Value getValue(int i) {
        return i == -1 ? ValueBigint.get(this.key) : this.data[i];
    }

    @Override // org.h2.result.SearchRow
    public void setValue(int i, Value value) {
        if (i == -1) {
            this.key = value.getLong();
        } else {
            this.data[i] = value;
        }
    }

    @Override // org.h2.result.SearchRow
    public int getColumnCount() {
        return this.data.length;
    }

    @Override // org.h2.result.SearchRow, org.h2.value.Value
    public int getMemory() {
        if (this.memory != -1) {
            return this.memory;
        }
        int calculateMemory = calculateMemory();
        this.memory = calculateMemory;
        return calculateMemory;
    }

    @Override // org.h2.value.Value
    public String toString() {
        StringBuilder append = new StringBuilder("( /* key:").append(this.key).append(" */ ");
        int length = this.data.length;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                append.append(", ");
            }
            Value value = this.data[i];
            append.append(value == null ? "null" : value.getTraceSQL());
        }
        return append.append(')').toString();
    }

    protected int calculateMemory() {
        int length = 64 + (this.data.length * 8);
        for (Value value : this.data) {
            if (value != null) {
                length += value.getMemory();
            }
        }
        return length;
    }

    @Override // org.h2.result.Row
    public Value[] getValueList() {
        return this.data;
    }

    @Override // org.h2.result.Row
    public boolean hasSharedData(Row row) {
        return (row instanceof DefaultRow) && this.data == ((DefaultRow) row).data;
    }

    @Override // org.h2.result.SearchRow
    public void copyFrom(SearchRow searchRow) {
        setKey(searchRow.getKey());
        for (int i = 0; i < getColumnCount(); i++) {
            setValue(i, searchRow.getValue(i));
        }
    }
}
