package org.h2.result;

import org.h2.engine.CastDataProvider;
import org.h2.mvstore.db.RowDataType;
import org.h2.store.DataHandler;
import org.h2.table.IndexColumn;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/RowFactory.class */
public abstract class RowFactory {
    public abstract Row createRow(Value[] valueArr, int i);

    public abstract SearchRow createRow();

    public abstract RowDataType getRowDataType();

    public abstract int[] getIndexes();

    public abstract TypeInfo[] getColumnTypes();

    public abstract int getColumnCount();

    public abstract boolean getStoreKeys();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/RowFactory$Holder.class */
    public static final class Holder {
        static final RowFactory EFFECTIVE = DefaultRowFactory.INSTANCE;

        private Holder() {
        }
    }

    public static DefaultRowFactory getDefaultRowFactory() {
        return DefaultRowFactory.INSTANCE;
    }

    public static RowFactory getRowFactory() {
        return Holder.EFFECTIVE;
    }

    public RowFactory createRowFactory(CastDataProvider castDataProvider, CompareMode compareMode, DataHandler dataHandler, Typed[] typedArr, IndexColumn[] indexColumnArr, boolean z) {
        return this;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/RowFactory$DefaultRowFactory.class */
    public static final class DefaultRowFactory extends RowFactory {
        private final RowDataType dataType;
        private final int columnCount;
        private final int[] indexes;
        private TypeInfo[] columnTypes;
        private final int[] map;
        public static final DefaultRowFactory INSTANCE = new DefaultRowFactory();

        DefaultRowFactory() {
            this(new RowDataType(null, CompareMode.getInstance(null, 0), null, null, null, 0, true), 0, null, null);
        }

        private DefaultRowFactory(RowDataType rowDataType, int i, int[] iArr, TypeInfo[] typeInfoArr) {
            this.dataType = rowDataType;
            this.columnCount = i;
            this.indexes = iArr;
            if (iArr == null) {
                this.map = null;
            } else {
                this.map = new int[i];
                int i2 = 0;
                int length = iArr.length;
                while (i2 < length) {
                    int[] iArr2 = this.map;
                    int i3 = iArr[i2];
                    i2++;
                    iArr2[i3] = i2;
                }
            }
            this.columnTypes = typeInfoArr;
        }

        @Override // org.h2.result.RowFactory
        public RowFactory createRowFactory(CastDataProvider castDataProvider, CompareMode compareMode, DataHandler dataHandler, Typed[] typedArr, IndexColumn[] indexColumnArr, boolean z) {
            int[] iArr = null;
            int[] iArr2 = null;
            TypeInfo[] typeInfoArr = null;
            int i = 0;
            if (typedArr != null) {
                i = typedArr.length;
                if (indexColumnArr == null) {
                    iArr2 = new int[i];
                    for (int i2 = 0; i2 < i; i2++) {
                        iArr2[i2] = 0;
                    }
                } else {
                    int length = indexColumnArr.length;
                    iArr = new int[length];
                    iArr2 = new int[length];
                    for (int i3 = 0; i3 < length; i3++) {
                        IndexColumn indexColumn = indexColumnArr[i3];
                        iArr[i3] = indexColumn.column.getColumnId();
                        iArr2[i3] = indexColumn.sortType;
                    }
                }
                typeInfoArr = new TypeInfo[i];
                for (int i4 = 0; i4 < i; i4++) {
                    typeInfoArr[i4] = typedArr[i4].getType();
                }
            }
            return createRowFactory(castDataProvider, compareMode, dataHandler, iArr2, iArr, typeInfoArr, i, z);
        }

        public RowFactory createRowFactory(CastDataProvider castDataProvider, CompareMode compareMode, DataHandler dataHandler, int[] iArr, int[] iArr2, TypeInfo[] typeInfoArr, int i, boolean z) {
            RowDataType rowDataType = new RowDataType(castDataProvider, compareMode, dataHandler, iArr, iArr2, i, z);
            DefaultRowFactory defaultRowFactory = new DefaultRowFactory(rowDataType, i, iArr2, typeInfoArr);
            rowDataType.setRowFactory(defaultRowFactory);
            return defaultRowFactory;
        }

        @Override // org.h2.result.RowFactory
        public Row createRow(Value[] valueArr, int i) {
            return new DefaultRow(valueArr, i);
        }

        @Override // org.h2.result.RowFactory
        public SearchRow createRow() {
            if (this.indexes == null) {
                return new DefaultRow(this.columnCount);
            }
            if (this.indexes.length == 1) {
                return new SimpleRowValue(this.columnCount, this.indexes[0]);
            }
            return new Sparse(this.columnCount, this.indexes.length, this.map);
        }

        @Override // org.h2.result.RowFactory
        public RowDataType getRowDataType() {
            return this.dataType;
        }

        @Override // org.h2.result.RowFactory
        public int[] getIndexes() {
            return this.indexes;
        }

        @Override // org.h2.result.RowFactory
        public TypeInfo[] getColumnTypes() {
            return this.columnTypes;
        }

        @Override // org.h2.result.RowFactory
        public int getColumnCount() {
            return this.columnCount;
        }

        @Override // org.h2.result.RowFactory
        public boolean getStoreKeys() {
            return this.dataType.isStoreKeys();
        }
    }
}
