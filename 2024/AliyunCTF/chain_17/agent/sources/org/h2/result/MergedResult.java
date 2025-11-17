package org.h2.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.h2.result.SimpleResult;
import org.h2.util.Utils;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/MergedResult.class */
public final class MergedResult {
    private final ArrayList<Map<SimpleResult.Column, Value>> data = Utils.newSmallArrayList();
    private final ArrayList<SimpleResult.Column> columns = Utils.newSmallArrayList();

    public void add(ResultInterface resultInterface) {
        int visibleColumnCount = resultInterface.getVisibleColumnCount();
        if (visibleColumnCount == 0) {
            return;
        }
        SimpleResult.Column[] columnArr = new SimpleResult.Column[visibleColumnCount];
        for (int i = 0; i < visibleColumnCount; i++) {
            SimpleResult.Column column = new SimpleResult.Column(resultInterface.getAlias(i), resultInterface.getColumnName(i), resultInterface.getColumnType(i));
            columnArr[i] = column;
            if (!this.columns.contains(column)) {
                this.columns.add(column);
            }
        }
        while (resultInterface.next()) {
            if (visibleColumnCount == 1) {
                this.data.add(Collections.singletonMap(columnArr[0], resultInterface.currentRow()[0]));
            } else {
                HashMap hashMap = new HashMap();
                for (int i2 = 0; i2 < visibleColumnCount; i2++) {
                    hashMap.put(columnArr[i2], resultInterface.currentRow()[i2]);
                }
                this.data.add(hashMap);
            }
        }
    }

    public SimpleResult getResult() {
        SimpleResult simpleResult = new SimpleResult();
        Iterator<SimpleResult.Column> it = this.columns.iterator();
        while (it.hasNext()) {
            simpleResult.addColumn(it.next());
        }
        Iterator<Map<SimpleResult.Column, Value>> it2 = this.data.iterator();
        while (it2.hasNext()) {
            Map<SimpleResult.Column, Value> next = it2.next();
            Value[] valueArr = new Value[this.columns.size()];
            for (Map.Entry<SimpleResult.Column, Value> entry : next.entrySet()) {
                valueArr[this.columns.indexOf(entry.getKey())] = entry.getValue();
            }
            simpleResult.addRow(valueArr);
        }
        return simpleResult;
    }

    public String toString() {
        return this.columns + ": " + this.data.size();
    }
}
