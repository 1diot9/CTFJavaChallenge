package org.h2.mvstore.db;

import org.h2.engine.Database;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.LongDataType;
import org.h2.result.ResultExternal;
import org.h2.result.RowFactory;
import org.h2.value.Value;
import org.h2.value.ValueRow;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVPlainTempResult.class */
public class MVPlainTempResult extends MVTempResult {
    private final MVMap<Long, ValueRow> map;
    private long counter;
    private Cursor<Long, ValueRow> cursor;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !MVPlainTempResult.class.desiredAssertionStatus();
    }

    private MVPlainTempResult(MVPlainTempResult mVPlainTempResult) {
        super(mVPlainTempResult);
        this.map = mVPlainTempResult.map;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MVPlainTempResult(Database database, Expression[] expressionArr, int i, int i2) {
        super(database, expressionArr, i, i2);
        ValueDataType valueDataType = new ValueDataType(database, new int[i2]);
        valueDataType.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(database, database.getCompareMode(), database, expressionArr, null, false));
        this.map = this.store.openMap("tmp", new MVMap.Builder().keyType((DataType) LongDataType.INSTANCE).valueType((DataType) valueDataType).singleWriter());
    }

    @Override // org.h2.result.ResultExternal
    public int addRow(Value[] valueArr) {
        if (!$assertionsDisabled && this.parent != null) {
            throw new AssertionError();
        }
        MVMap<Long, ValueRow> mVMap = this.map;
        long j = this.counter;
        this.counter = j + 1;
        mVMap.append(Long.valueOf(j), ValueRow.get(valueArr));
        int i = this.rowCount + 1;
        this.rowCount = i;
        return i;
    }

    @Override // org.h2.result.ResultExternal
    public boolean contains(Value[] valueArr) {
        throw DbException.getUnsupportedException("contains()");
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
        return new MVPlainTempResult(this);
    }

    @Override // org.h2.result.ResultExternal
    public Value[] next() {
        if (this.cursor == null) {
            this.cursor = this.map.cursor(null);
        }
        if (!this.cursor.hasNext()) {
            return null;
        }
        this.cursor.next();
        return this.cursor.getValue().getList();
    }

    @Override // org.h2.result.ResultExternal
    public int removeRow(Value[] valueArr) {
        throw DbException.getUnsupportedException("removeRow()");
    }

    @Override // org.h2.result.ResultExternal
    public void reset() {
        this.cursor = null;
    }
}
