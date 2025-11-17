package org.h2.mvstore.db;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Database;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StatefulDataType;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.store.DataHandler;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/RowDataType.class */
public final class RowDataType extends BasicDataType<SearchRow> implements StatefulDataType<Database> {
    private final ValueDataType valueDataType;
    private final int[] sortTypes;
    private final int[] indexes;
    private final int columnCount;
    private final boolean storeKeys;
    private static final Factory FACTORY;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !RowDataType.class.desiredAssertionStatus();
        FACTORY = new Factory();
    }

    public RowDataType(CastDataProvider castDataProvider, CompareMode compareMode, DataHandler dataHandler, int[] iArr, int[] iArr2, int i, boolean z) {
        this.valueDataType = new ValueDataType(castDataProvider, compareMode, dataHandler, iArr);
        this.sortTypes = iArr;
        this.indexes = iArr2;
        this.columnCount = i;
        this.storeKeys = z;
        if (!$assertionsDisabled && iArr2 != null && iArr.length != iArr2.length) {
            throw new AssertionError();
        }
    }

    public int[] getIndexes() {
        return this.indexes;
    }

    public RowFactory getRowFactory() {
        return this.valueDataType.getRowFactory();
    }

    public void setRowFactory(RowFactory rowFactory) {
        this.valueDataType.setRowFactory(rowFactory);
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public boolean isStoreKeys() {
        return this.storeKeys;
    }

    @Override // org.h2.mvstore.type.DataType
    public SearchRow[] createStorage(int i) {
        return new SearchRow[i];
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(SearchRow searchRow, SearchRow searchRow2) {
        if (searchRow == searchRow2) {
            return 0;
        }
        if (this.indexes == null) {
            int columnCount = searchRow.getColumnCount();
            if (!$assertionsDisabled && columnCount != searchRow2.getColumnCount()) {
                throw new AssertionError(columnCount + " != " + searchRow2.getColumnCount());
            }
            for (int i = 0; i < columnCount; i++) {
                int compareValues = this.valueDataType.compareValues(searchRow.getValue(i), searchRow2.getValue(i), this.sortTypes[i]);
                if (compareValues != 0) {
                    return compareValues;
                }
            }
            return 0;
        }
        return compareSearchRows(searchRow, searchRow2);
    }

    private int compareSearchRows(SearchRow searchRow, SearchRow searchRow2) {
        for (int i = 0; i < this.indexes.length; i++) {
            int i2 = this.indexes[i];
            Value value = searchRow.getValue(i2);
            Value value2 = searchRow2.getValue(i2);
            if (value == null || value2 == null) {
                break;
            }
            int compareValues = this.valueDataType.compareValues(value, value2, this.sortTypes[i]);
            if (compareValues != 0) {
                return compareValues;
            }
        }
        long key = searchRow.getKey();
        long key2 = searchRow2.getKey();
        if (key == SearchRow.MATCH_ALL_ROW_KEY || key2 == SearchRow.MATCH_ALL_ROW_KEY) {
            return 0;
        }
        return Long.compare(key, key2);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int binarySearch(SearchRow searchRow, Object obj, int i, int i2) {
        return binarySearch(searchRow, (SearchRow[]) obj, i, i2);
    }

    public int binarySearch(SearchRow searchRow, SearchRow[] searchRowArr, int i, int i2) {
        int i3 = 0;
        int i4 = i - 1;
        int i5 = i2 - 1;
        if (i5 < 0 || i5 > i4) {
            i5 = i4 >>> 1;
        }
        while (i3 <= i4) {
            int compareSearchRows = compareSearchRows(searchRow, searchRowArr[i5]);
            if (compareSearchRows > 0) {
                i3 = i5 + 1;
            } else if (compareSearchRows < 0) {
                i4 = i5 - 1;
            } else {
                return i5;
            }
            i5 = (i3 + i4) >>> 1;
        }
        return -(i3 + 1);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(SearchRow searchRow) {
        return searchRow.getMemory();
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public SearchRow read(ByteBuffer byteBuffer) {
        RowFactory rowFactory = this.valueDataType.getRowFactory();
        SearchRow createRow = rowFactory.createRow();
        if (this.storeKeys) {
            createRow.setKey(DataUtils.readVarLong(byteBuffer));
        }
        TypeInfo[] columnTypes = rowFactory.getColumnTypes();
        if (this.indexes == null) {
            int columnCount = createRow.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                createRow.setValue(i, this.valueDataType.readValue(byteBuffer, columnTypes != null ? columnTypes[i] : null));
            }
        } else {
            for (int i2 : this.indexes) {
                createRow.setValue(i2, this.valueDataType.readValue(byteBuffer, columnTypes != null ? columnTypes[i2] : null));
            }
        }
        return createRow;
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, SearchRow searchRow) {
        if (this.storeKeys) {
            writeBuffer.putVarLong(searchRow.getKey());
        }
        if (this.indexes == null) {
            int columnCount = searchRow.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                this.valueDataType.write(writeBuffer, searchRow.getValue(i));
            }
            return;
        }
        for (int i2 : this.indexes) {
            this.valueDataType.write(writeBuffer, searchRow.getValue(i2));
        }
    }

    @Override // org.h2.mvstore.type.BasicDataType, java.util.Comparator
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != RowDataType.class) {
            return false;
        }
        RowDataType rowDataType = (RowDataType) obj;
        return this.columnCount == rowDataType.columnCount && Arrays.equals(this.indexes, rowDataType.indexes) && Arrays.equals(this.sortTypes, rowDataType.sortTypes) && this.valueDataType.equals(rowDataType.valueDataType);
    }

    @Override // org.h2.mvstore.type.BasicDataType
    public int hashCode() {
        return (((((((super.hashCode() * 31) + this.columnCount) * 31) + Arrays.hashCode(this.indexes)) * 31) + Arrays.hashCode(this.sortTypes)) * 31) + this.valueDataType.hashCode();
    }

    @Override // org.h2.mvstore.type.StatefulDataType
    public void save(WriteBuffer writeBuffer, MetaType<Database> metaType) {
        writeBuffer.putVarInt(this.columnCount);
        writeIntArray(writeBuffer, this.sortTypes);
        writeIntArray(writeBuffer, this.indexes);
        writeBuffer.put(this.storeKeys ? (byte) 1 : (byte) 0);
    }

    private static void writeIntArray(WriteBuffer writeBuffer, int[] iArr) {
        if (iArr == null) {
            writeBuffer.putVarInt(0);
            return;
        }
        writeBuffer.putVarInt(iArr.length + 1);
        for (int i : iArr) {
            writeBuffer.putVarInt(i);
        }
    }

    @Override // org.h2.mvstore.type.StatefulDataType
    /* renamed from: getFactory */
    public StatefulDataType.Factory<Database> getFactory2() {
        return FACTORY;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/RowDataType$Factory.class */
    public static final class Factory implements StatefulDataType.Factory<Database> {
        @Override // org.h2.mvstore.type.StatefulDataType.Factory
        public RowDataType create(ByteBuffer byteBuffer, MetaType<Database> metaType, Database database) {
            int readVarInt = DataUtils.readVarInt(byteBuffer);
            return RowFactory.getDefaultRowFactory().createRowFactory(database, database == null ? CompareMode.getInstance(null, 0) : database.getCompareMode(), database, readIntArray(byteBuffer), readIntArray(byteBuffer), null, readVarInt, byteBuffer.get() != 0).getRowDataType();
        }

        private static int[] readIntArray(ByteBuffer byteBuffer) {
            int readVarInt = DataUtils.readVarInt(byteBuffer) - 1;
            if (readVarInt < 0) {
                return null;
            }
            int[] iArr = new int[readVarInt];
            for (int i = 0; i < iArr.length; i++) {
                iArr[i] = DataUtils.readVarInt(byteBuffer);
            }
            return iArr;
        }
    }
}
