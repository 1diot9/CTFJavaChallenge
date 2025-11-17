package org.h2.mvstore.db;

import java.util.Arrays;
import java.util.BitSet;
import org.h2.engine.Database;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.LongDataType;
import org.h2.result.ResultExternal;
import org.h2.result.RowFactory;
import org.h2.result.SortOrder;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSortedTempResult.class */
public class MVSortedTempResult extends MVTempResult {
    private final boolean distinct;
    private final int[] distinctIndexes;
    private final int[] indexes;
    private final MVMap<ValueRow, Long> map;
    private MVMap<ValueRow, Value> index;
    private ValueDataType orderedDistinctOnType;
    private Cursor<ValueRow, Long> cursor;
    private Value[] current;
    private long valueCount;
    static final /* synthetic */ boolean $assertionsDisabled;

    /*  JADX ERROR: Failed to decode insn: 0x0024: MOVE_MULTI, method: org.h2.mvstore.db.MVSortedTempResult.next():org.h2.value.Value[]
        java.lang.ArrayIndexOutOfBoundsException: arraycopy: source index -1 out of bounds for object array[6]
        	at java.base/java.lang.System.arraycopy(Native Method)
        	at jadx.plugins.input.java.data.code.StackState.insert(StackState.java:49)
        	at jadx.plugins.input.java.data.code.CodeDecodeState.insert(CodeDecodeState.java:118)
        	at jadx.plugins.input.java.data.code.JavaInsnsRegister.dup2x1(JavaInsnsRegister.java:313)
        	at jadx.plugins.input.java.data.code.JavaInsnData.decode(JavaInsnData.java:46)
        	at jadx.core.dex.instructions.InsnDecoder.lambda$process$0(InsnDecoder.java:54)
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:81)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:50)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:156)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:443)
        	at jadx.core.ProcessClass.process(ProcessClass.java:70)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
        	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
        	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
        */
    @Override // org.h2.result.ResultExternal
    public org.h2.value.Value[] next() {
        /*
            r6 = this;
            r0 = r6
            org.h2.mvstore.Cursor<org.h2.value.ValueRow, java.lang.Long> r0 = r0.cursor
            if (r0 != 0) goto L1d
            r0 = r6
            r1 = r6
            org.h2.mvstore.MVMap<org.h2.value.ValueRow, java.lang.Long> r1 = r1.map
            r2 = 0
            org.h2.mvstore.Cursor r1 = r1.cursor(r2)
            r0.cursor = r1
            r0 = r6
            r1 = 0
            r0.current = r1
            r0 = r6
            r1 = 0
            r0.valueCount = r1
            r0 = r6
            r1 = r0
            long r1 = r1.valueCount
            r2 = 1
            long r1 = r1 - r2
            // decode failed: arraycopy: source index -1 out of bounds for object array[6]
            r0.valueCount = r1
            r0 = 0
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 <= 0) goto L32
            r-1 = r6
            org.h2.value.Value[] r-1 = r-1.current
            return r-1
            r-1 = r6
            org.h2.mvstore.Cursor<org.h2.value.ValueRow, java.lang.Long> r-1 = r-1.cursor
            r-1.hasNext()
            if (r-1 != 0) goto L43
            r-1 = r6
            r0 = 0
            r-1.current = r0
            r-1 = 0
            return r-1
            r-1 = r6
            r0 = r6
            r1 = r6
            org.h2.mvstore.Cursor<org.h2.value.ValueRow, java.lang.Long> r1 = r1.cursor
            java.lang.Object r1 = r1.next()
            org.h2.value.ValueRow r1 = (org.h2.value.ValueRow) r1
            org.h2.value.Value[] r1 = r1.getList()
            org.h2.value.Value[] r0 = r0.getValue(r1)
            r-1.current = r0
            r-1 = r6
            r0 = r6
            org.h2.mvstore.Cursor<org.h2.value.ValueRow, java.lang.Long> r0 = r0.cursor
            java.lang.Object r0 = r0.getValue()
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            r-1.valueCount = r0
            r-1 = r6
            org.h2.value.Value[] r-1 = r-1.current
            return r-1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.db.MVSortedTempResult.next():org.h2.value.Value[]");
    }

    static {
        $assertionsDisabled = !MVSortedTempResult.class.desiredAssertionStatus();
    }

    private MVSortedTempResult(MVSortedTempResult mVSortedTempResult) {
        super(mVSortedTempResult);
        this.distinct = mVSortedTempResult.distinct;
        this.distinctIndexes = mVSortedTempResult.distinctIndexes;
        this.indexes = mVSortedTempResult.indexes;
        this.map = mVSortedTempResult.map;
        this.rowCount = mVSortedTempResult.rowCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.h2.mvstore.db.NullValueDataType] */
    public MVSortedTempResult(Database database, Expression[] expressionArr, boolean z, int[] iArr, int i, int i2, SortOrder sortOrder) {
        super(database, expressionArr, i, i2);
        int[] iArr2;
        int i3;
        TypeInfo[] typeInfoArr;
        ValueDataType valueDataType;
        this.distinct = z;
        this.distinctIndexes = iArr;
        int[] iArr3 = new int[i2];
        if (sortOrder != null) {
            iArr2 = new int[i2];
            int[] queryColumnIndexes = sortOrder.getQueryColumnIndexes();
            int length = queryColumnIndexes.length;
            BitSet bitSet = new BitSet();
            for (int i4 = 0; i4 < length; i4++) {
                int i5 = queryColumnIndexes[i4];
                if (!$assertionsDisabled && bitSet.get(i5)) {
                    throw new AssertionError();
                }
                bitSet.set(i5);
                iArr2[i4] = i5;
                iArr3[i4] = sortOrder.getSortTypes()[i4];
            }
            int i6 = 0;
            for (int i7 = length; i7 < i2; i7++) {
                int nextClearBit = bitSet.nextClearBit(i6);
                iArr2[i7] = nextClearBit;
                i6 = nextClearBit + 1;
            }
            int i8 = 0;
            while (true) {
                if (i8 < i2) {
                    if (iArr2[i8] != i8) {
                        break;
                    } else {
                        i8++;
                    }
                } else {
                    iArr2 = null;
                    break;
                }
            }
        } else {
            iArr2 = null;
        }
        this.indexes = iArr2;
        ValueDataType valueDataType2 = new ValueDataType(database, SortOrder.addNullOrdering(database, iArr3));
        if (iArr2 != null) {
            int length2 = iArr2.length;
            TypeInfo[] typeInfoArr2 = new TypeInfo[length2];
            for (int i9 = 0; i9 < length2; i9++) {
                typeInfoArr2[i9] = expressionArr[iArr2[i9]].getType();
            }
            valueDataType2.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(database, database.getCompareMode(), database, typeInfoArr2, null, false));
        } else {
            valueDataType2.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(database, database.getCompareMode(), database, expressionArr, null, false));
        }
        this.map = this.store.openMap("tmp", new MVMap.Builder().keyType((DataType) valueDataType2).valueType((DataType) LongDataType.INSTANCE));
        if ((z && i2 != i) || iArr != null) {
            if (iArr != null) {
                i3 = iArr.length;
                typeInfoArr = new TypeInfo[i3];
                for (int i10 = 0; i10 < i3; i10++) {
                    typeInfoArr[i10] = expressionArr[iArr[i10]].getType();
                }
            } else {
                i3 = i;
                typeInfoArr = new TypeInfo[i3];
                for (int i11 = 0; i11 < i3; i11++) {
                    typeInfoArr[i11] = expressionArr[i11].getType();
                }
            }
            ValueDataType valueDataType3 = new ValueDataType(database, new int[i3]);
            valueDataType3.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(database, database.getCompareMode(), database, typeInfoArr, null, false));
            if (iArr != null && sortOrder != null) {
                this.orderedDistinctOnType = valueDataType2;
                valueDataType = valueDataType2;
            } else {
                valueDataType = NullValueDataType.INSTANCE;
            }
            this.index = this.store.openMap("idx", new MVMap.Builder().keyType((DataType) valueDataType3).valueType((DataType) valueDataType));
        }
    }

    @Override // org.h2.result.ResultExternal
    public int addRow(Value[] valueArr) {
        if (!$assertionsDisabled && this.parent != null) {
            throw new AssertionError();
        }
        ValueRow key = getKey(valueArr);
        if (this.distinct || this.distinctIndexes != null) {
            if (this.distinctIndexes != null) {
                int length = this.distinctIndexes.length;
                Value[] valueArr2 = new Value[length];
                for (int i = 0; i < length; i++) {
                    valueArr2[i] = valueArr[this.distinctIndexes[i]];
                }
                ValueRow valueRow = ValueRow.get(valueArr2);
                if (this.orderedDistinctOnType == null) {
                    if (this.index.putIfAbsent(valueRow, ValueNull.INSTANCE) != null) {
                        return this.rowCount;
                    }
                } else {
                    ValueRow valueRow2 = (ValueRow) this.index.get(valueRow);
                    if (valueRow2 == null) {
                        this.index.put(valueRow, key);
                    } else if (this.orderedDistinctOnType.compare((Value) valueRow2, (Value) key) > 0) {
                        this.map.remove(valueRow2);
                        this.rowCount--;
                        this.index.put(valueRow, key);
                    } else {
                        return this.rowCount;
                    }
                }
            } else if (this.visibleColumnCount != this.resultColumnCount) {
                if (this.index.putIfAbsent(ValueRow.get((Value[]) Arrays.copyOf(valueArr, this.visibleColumnCount)), ValueNull.INSTANCE) != null) {
                    return this.rowCount;
                }
            }
            if (this.map.putIfAbsent(key, 1L) == null) {
                this.rowCount++;
            }
        } else {
            Long putIfAbsent = this.map.putIfAbsent(key, 1L);
            if (putIfAbsent != null) {
                this.map.put(key, Long.valueOf(putIfAbsent.longValue() + 1));
            }
            this.rowCount++;
        }
        return this.rowCount;
    }

    @Override // org.h2.result.ResultExternal
    public boolean contains(Value[] valueArr) {
        if (this.parent != null) {
            return this.parent.contains(valueArr);
        }
        if (!$assertionsDisabled && !this.distinct) {
            throw new AssertionError();
        }
        if (this.visibleColumnCount != this.resultColumnCount) {
            return this.index.containsKey(ValueRow.get(valueArr));
        }
        return this.map.containsKey(getKey(valueArr));
    }

    @Override // org.h2.result.ResultExternal
    public synchronized ResultExternal createShallowCopy() {
        if (this.parent != null) {
            return this.parent.createShallowCopy();
        }
        if (this.closed) {
            return null;
        }
        this.childCount++;
        return new MVSortedTempResult(this);
    }

    private ValueRow getKey(Value[] valueArr) {
        if (this.indexes != null) {
            Value[] valueArr2 = new Value[this.indexes.length];
            for (int i = 0; i < this.indexes.length; i++) {
                valueArr2[i] = valueArr[this.indexes[i]];
            }
            valueArr = valueArr2;
        }
        return ValueRow.get(valueArr);
    }

    private Value[] getValue(Value[] valueArr) {
        if (this.indexes != null) {
            Value[] valueArr2 = new Value[this.indexes.length];
            for (int i = 0; i < this.indexes.length; i++) {
                valueArr2[this.indexes[i]] = valueArr[i];
            }
            valueArr = valueArr2;
        }
        return valueArr;
    }

    @Override // org.h2.result.ResultExternal
    public int removeRow(Value[] valueArr) {
        if (!$assertionsDisabled && (this.parent != null || !this.distinct)) {
            throw new AssertionError();
        }
        if (this.visibleColumnCount != this.resultColumnCount) {
            throw DbException.getUnsupportedException("removeRow()");
        }
        if (this.map.remove(getKey(valueArr)) != null) {
            this.rowCount--;
        }
        return this.rowCount;
    }

    @Override // org.h2.result.ResultExternal
    public void reset() {
        this.cursor = null;
        this.current = null;
        this.valueCount = 0L;
    }
}
