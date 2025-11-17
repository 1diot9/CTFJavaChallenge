package org.h2.index;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.h2.api.ErrorCode;
import org.h2.command.Parser;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Query;
import org.h2.command.query.SelectUnion;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.QueryExpressionTable;
import org.h2.table.TableFilter;
import org.h2.table.TableView;
import org.h2.util.IntArray;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/QueryExpressionIndex.class */
public class QueryExpressionIndex extends Index implements SpatialIndex {
    private static final long MAX_AGE_NANOS;
    private final QueryExpressionTable table;
    private final String querySQL;
    private final ArrayList<Parameter> originalParameters;
    private boolean recursive;
    private final int[] indexMasks;
    private Query query;
    private final SessionLocal createSession;
    private final long evaluatedAt;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !QueryExpressionIndex.class.desiredAssertionStatus();
        MAX_AGE_NANOS = TimeUnit.MILLISECONDS.toNanos(AbstractComponentTracker.LINGERING_TIMEOUT);
    }

    public QueryExpressionIndex(QueryExpressionTable queryExpressionTable, String str, ArrayList<Parameter> arrayList, boolean z) {
        super(queryExpressionTable, 0, null, null, 0, IndexType.createNonUnique(false));
        this.table = queryExpressionTable;
        this.querySQL = str;
        this.originalParameters = arrayList;
        this.recursive = z;
        this.columns = new Column[0];
        this.createSession = null;
        this.indexMasks = null;
        this.evaluatedAt = Long.MIN_VALUE;
    }

    public QueryExpressionIndex(QueryExpressionTable queryExpressionTable, QueryExpressionIndex queryExpressionIndex, SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder) {
        super(queryExpressionTable, 0, null, null, 0, IndexType.createNonUnique(false));
        this.table = queryExpressionTable;
        this.querySQL = queryExpressionIndex.querySQL;
        this.originalParameters = queryExpressionIndex.originalParameters;
        this.recursive = queryExpressionIndex.recursive;
        this.indexMasks = iArr;
        this.createSession = sessionLocal;
        this.columns = new Column[0];
        if (!this.recursive) {
            this.query = getQuery(sessionLocal, iArr);
        }
        if (this.recursive || queryExpressionTable.getTopQuery() != null) {
            this.evaluatedAt = Long.MAX_VALUE;
        } else {
            long nanoTime = System.nanoTime();
            this.evaluatedAt = nanoTime == Long.MAX_VALUE ? nanoTime + 1 : nanoTime;
        }
    }

    public SessionLocal getSession() {
        return this.createSession;
    }

    public boolean isExpired() {
        if ($assertionsDisabled || this.evaluatedAt != Long.MIN_VALUE) {
            return !this.recursive && this.table.getTopQuery() == null && System.nanoTime() - this.evaluatedAt > MAX_AGE_NANOS;
        }
        throw new AssertionError("must not be called for main index of TableView");
    }

    @Override // org.h2.index.Index
    public String getPlanSQL() {
        if (this.query == null) {
            return null;
        }
        return this.query.getPlanSQL(11);
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        if (this.recursive) {
            return 1000.0d;
        }
        return this.query.getCost();
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return find(sessionLocal, searchRow, searchRow2, null);
    }

    @Override // org.h2.index.SpatialIndex
    public Cursor findByGeometry(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2, SearchRow searchRow3) {
        return find(sessionLocal, searchRow, searchRow2, searchRow3);
    }

    private Cursor findRecursive(SearchRow searchRow, SearchRow searchRow2) {
        TableView tableView = (TableView) this.table;
        ResultInterface recursiveResult = tableView.getRecursiveResult();
        if (recursiveResult != null) {
            recursiveResult.reset();
            return new QueryExpressionCursor(this, recursiveResult, searchRow, searchRow2);
        }
        if (this.query == null) {
            Parser parser = new Parser(this.createSession);
            parser.setRightsChecked(true);
            parser.setSuppliedParameters(this.originalParameters);
            this.query = (Query) parser.prepare(this.querySQL);
            this.query.setNeverLazy(true);
        }
        if (!this.query.isUnion()) {
            throw DbException.get(ErrorCode.SYNTAX_ERROR_2, "recursive queries without UNION");
        }
        SelectUnion selectUnion = (SelectUnion) this.query;
        Query left = selectUnion.getLeft();
        left.setNeverLazy(true);
        left.disableCache();
        ResultInterface query = left.query(0L);
        LocalResult emptyResult = selectUnion.getEmptyResult();
        emptyResult.setMaxMemoryRows(Integer.MAX_VALUE);
        while (query.next()) {
            emptyResult.addRow(query.currentRow());
        }
        Query right = selectUnion.getRight();
        right.setNeverLazy(true);
        query.reset();
        tableView.setRecursiveResult(query);
        right.disableCache();
        while (true) {
            ResultInterface query2 = right.query(0L);
            if (query2.hasNext()) {
                while (query2.next()) {
                    emptyResult.addRow(query2.currentRow());
                }
                query2.reset();
                tableView.setRecursiveResult(query2);
            } else {
                tableView.setRecursiveResult(null);
                emptyResult.done();
                return new QueryExpressionCursor(this, emptyResult, searchRow, searchRow2);
            }
        }
    }

    public void setupQueryParameters(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2, SearchRow searchRow3) {
        int i;
        ArrayList<Parameter> parameters = this.query.getParameters();
        if (this.originalParameters != null) {
            Iterator<Parameter> it = this.originalParameters.iterator();
            while (it.hasNext()) {
                Parameter next = it.next();
                if (next != null) {
                    setParameter(parameters, next.getIndex(), next.getValue(sessionLocal));
                }
            }
        }
        if (searchRow != null) {
            i = searchRow.getColumnCount();
        } else if (searchRow2 != null) {
            i = searchRow2.getColumnCount();
        } else if (searchRow3 != null) {
            i = searchRow3.getColumnCount();
        } else {
            i = 0;
        }
        int parameterOffset = this.table.getParameterOffset(this.originalParameters);
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = this.indexMasks[i2];
            if ((i3 & 1) != 0) {
                int i4 = parameterOffset;
                parameterOffset++;
                setParameter(parameters, i4, searchRow.getValue(i2));
            }
            if ((i3 & 2) != 0) {
                int i5 = parameterOffset;
                parameterOffset++;
                setParameter(parameters, i5, searchRow.getValue(i2));
            }
            if ((i3 & 4) != 0) {
                int i6 = parameterOffset;
                parameterOffset++;
                setParameter(parameters, i6, searchRow2.getValue(i2));
            }
            if ((i3 & 16) != 0) {
                int i7 = parameterOffset;
                parameterOffset++;
                setParameter(parameters, i7, searchRow3.getValue(i2));
            }
        }
    }

    private Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2, SearchRow searchRow3) {
        if (this.recursive) {
            return findRecursive(searchRow, searchRow2);
        }
        setupQueryParameters(sessionLocal, searchRow, searchRow2, searchRow3);
        return new QueryExpressionCursor(this, this.query.query(0L), searchRow, searchRow2);
    }

    private static void setParameter(ArrayList<Parameter> arrayList, int i, Value value) {
        if (i >= arrayList.size()) {
            return;
        }
        arrayList.get(i).setValue(value);
    }

    public Query getQuery() {
        return this.query;
    }

    private Query getQuery(SessionLocal sessionLocal, int[] iArr) {
        Query prepareQueryExpression = sessionLocal.prepareQueryExpression(this.querySQL);
        if (iArr == null || !prepareQueryExpression.allowGlobalConditions()) {
            prepareQueryExpression.preparePlan();
            return prepareQueryExpression;
        }
        int parameterOffset = this.table.getParameterOffset(this.originalParameters);
        IntArray intArray = new IntArray();
        int i = 0;
        for (int i2 = 0; i2 < iArr.length; i2++) {
            int i3 = iArr[i2];
            if (i3 != 0) {
                i++;
                int bitCount = Integer.bitCount(i3);
                for (int i4 = 0; i4 < bitCount; i4++) {
                    intArray.add(i2);
                }
            }
        }
        int size = intArray.size();
        ArrayList arrayList = new ArrayList(size);
        int i5 = 0;
        while (i5 < size) {
            int i6 = intArray.get(i5);
            arrayList.add(this.table.getColumn(i6));
            int i7 = iArr[i6];
            if ((i7 & 1) != 0) {
                prepareQueryExpression.addGlobalCondition(new Parameter(parameterOffset + i5), i6, 6);
                i5++;
            }
            if ((i7 & 2) != 0) {
                prepareQueryExpression.addGlobalCondition(new Parameter(parameterOffset + i5), i6, 5);
                i5++;
            }
            if ((i7 & 4) != 0) {
                prepareQueryExpression.addGlobalCondition(new Parameter(parameterOffset + i5), i6, 4);
                i5++;
            }
            if ((i7 & 16) != 0) {
                prepareQueryExpression.addGlobalCondition(new Parameter(parameterOffset + i5), i6, 8);
                i5++;
            }
        }
        this.columns = (Column[]) arrayList.toArray(new Column[0]);
        this.indexColumns = new IndexColumn[i];
        this.columnIds = new int[i];
        int i8 = 0;
        for (int i9 = 0; i9 < 2; i9++) {
            for (int i10 = 0; i10 < iArr.length; i10++) {
                int i11 = iArr[i10];
                if (i11 != 0) {
                    if (i9 == 0) {
                        if ((i11 & 1) == 0) {
                        }
                        Column column = this.table.getColumn(i10);
                        this.indexColumns[i8] = new IndexColumn(column);
                        this.columnIds[i8] = column.getColumnId();
                        i8++;
                    } else {
                        if ((i11 & 1) != 0) {
                        }
                        Column column2 = this.table.getColumn(i10);
                        this.indexColumns[i8] = new IndexColumn(column2);
                        this.columnIds[i8] = column2.getColumnId();
                        i8++;
                    }
                }
            }
        }
        String planSQL = prepareQueryExpression.getPlanSQL(0);
        if (!planSQL.equals(this.querySQL)) {
            prepareQueryExpression = sessionLocal.prepareQueryExpression(planSQL);
        }
        prepareQueryExpression.preparePlan();
        return prepareQueryExpression;
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override // org.h2.index.Index
    public boolean needRebuild() {
        return false;
    }

    public void setRecursive(boolean z) {
        this.recursive = z;
    }

    @Override // org.h2.index.Index
    public long getRowCount(SessionLocal sessionLocal) {
        return 0L;
    }

    @Override // org.h2.index.Index
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return 0L;
    }

    public boolean isRecursive() {
        return this.recursive;
    }
}
