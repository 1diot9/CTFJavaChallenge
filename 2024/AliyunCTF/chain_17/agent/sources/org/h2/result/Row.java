package org.h2.result;

import java.util.Arrays;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/Row.class */
public abstract class Row extends SearchRow {
    public abstract Value[] getValueList();

    public static Row get(Value[] valueArr, int i) {
        return new DefaultRow(valueArr, i);
    }

    public static Row get(Value[] valueArr, int i, long j) {
        DefaultRow defaultRow = new DefaultRow(valueArr, i);
        defaultRow.setKey(j);
        return defaultRow;
    }

    public boolean hasSameValues(Row row) {
        return Arrays.equals(getValueList(), row.getValueList());
    }

    public boolean hasSharedData(Row row) {
        return false;
    }
}
