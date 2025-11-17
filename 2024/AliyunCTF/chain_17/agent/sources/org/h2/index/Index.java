package org.h2.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.NullsDistinct;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.schema.SchemaObject;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/Index.class */
public abstract class Index extends SchemaObject {
    protected IndexColumn[] indexColumns;
    protected Column[] columns;
    protected int[] columnIds;
    protected final int uniqueColumnColumn;
    protected final Table table;
    protected final IndexType indexType;
    private final RowFactory rowFactory;
    private final RowFactory uniqueRowFactory;

    public abstract void close(SessionLocal sessionLocal);

    public abstract void add(SessionLocal sessionLocal, Row row);

    public abstract void remove(SessionLocal sessionLocal, Row row);

    public abstract Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2);

    public abstract double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan);

    public abstract void remove(SessionLocal sessionLocal);

    public abstract void truncate(SessionLocal sessionLocal);

    public abstract boolean needRebuild();

    public abstract long getRowCount(SessionLocal sessionLocal);

    public abstract long getRowCountApproximation(SessionLocal sessionLocal);

    /* JADX INFO: Access modifiers changed from: protected */
    public static void checkIndexColumnTypes(IndexColumn[] indexColumnArr) {
        for (IndexColumn indexColumn : indexColumnArr) {
            if (!DataType.isIndexable(indexColumn.column.getType())) {
                throw DbException.getUnsupportedException("Index on column: " + indexColumn.column.getCreateSQL());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Index(Table table, int i, String str, IndexColumn[] indexColumnArr, int i2, IndexType indexType) {
        super(table.getSchema(), i, str, 5);
        RowFactory rowFactory;
        this.uniqueColumnColumn = i2;
        this.indexType = indexType;
        this.table = table;
        if (indexColumnArr != null) {
            this.indexColumns = indexColumnArr;
            this.columns = new Column[indexColumnArr.length];
            int length = this.columns.length;
            this.columnIds = new int[length];
            for (int i3 = 0; i3 < length; i3++) {
                Column column = indexColumnArr[i3].column;
                this.columns[i3] = column;
                this.columnIds[i3] = column.getColumnId();
            }
        }
        RowFactory rowFactory2 = this.database.getRowFactory();
        CompareMode compareMode = this.database.getCompareMode();
        Column[] columns = this.table.getColumns();
        this.rowFactory = rowFactory2.createRowFactory(this.database, compareMode, this.database, columns, indexType.isScan() ? null : indexColumnArr, true);
        if (i2 > 0) {
            if (indexColumnArr == null || i2 == indexColumnArr.length) {
                rowFactory = this.rowFactory;
            } else {
                rowFactory = rowFactory2.createRowFactory(this.database, compareMode, this.database, columns, (IndexColumn[]) Arrays.copyOf(indexColumnArr, i2), true);
            }
        } else {
            rowFactory = null;
        }
        this.uniqueRowFactory = rowFactory;
    }

    @Override // org.h2.engine.DbObject
    public final int getType() {
        return 1;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.table.removeIndex(this);
        remove(sessionLocal);
        this.database.removeMeta(sessionLocal, getId());
    }

    @Override // org.h2.schema.SchemaObject
    public final boolean isHidden() {
        return this.table.isHidden();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
        StringBuilder sb = new StringBuilder("CREATE ");
        sb.append(this.indexType.getSQL(true));
        sb.append(' ');
        if (this.table.isHidden()) {
            sb.append("IF NOT EXISTS ");
        }
        sb.append(str);
        sb.append(" ON ");
        table.getSQL(sb, 0);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        return getColumnListSQL(sb, 0).toString();
    }

    private StringBuilder getColumnListSQL(StringBuilder sb, int i) {
        sb.append('(');
        int length = this.indexColumns.length;
        if (this.uniqueColumnColumn > 0 && this.uniqueColumnColumn < length) {
            IndexColumn.writeColumns(sb, this.indexColumns, 0, this.uniqueColumnColumn, i).append(") INCLUDE(");
            IndexColumn.writeColumns(sb, this.indexColumns, this.uniqueColumnColumn, length, i);
        } else {
            IndexColumn.writeColumns(sb, this.indexColumns, 0, length, i);
        }
        return sb.append(')');
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQLForCopy(this.table, getSQL(0));
    }

    public String getPlanSQL() {
        return getSQL(11);
    }

    public void update(SessionLocal sessionLocal, Row row, Row row2) {
        remove(sessionLocal, row);
        add(sessionLocal, row2);
    }

    public boolean isFindUsingFullTableScan() {
        return false;
    }

    public boolean canGetFirstOrLast() {
        return false;
    }

    public boolean canFindNext() {
        return false;
    }

    public Cursor findNext(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        throw DbException.getInternalError(toString());
    }

    public Cursor findFirstOrLast(SessionLocal sessionLocal, boolean z) {
        throw DbException.getInternalError(toString());
    }

    public long getDiskSpaceUsed() {
        return 0L;
    }

    public final int compareRows(SearchRow searchRow, SearchRow searchRow2) {
        if (searchRow == searchRow2) {
            return 0;
        }
        int length = this.indexColumns.length;
        for (int i = 0; i < length; i++) {
            int i2 = this.columnIds[i];
            Value value = searchRow.getValue(i2);
            Value value2 = searchRow2.getValue(i2);
            if (value == null || value2 == null) {
                return 0;
            }
            int compareValues = compareValues(value, value2, this.indexColumns[i].sortType);
            if (compareValues != 0) {
                return compareValues;
            }
        }
        return 0;
    }

    private int compareValues(Value value, Value value2, int i) {
        if (value == value2) {
            return 0;
        }
        boolean z = value == ValueNull.INSTANCE;
        if (z || value2 == ValueNull.INSTANCE) {
            return this.table.getDatabase().getDefaultNullOrdering().compareNull(z, i);
        }
        int compareValues = this.table.compareValues(this.database, value, value2);
        if ((i & 1) != 0) {
            compareValues = -compareValues;
        }
        return compareValues;
    }

    public int getColumnIndex(Column column) {
        int length = this.columns.length;
        for (int i = 0; i < length; i++) {
            if (this.columns[i].equals(column)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isFirstColumn(Column column) {
        return column.equals(this.columns[0]);
    }

    public final IndexColumn[] getIndexColumns() {
        return this.indexColumns;
    }

    public final Column[] getColumns() {
        return this.columns;
    }

    public final int getUniqueColumnCount() {
        return this.uniqueColumnColumn;
    }

    public final IndexType getIndexType() {
        return this.indexType;
    }

    public Table getTable() {
        return this.table;
    }

    public Row getRow(SessionLocal sessionLocal, long j) {
        throw DbException.getUnsupportedException(toString());
    }

    public boolean isRowIdIndex() {
        return false;
    }

    public boolean canScan() {
        return true;
    }

    public DbException getDuplicateKeyException(String str) {
        StringBuilder sb = new StringBuilder();
        getSQL(sb, 3).append(" ON ");
        this.table.getSQL(sb, 3);
        getColumnListSQL(sb, 3);
        if (str != null) {
            sb.append(" VALUES ").append(str);
        }
        DbException dbException = DbException.get(ErrorCode.DUPLICATE_KEY_1, sb.toString());
        dbException.setSource(this);
        return dbException;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StringBuilder getDuplicatePrimaryKeyMessage(int i) {
        StringBuilder sb = new StringBuilder("PRIMARY KEY ON ");
        this.table.getSQL(sb, 3);
        if (i >= 0 && i < this.indexColumns.length) {
            sb.append('(');
            this.indexColumns[i].getSQL(sb, 3).append(')');
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final long getCostRangeIndex(int[] iArr, long j, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, boolean z, AllColumnsForPlan allColumnsForPlan) {
        boolean z2;
        long length;
        long j2 = j + 1000;
        int i2 = 0;
        long j3 = j2;
        if (iArr != null) {
            int i3 = 0;
            int length2 = this.columns.length;
            boolean z3 = false;
            while (true) {
                if (i3 >= length2) {
                    break;
                }
                int i4 = i3;
                i3++;
                Column column = this.columns[i4];
                int i5 = iArr[column.getColumnId()];
                if ((i5 & 1) == 1) {
                    if (i3 > 0 && i3 == this.uniqueColumnColumn) {
                        j3 = 3;
                        break;
                    }
                    i2 = 100 - (((100 - i2) * (100 - column.getSelectivity())) / 100);
                    long j4 = (j2 * i2) / 100;
                    if (j4 <= 0) {
                        j4 = 1;
                    }
                    j3 = 2 + Math.max(j2 / j4, 1L);
                } else if ((i5 & 6) == 6) {
                    j3 = 2 + (j3 / 4);
                    z3 = true;
                } else if ((i5 & 2) == 2) {
                    j3 = 2 + (j3 / 3);
                    z3 = true;
                } else if ((i5 & 4) == 4) {
                    j3 /= 3;
                    z3 = true;
                } else if (i5 == 0) {
                    i3--;
                }
            }
            if (z3) {
                while (i3 < length2 && iArr[this.columns[i3].getColumnId()] != 0) {
                    i3++;
                    j3--;
                }
            }
            j3 += length2 - i3;
        }
        long j5 = 0;
        if (sortOrder != null) {
            j5 = 100 + (j2 / 10);
        }
        if (sortOrder != null && !z) {
            boolean z4 = true;
            int i6 = 0;
            int[] sortTypesWithNullOrdering = sortOrder.getSortTypesWithNullOrdering();
            TableFilter tableFilter = tableFilterArr == null ? null : tableFilterArr[i];
            int i7 = 0;
            int length3 = sortTypesWithNullOrdering.length;
            while (true) {
                if (i7 >= length3 || i7 >= this.indexColumns.length) {
                    break;
                }
                Column column2 = sortOrder.getColumn(i7, tableFilter);
                if (column2 == null) {
                    z4 = false;
                    break;
                }
                IndexColumn indexColumn = this.indexColumns[i7];
                if (!column2.equals(indexColumn.column)) {
                    z4 = false;
                    break;
                }
                if (sortTypesWithNullOrdering[i7] != indexColumn.sortType) {
                    z4 = false;
                    break;
                }
                i6++;
                i7++;
            }
            if (z4) {
                j5 = 100 - i6;
            }
        }
        if (!z && allColumnsForPlan != null) {
            z2 = false;
            ArrayList<Column> arrayList = allColumnsForPlan.get(getTable());
            if (arrayList != null) {
                int mainIndexColumn = this.table.getMainIndexColumn();
                Iterator<Column> it = arrayList.iterator();
                loop3: while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Column next = it.next();
                    int columnId = next.getColumnId();
                    if (columnId != -1 && columnId != mainIndexColumn) {
                        for (Column column3 : this.columns) {
                            if (next == column3) {
                                break;
                            }
                        }
                        z2 = true;
                        break loop3;
                    }
                }
            }
        } else {
            z2 = true;
        }
        if (z) {
            length = j3 + j5 + 20;
        } else if (z2) {
            length = j3 + j3 + j5 + 20;
        } else {
            length = j3 + j5 + this.columns.length;
        }
        return length;
    }

    public final boolean needsUniqueCheck(SearchRow searchRow) {
        NullsDistinct effectiveNullsDistinct = this.indexType.getEffectiveNullsDistinct();
        if (effectiveNullsDistinct != null) {
            if (effectiveNullsDistinct != NullsDistinct.NOT_DISTINCT) {
                if (needsUniqueCheck(searchRow, effectiveNullsDistinct == NullsDistinct.DISTINCT)) {
                }
            }
            return true;
        }
        return false;
    }

    private boolean needsUniqueCheck(SearchRow searchRow, boolean z) {
        if (z) {
            for (int i = 0; i < this.uniqueColumnColumn; i++) {
                if (searchRow.getValue(this.columnIds[i]) == ValueNull.INSTANCE) {
                    return false;
                }
            }
            return true;
        }
        for (int i2 = 0; i2 < this.uniqueColumnColumn; i2++) {
            if (searchRow.getValue(this.columnIds[i2]) != ValueNull.INSTANCE) {
                return true;
            }
        }
        return false;
    }

    public RowFactory getRowFactory() {
        return this.rowFactory;
    }

    public RowFactory getUniqueRowFactory() {
        return this.uniqueRowFactory;
    }
}
