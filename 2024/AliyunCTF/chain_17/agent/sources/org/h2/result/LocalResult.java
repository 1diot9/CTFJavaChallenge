package org.h2.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVTempResult;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueLob;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/LocalResult.class */
public class LocalResult implements ResultInterface, ResultTarget {
    private int maxMemoryRows;
    private final SessionLocal session;
    private int visibleColumnCount;
    private int resultColumnCount;
    private Expression[] expressions;
    private boolean forDataChangeDeltaTable;
    private long rowId;
    private long rowCount;
    private ArrayList<Value[]> rows;
    private SortOrder sort;
    private TreeMap<ValueRow, Value[]> distinctRows;
    private Value[] currentRow;
    private long offset;
    private long limit;
    private boolean fetchPercent;
    private SortOrder withTiesSortOrder;
    private boolean limitsWereApplied;
    private ResultExternal external;
    private boolean distinct;
    private int[] distinctIndexes;
    private boolean closed;
    private boolean containsLobs;
    private Boolean containsNull;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !LocalResult.class.desiredAssertionStatus();
    }

    public static LocalResult forTable(SessionLocal sessionLocal, Table table) {
        Column[] columns = table.getColumns();
        int length = columns.length;
        Expression[] expressionArr = new Expression[length + 1];
        Database database = sessionLocal.getDatabase();
        for (int i = 0; i < length; i++) {
            expressionArr[i] = new ExpressionColumn(database, columns[i]);
        }
        Column rowIdColumn = table.getRowIdColumn();
        expressionArr[length] = rowIdColumn != null ? new ExpressionColumn(database, rowIdColumn) : new ExpressionColumn(database, null, table.getName());
        return new LocalResult(sessionLocal, expressionArr, length, length + 1);
    }

    public LocalResult() {
        this(null);
    }

    private LocalResult(SessionLocal sessionLocal) {
        this.limit = -1L;
        this.session = sessionLocal;
    }

    public LocalResult(SessionLocal sessionLocal, Expression[] expressionArr, int i, int i2) {
        this.limit = -1L;
        this.session = sessionLocal;
        if (sessionLocal == null) {
            this.maxMemoryRows = Integer.MAX_VALUE;
        } else {
            Database database = sessionLocal.getDatabase();
            if (database.isPersistent() && !database.isReadOnly()) {
                this.maxMemoryRows = sessionLocal.getDatabase().getMaxMemoryRows();
            } else {
                this.maxMemoryRows = Integer.MAX_VALUE;
            }
        }
        this.rows = Utils.newSmallArrayList();
        this.visibleColumnCount = i;
        this.resultColumnCount = i2;
        this.rowId = -1L;
        this.expressions = expressionArr;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isLazy() {
        return false;
    }

    public void setMaxMemoryRows(int i) {
        this.maxMemoryRows = i;
    }

    public void setForDataChangeDeltaTable() {
        this.forDataChangeDeltaTable = true;
    }

    @Override // org.h2.result.ResultInterface
    public LocalResult createShallowCopy(Session session) {
        if ((this.external == null && (this.rows == null || this.rows.size() < this.rowCount)) || this.containsLobs) {
            return null;
        }
        ResultExternal resultExternal = null;
        if (this.external != null) {
            resultExternal = this.external.createShallowCopy();
            if (resultExternal == null) {
                return null;
            }
        }
        LocalResult localResult = new LocalResult((SessionLocal) session);
        localResult.maxMemoryRows = this.maxMemoryRows;
        localResult.visibleColumnCount = this.visibleColumnCount;
        localResult.resultColumnCount = this.resultColumnCount;
        localResult.expressions = this.expressions;
        localResult.rowId = -1L;
        localResult.rowCount = this.rowCount;
        localResult.rows = this.rows;
        localResult.sort = this.sort;
        localResult.distinctRows = this.distinctRows;
        localResult.distinct = this.distinct;
        localResult.distinctIndexes = this.distinctIndexes;
        localResult.currentRow = null;
        localResult.offset = 0L;
        localResult.limit = -1L;
        localResult.external = resultExternal;
        localResult.containsNull = this.containsNull;
        return localResult;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sort = sortOrder;
    }

    public void setDistinct() {
        if (!$assertionsDisabled && this.distinctIndexes != null) {
            throw new AssertionError();
        }
        this.distinct = true;
        this.distinctRows = new TreeMap<>(this.session.getDatabase().getCompareMode());
    }

    public void setDistinct(int[] iArr) {
        if (!$assertionsDisabled && this.distinct) {
            throw new AssertionError();
        }
        this.distinctIndexes = iArr;
        this.distinctRows = new TreeMap<>(this.session.getDatabase().getCompareMode());
    }

    private boolean isAnyDistinct() {
        return this.distinct || this.distinctIndexes != null;
    }

    public boolean containsDistinct(Value[] valueArr) {
        if (!$assertionsDisabled && valueArr.length != this.visibleColumnCount) {
            throw new AssertionError();
        }
        if (this.external != null) {
            return this.external.contains(valueArr);
        }
        if (this.distinctRows == null) {
            this.distinctRows = new TreeMap<>(this.session.getDatabase().getCompareMode());
            Iterator<Value[]> it = this.rows.iterator();
            while (it.hasNext()) {
                ValueRow distinctRow = getDistinctRow(it.next());
                this.distinctRows.put(distinctRow, distinctRow.getList());
            }
        }
        return this.distinctRows.get(ValueRow.get(valueArr)) != null;
    }

    public boolean containsNull() {
        Boolean bool = this.containsNull;
        if (bool == null) {
            bool = false;
            reset();
            loop0: while (true) {
                if (!next()) {
                    break;
                }
                Value[] valueArr = this.currentRow;
                for (int i = 0; i < this.visibleColumnCount; i++) {
                    if (valueArr[i].containsNull()) {
                        bool = true;
                        break loop0;
                    }
                }
            }
            reset();
            this.containsNull = bool;
        }
        return bool.booleanValue();
    }

    public void removeDistinct(Value[] valueArr) {
        if (!this.distinct) {
            throw DbException.getInternalError();
        }
        if (!$assertionsDisabled && valueArr.length != this.visibleColumnCount) {
            throw new AssertionError();
        }
        if (this.distinctRows != null) {
            this.distinctRows.remove(ValueRow.get(valueArr));
            this.rowCount = this.distinctRows.size();
        } else {
            this.rowCount = this.external.removeRow(valueArr);
        }
    }

    @Override // org.h2.result.ResultInterface
    public void reset() {
        this.rowId = -1L;
        this.currentRow = null;
        if (this.external != null) {
            this.external.reset();
        }
    }

    public Row currentRowForTable() {
        int i = this.visibleColumnCount;
        Value[] valueArr = this.currentRow;
        Row createRow = this.session.getDatabase().getRowFactory().createRow((Value[]) Arrays.copyOf(valueArr, i), -1);
        createRow.setKey(valueArr[i].getLong());
        return createRow;
    }

    @Override // org.h2.result.ResultInterface
    public Value[] currentRow() {
        return this.currentRow;
    }

    @Override // org.h2.result.ResultInterface
    public boolean next() {
        if (!this.closed && this.rowId < this.rowCount) {
            this.rowId++;
            if (this.rowId < this.rowCount) {
                if (this.external != null) {
                    this.currentRow = this.external.next();
                    return true;
                }
                this.currentRow = this.rows.get((int) this.rowId);
                return true;
            }
            this.currentRow = null;
            return false;
        }
        return false;
    }

    @Override // org.h2.result.ResultInterface
    public long getRowId() {
        return this.rowId;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isAfterLast() {
        return this.rowId >= this.rowCount;
    }

    private void cloneLobs(Value[] valueArr) {
        for (int i = 0; i < valueArr.length; i++) {
            Value value = valueArr[i];
            if (value instanceof ValueLob) {
                if (this.forDataChangeDeltaTable) {
                    this.containsLobs = true;
                } else {
                    ValueLob copyToResult = ((ValueLob) value).copyToResult();
                    if (copyToResult != value) {
                        this.containsLobs = true;
                        valueArr[i] = this.session.addTemporaryLob(copyToResult);
                    }
                }
            }
        }
    }

    private ValueRow getDistinctRow(Value[] valueArr) {
        if (this.distinctIndexes != null) {
            int length = this.distinctIndexes.length;
            Value[] valueArr2 = new Value[length];
            for (int i = 0; i < length; i++) {
                valueArr2[i] = valueArr[this.distinctIndexes[i]];
            }
            valueArr = valueArr2;
        } else if (valueArr.length > this.visibleColumnCount) {
            valueArr = (Value[]) Arrays.copyOf(valueArr, this.visibleColumnCount);
        }
        return ValueRow.get(valueArr);
    }

    private void createExternalResult() {
        this.external = MVTempResult.of(this.session.getDatabase(), this.expressions, this.distinct, this.distinctIndexes, this.visibleColumnCount, this.resultColumnCount, this.sort);
    }

    public void addRowForTable(Row row) {
        int i = this.visibleColumnCount;
        Value[] valueArr = new Value[i + 1];
        for (int i2 = 0; i2 < i; i2++) {
            valueArr[i2] = row.getValue(i2);
        }
        valueArr[i] = ValueBigint.get(row.getKey());
        addRowInternal(valueArr);
    }

    @Override // org.h2.result.ResultTarget
    public void addRow(Value... valueArr) {
        if (!$assertionsDisabled && valueArr.length != this.resultColumnCount) {
            throw new AssertionError();
        }
        cloneLobs(valueArr);
        addRowInternal(valueArr);
    }

    private void addRowInternal(Value... valueArr) {
        if (isAnyDistinct()) {
            if (this.distinctRows != null) {
                ValueRow distinctRow = getDistinctRow(valueArr);
                Value[] valueArr2 = this.distinctRows.get(distinctRow);
                if (valueArr2 == null || (this.sort != null && this.sort.compare(valueArr2, valueArr) > 0)) {
                    this.distinctRows.put(distinctRow, valueArr);
                }
                this.rowCount = this.distinctRows.size();
                if (this.rowCount > this.maxMemoryRows) {
                    createExternalResult();
                    this.rowCount = this.external.addRows(this.distinctRows.values());
                    this.distinctRows = null;
                    return;
                }
                return;
            }
            this.rowCount = this.external.addRow(valueArr);
            return;
        }
        this.rows.add(valueArr);
        this.rowCount++;
        if (this.rows.size() > this.maxMemoryRows) {
            addRowsToDisk();
        }
    }

    private void addRowsToDisk() {
        if (this.external == null) {
            createExternalResult();
        }
        this.rowCount = this.external.addRows(this.rows);
        this.rows.clear();
    }

    @Override // org.h2.result.ResultInterface
    public int getVisibleColumnCount() {
        return this.visibleColumnCount;
    }

    public void done() {
        if (this.external != null) {
            addRowsToDisk();
        } else {
            if (isAnyDistinct()) {
                this.rows = new ArrayList<>(this.distinctRows.values());
            }
            if (this.sort != null && this.limit != 0 && !this.limitsWereApplied) {
                boolean z = this.limit > 0 && this.withTiesSortOrder == null;
                if (this.offset > 0 || z) {
                    int size = this.rows.size();
                    if (this.offset < size) {
                        int i = (int) this.offset;
                        if (z && this.limit < size - i) {
                            size = i + ((int) this.limit);
                        }
                        this.sort.sort(this.rows, i, size);
                    }
                } else {
                    this.sort.sort(this.rows);
                }
            }
        }
        applyOffsetAndLimit();
        reset();
    }

    private void applyOffsetAndLimit() {
        long j;
        if (this.limitsWereApplied) {
            return;
        }
        long max = Math.max(this.offset, 0L);
        long j2 = this.limit;
        if ((max == 0 && j2 < 0 && !this.fetchPercent) || this.rowCount == 0) {
            return;
        }
        if (this.fetchPercent) {
            if (j2 < 0 || j2 > 100) {
                throw DbException.getInvalidValueException("FETCH PERCENT", Long.valueOf(j2));
            }
            j2 = ((j2 * this.rowCount) + 99) / 100;
        }
        boolean z = max >= this.rowCount || j2 == 0;
        if (!z) {
            long j3 = this.rowCount - max;
            j = j2 < 0 ? j3 : Math.min(j3, j2);
            if (max == 0 && j3 <= j) {
                return;
            }
        } else {
            j = 0;
        }
        this.distinctRows = null;
        this.rowCount = j;
        if (this.external == null) {
            if (z) {
                this.rows.clear();
                return;
            }
            int i = (int) (max + j);
            if (this.withTiesSortOrder != null) {
                Value[] valueArr = this.rows.get(i - 1);
                while (i < this.rows.size() && this.withTiesSortOrder.compare(valueArr, this.rows.get(i)) == 0) {
                    i++;
                    this.rowCount++;
                }
            }
            if (max != 0 || i != this.rows.size()) {
                this.rows = new ArrayList<>(this.rows.subList((int) max, i));
                return;
            }
            return;
        }
        if (z) {
            this.external.close();
            this.external = null;
        } else {
            trimExternal(max, j);
        }
    }

    private void trimExternal(long j, long j2) {
        ResultExternal resultExternal = this.external;
        this.external = null;
        resultExternal.reset();
        while (true) {
            long j3 = j - 1;
            j = j3;
            if (j3 < 0) {
                break;
            } else {
                resultExternal.next();
            }
        }
        Value[] valueArr = null;
        while (true) {
            long j4 = j2 - 1;
            j2 = j4;
            if (j4 < 0) {
                break;
            }
            valueArr = resultExternal.next();
            this.rows.add(valueArr);
            if (this.rows.size() > this.maxMemoryRows) {
                addRowsToDisk();
            }
        }
        if (this.withTiesSortOrder != null && valueArr != null) {
            Value[] valueArr2 = valueArr;
            while (true) {
                Value[] next = resultExternal.next();
                if (next == null || this.withTiesSortOrder.compare(valueArr2, next) != 0) {
                    break;
                }
                this.rows.add(next);
                this.rowCount++;
                if (this.rows.size() > this.maxMemoryRows) {
                    addRowsToDisk();
                }
            }
        }
        if (this.external != null) {
            addRowsToDisk();
        }
        resultExternal.close();
    }

    @Override // org.h2.result.ResultInterface
    public long getRowCount() {
        return this.rowCount;
    }

    @Override // org.h2.result.ResultTarget
    public void limitsWereApplied() {
        this.limitsWereApplied = true;
    }

    @Override // org.h2.result.ResultInterface
    public boolean hasNext() {
        return !this.closed && this.rowId < this.rowCount - 1;
    }

    public void setLimit(long j) {
        this.limit = j;
    }

    public void setFetchPercent(boolean z) {
        this.fetchPercent = z;
    }

    public void setWithTies(SortOrder sortOrder) {
        if (!$assertionsDisabled && this.sort != null && this.sort != sortOrder) {
            throw new AssertionError();
        }
        this.withTiesSortOrder = sortOrder;
    }

    @Override // org.h2.result.ResultInterface
    public boolean needToClose() {
        return this.external != null;
    }

    @Override // org.h2.result.ResultInterface, java.lang.AutoCloseable
    public void close() {
        if (this.external != null) {
            this.external.close();
            this.external = null;
            this.closed = true;
        }
    }

    @Override // org.h2.result.ResultInterface
    public String getAlias(int i) {
        return this.expressions[i].getAlias(this.session, i);
    }

    @Override // org.h2.result.ResultInterface
    public String getTableName(int i) {
        return this.expressions[i].getTableName();
    }

    @Override // org.h2.result.ResultInterface
    public String getSchemaName(int i) {
        return this.expressions[i].getSchemaName();
    }

    @Override // org.h2.result.ResultInterface
    public String getColumnName(int i) {
        return this.expressions[i].getColumnName(this.session, i);
    }

    @Override // org.h2.result.ResultInterface
    public TypeInfo getColumnType(int i) {
        return this.expressions[i].getType();
    }

    @Override // org.h2.result.ResultInterface
    public int getNullable(int i) {
        return this.expressions[i].getNullable();
    }

    @Override // org.h2.result.ResultInterface
    public boolean isIdentity(int i) {
        return this.expressions[i].isIdentity();
    }

    public void setOffset(long j) {
        this.offset = j;
    }

    public String toString() {
        return super.toString() + " columns: " + this.visibleColumnCount + " rows: " + this.rowCount + " pos: " + this.rowId;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isClosed() {
        return this.closed;
    }

    @Override // org.h2.result.ResultInterface
    public int getFetchSize() {
        return 0;
    }

    @Override // org.h2.result.ResultInterface
    public void setFetchSize(int i) {
    }
}
