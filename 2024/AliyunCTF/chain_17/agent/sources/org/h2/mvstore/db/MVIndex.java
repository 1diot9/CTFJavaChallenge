package org.h2.mvstore.db;

import java.util.List;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.mvstore.MVMap;
import org.h2.result.Row;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVIndex.class */
public abstract class MVIndex<K, V> extends Index {
    public abstract void addRowsToBuffer(List<Row> list, String str);

    public abstract void addBufferedRows(List<String> list);

    public abstract MVMap<K, VersionedValue<V>> getMVMap();

    /* JADX INFO: Access modifiers changed from: protected */
    public MVIndex(Table table, int i, String str, IndexColumn[] indexColumnArr, int i2, IndexType indexType) {
        super(table, i, str, indexColumnArr, i2, indexType);
    }
}
