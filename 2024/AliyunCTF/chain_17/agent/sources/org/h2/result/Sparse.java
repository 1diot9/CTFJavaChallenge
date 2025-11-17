package org.h2.result;

import org.h2.value.Value;
import org.h2.value.ValueBigint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/Sparse.class */
public final class Sparse extends DefaultRow {
    private final int columnCount;
    private final int[] map;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Sparse(int i, int i2, int[] iArr) {
        super(new Value[i2]);
        this.columnCount = i;
        this.map = iArr;
    }

    @Override // org.h2.result.DefaultRow, org.h2.result.SearchRow
    public int getColumnCount() {
        return this.columnCount;
    }

    @Override // org.h2.result.DefaultRow, org.h2.result.SearchRow
    public Value getValue(int i) {
        if (i == -1) {
            return ValueBigint.get(getKey());
        }
        int i2 = this.map[i];
        if (i2 > 0) {
            return super.getValue(i2 - 1);
        }
        return null;
    }

    @Override // org.h2.result.DefaultRow, org.h2.result.SearchRow
    public void setValue(int i, Value value) {
        if (i == -1) {
            setKey(value.getLong());
        }
        int i2 = this.map[i];
        if (i2 > 0) {
            super.setValue(i2 - 1, value);
        }
    }

    @Override // org.h2.result.DefaultRow, org.h2.result.SearchRow
    public void copyFrom(SearchRow searchRow) {
        setKey(searchRow.getKey());
        for (int i = 0; i < this.map.length; i++) {
            int i2 = this.map[i];
            if (i2 > 0) {
                super.setValue(i2 - 1, searchRow.getValue(i));
            }
        }
    }
}
