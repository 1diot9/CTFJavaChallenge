package org.h2.command.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.Wildcard;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.Window;
import org.h2.expression.condition.Comparison;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.expression.condition.ConditionLocalAndGlobal;
import org.h2.expression.function.CoalesceFunction;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.index.QueryExpressionIndex;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.LazyResult;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableType;
import org.h2.table.TableView;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/Select.class */
public class Select extends Query {
    TableFilter topTableFilter;
    private final ArrayList<TableFilter> filters;
    private final ArrayList<TableFilter> topFilters;
    private Select parentSelect;
    private Expression condition;
    private Expression having;
    private Expression qualify;
    private Expression[] distinctExpressions;
    private int[] distinctIndexes;
    private ArrayList<Expression> group;
    int[] groupIndex;
    boolean[] groupByExpression;
    SelectGroups groupData;
    private int havingIndex;
    private int qualifyIndex;
    private int[] groupByCopies;
    private boolean isExplicitTable;
    boolean isGroupQuery;
    private boolean isGroupSortedQuery;
    private boolean isWindowQuery;
    private ForUpdate forUpdate;
    private double cost;
    private boolean isQuickAggregateQuery;
    private boolean isDistinctQuery;
    private boolean sortUsingIndex;
    private boolean isGroupWindowStage2;
    private HashMap<String, Window> windows;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Select.class.desiredAssertionStatus();
    }

    public Select(SessionLocal sessionLocal, Select select) {
        super(sessionLocal);
        this.filters = Utils.newSmallArrayList();
        this.topFilters = Utils.newSmallArrayList();
        this.parentSelect = select;
    }

    @Override // org.h2.command.query.Query
    public boolean isUnion() {
        return false;
    }

    public void addTableFilter(TableFilter tableFilter, boolean z) {
        this.filters.add(tableFilter);
        if (z) {
            this.topFilters.add(tableFilter);
        }
    }

    public ArrayList<TableFilter> getTopFilters() {
        return this.topFilters;
    }

    public void setExpressions(ArrayList<Expression> arrayList) {
        this.expressions = arrayList;
    }

    public void setExplicitTable() {
        setWildcard();
        this.isExplicitTable = true;
    }

    public void setWildcard() {
        this.expressions = new ArrayList<>(1);
        this.expressions.add(new Wildcard(null, null));
    }

    public void setGroupQuery() {
        this.isGroupQuery = true;
    }

    public void setWindowQuery() {
        this.isWindowQuery = true;
    }

    public void setGroupBy(ArrayList<Expression> arrayList) {
        this.group = arrayList;
    }

    public ArrayList<Expression> getGroupBy() {
        return this.group;
    }

    public SelectGroups getGroupDataIfCurrent(boolean z) {
        if (this.groupData == null || !(z || this.groupData.isCurrentGroup())) {
            return null;
        }
        return this.groupData;
    }

    public void setDistinct() {
        if (this.distinctExpressions != null) {
            throw DbException.getUnsupportedException("DISTINCT ON together with DISTINCT");
        }
        this.distinct = true;
    }

    public void setDistinct(Expression[] expressionArr) {
        if (this.distinct) {
            throw DbException.getUnsupportedException("DISTINCT ON together with DISTINCT");
        }
        this.distinctExpressions = expressionArr;
    }

    @Override // org.h2.command.query.Query
    public boolean isAnyDistinct() {
        return this.distinct || this.distinctExpressions != null;
    }

    public boolean addWindow(String str, Window window) {
        if (this.windows == null) {
            this.windows = new HashMap<>();
        }
        return this.windows.put(str, window) == null;
    }

    public Window getWindow(String str) {
        if (this.windows != null) {
            return this.windows.get(str);
        }
        return null;
    }

    public void addCondition(Expression expression) {
        if (this.condition == null) {
            this.condition = expression;
        } else {
            this.condition = new ConditionAndOr(0, expression, this.condition);
        }
    }

    public Expression getCondition() {
        return this.condition;
    }

    private LazyResult queryGroupSorted(int i, ResultTarget resultTarget, long j, boolean z) {
        LazyResultGroupSorted lazyResultGroupSorted = new LazyResultGroupSorted(this.expressionArray, i);
        skipOffset(lazyResultGroupSorted, j, z);
        if (resultTarget == null) {
            return lazyResultGroupSorted;
        }
        while (lazyResultGroupSorted.next()) {
            resultTarget.addRow(lazyResultGroupSorted.currentRow());
        }
        return null;
    }

    Value[] createGroupSortedRow(Value[] valueArr, int i) {
        Value[] constructGroupResultRow = constructGroupResultRow(valueArr, i);
        if (isHavingNullOrFalse(constructGroupResultRow)) {
            return null;
        }
        return rowForResult(constructGroupResultRow, i);
    }

    private Value[] rowForResult(Value[] valueArr, int i) {
        if (i == this.resultColumnCount) {
            return valueArr;
        }
        return (Value[]) Arrays.copyOf(valueArr, this.resultColumnCount);
    }

    private boolean isHavingNullOrFalse(Value[] valueArr) {
        return this.havingIndex >= 0 && !valueArr[this.havingIndex].isTrue();
    }

    private Index getGroupSortedIndex() {
        ArrayList<Index> indexes;
        if (this.groupIndex != null && this.groupByExpression != null && (indexes = this.topTableFilter.getTable().getIndexes()) != null) {
            Iterator<Index> it = indexes.iterator();
            while (it.hasNext()) {
                Index next = it.next();
                if (!next.getIndexType().isScan() && !next.getIndexType().isHash() && isGroupSortedIndex(this.topTableFilter, next)) {
                    return next;
                }
            }
            return null;
        }
        return null;
    }

    private boolean isGroupSortedIndex(TableFilter tableFilter, Index index) {
        Column[] columns = index.getColumns();
        boolean[] zArr = new boolean[columns.length];
        int size = this.expressions.size();
        for (int i = 0; i < size; i++) {
            if (this.groupByExpression[i]) {
                Expression nonAliasExpression = this.expressions.get(i).getNonAliasExpression();
                if (!(nonAliasExpression instanceof ExpressionColumn)) {
                    return false;
                }
                ExpressionColumn expressionColumn = (ExpressionColumn) nonAliasExpression;
                for (int i2 = 0; i2 < columns.length; i2++) {
                    if (tableFilter == expressionColumn.getTableFilter() && columns[i2].equals(expressionColumn.getColumn())) {
                        zArr[i2] = true;
                    }
                }
                return false;
            }
        }
        for (int i3 = 1; i3 < zArr.length; i3++) {
            if (!zArr[i3 - 1] && zArr[i3]) {
                return false;
            }
        }
        return true;
    }

    boolean isConditionMetForUpdate() {
        if (isConditionMet()) {
            int size = this.filters.size();
            boolean z = true;
            for (int i = 0; i < size; i++) {
                TableFilter tableFilter = this.filters.get(i);
                if (!tableFilter.isJoinOuter() && !tableFilter.isJoinOuterIndirect()) {
                    Row row = tableFilter.get();
                    Table table = tableFilter.getTable();
                    if (table.isRowLockable()) {
                        Row lockRow = table.lockRow(this.session, row, this.forUpdate.getTimeoutMillis());
                        if (lockRow == null) {
                            return false;
                        }
                        if (!row.hasSharedData(lockRow)) {
                            tableFilter.set(lockRow);
                            z = false;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return z || isConditionMet();
        }
        return false;
    }

    boolean isConditionMet() {
        return this.condition == null || this.condition.getBooleanValue(this.session);
    }

    private void queryWindow(int i, LocalResult localResult, long j, boolean z) {
        initGroupData(i);
        try {
            gatherGroup(i, 2);
            processGroupResult(i, localResult, j, z, false);
            this.groupData.reset();
        } catch (Throwable th) {
            this.groupData.reset();
            throw th;
        }
    }

    private void queryGroupWindow(int i, LocalResult localResult, long j, boolean z) {
        initGroupData(i);
        try {
            gatherGroup(i, 1);
            try {
                this.isGroupWindowStage2 = true;
                while (this.groupData.next() != null) {
                    if (this.havingIndex < 0 || this.expressions.get(this.havingIndex).getBooleanValue(this.session)) {
                        updateAgg(i, 2);
                    } else {
                        this.groupData.remove();
                    }
                }
                this.groupData.done();
                processGroupResult(i, localResult, j, z, false);
                this.isGroupWindowStage2 = false;
            } catch (Throwable th) {
                this.isGroupWindowStage2 = false;
                throw th;
            }
        } finally {
            this.groupData.reset();
        }
    }

    private void queryGroup(int i, LocalResult localResult, long j, boolean z) {
        initGroupData(i);
        try {
            gatherGroup(i, 1);
            processGroupResult(i, localResult, j, z, true);
            this.groupData.reset();
        } catch (Throwable th) {
            this.groupData.reset();
            throw th;
        }
    }

    private void initGroupData(int i) {
        if (this.groupData == null) {
            setGroupData(SelectGroups.getInstance(this.session, this.expressions, this.isGroupQuery, this.groupIndex));
        } else {
            updateAgg(i, 0);
        }
        this.groupData.reset();
    }

    void setGroupData(SelectGroups selectGroups) {
        this.groupData = selectGroups;
        this.topTableFilter.visit(tableFilter -> {
            Select select = tableFilter.getSelect();
            if (select != null) {
                select.groupData = selectGroups;
            }
        });
    }

    private void gatherGroup(int i, int i2) {
        long j = 0;
        setCurrentRowNumber(0L);
        while (this.topTableFilter.next()) {
            setCurrentRowNumber(j + 1);
            if (this.forUpdate != null) {
                if (isConditionMetForUpdate()) {
                    j++;
                    this.groupData.nextSource();
                    updateAgg(i, i2);
                }
            } else if (isConditionMet()) {
                j++;
                this.groupData.nextSource();
                updateAgg(i, i2);
            }
        }
        this.groupData.done();
    }

    void updateAgg(int i, int i2) {
        for (int i3 = 0; i3 < i; i3++) {
            if ((this.groupByExpression == null || !this.groupByExpression[i3]) && (this.groupByCopies == null || this.groupByCopies[i3] < 0)) {
                this.expressions.get(i3).updateAggregate(this.session, i2);
            }
        }
    }

    private void processGroupResult(int i, LocalResult localResult, long j, boolean z, boolean z2) {
        while (true) {
            ValueRow next = this.groupData.next();
            if (next != null) {
                Value[] constructGroupResultRow = constructGroupResultRow(next.getList(), i);
                if (!z2 || !isHavingNullOrFalse(constructGroupResultRow)) {
                    if (this.qualifyIndex < 0 || constructGroupResultRow[this.qualifyIndex].isTrue()) {
                        if (z && j > 0) {
                            j--;
                        } else {
                            localResult.addRow(rowForResult(constructGroupResultRow, i));
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private Value[] constructGroupResultRow(Value[] valueArr, int i) {
        int i2;
        Value[] valueArr2 = new Value[i];
        if (this.groupIndex != null) {
            int length = this.groupIndex.length;
            for (int i3 = 0; i3 < length; i3++) {
                valueArr2[this.groupIndex[i3]] = valueArr[i3];
            }
        }
        for (int i4 = 0; i4 < i; i4++) {
            if (this.groupByExpression == null || !this.groupByExpression[i4]) {
                if (this.groupByCopies != null && (i2 = this.groupByCopies[i4]) >= 0) {
                    valueArr2[i4] = valueArr2[i2];
                } else {
                    valueArr2[i4] = this.expressions.get(i4).getValue(this.session);
                }
            }
        }
        return valueArr2;
    }

    private Index getSortIndex() {
        if (this.sort == null) {
            return null;
        }
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        int[] queryColumnIndexes = this.sort.getQueryColumnIndexes();
        int length = queryColumnIndexes.length;
        int[] iArr = new int[length];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = queryColumnIndexes[i2];
            if (i3 < 0 || i3 >= this.expressions.size()) {
                throw DbException.getInvalidValueException("ORDER BY", Integer.valueOf(i3 + 1));
            }
            Expression nonAliasExpression = this.expressions.get(i3).getNonAliasExpression();
            if (!nonAliasExpression.isConstant()) {
                if (!(nonAliasExpression instanceof ExpressionColumn)) {
                    return null;
                }
                ExpressionColumn expressionColumn = (ExpressionColumn) nonAliasExpression;
                if (expressionColumn.getTableFilter() != this.topTableFilter) {
                    return null;
                }
                newSmallArrayList.add(expressionColumn.getColumn());
                int i4 = i;
                i++;
                iArr[i4] = i2;
            }
        }
        Column[] columnArr = (Column[]) newSmallArrayList.toArray(new Column[0]);
        if (columnArr.length == 0) {
            return this.topTableFilter.getTable().getScanIndex(this.session);
        }
        ArrayList<Index> indexes = this.topTableFilter.getTable().getIndexes();
        if (indexes != null) {
            int[] sortTypesWithNullOrdering = this.sort.getSortTypesWithNullOrdering();
            DefaultNullOrdering defaultNullOrdering = getDatabase().getDefaultNullOrdering();
            Iterator<Index> it = indexes.iterator();
            while (it.hasNext()) {
                Index next = it.next();
                if (next.getCreateSQL() != null && !next.getIndexType().isHash()) {
                    IndexColumn[] indexColumns = next.getIndexColumns();
                    if (indexColumns.length >= columnArr.length) {
                        for (int i5 = 0; i5 < columnArr.length; i5++) {
                            IndexColumn indexColumn = indexColumns[i5];
                            Column column = columnArr[i5];
                            if (indexColumn.column != column) {
                                break;
                            }
                            int i6 = sortTypesWithNullOrdering[iArr[i5]];
                            if (!column.isNullable()) {
                                if ((indexColumn.sortType & 1) != (i6 & 1)) {
                                    break;
                                }
                            } else {
                                if (defaultNullOrdering.addExplicitNullOrdering(indexColumn.sortType) != i6) {
                                    break;
                                }
                            }
                        }
                        return next;
                    }
                    continue;
                }
            }
        }
        if (columnArr.length == 1 && columnArr[0].getColumnId() == -1) {
            Index scanIndex = this.topTableFilter.getTable().getScanIndex(this.session);
            if (scanIndex.isRowIdIndex()) {
                return scanIndex;
            }
            return null;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void queryDistinct(ResultTarget resultTarget, long j, long j2, boolean z, boolean z2) {
        if (j2 > 0 && j > 0) {
            j2 += j;
            if (j2 < 0) {
                j2 = Long.MAX_VALUE;
            }
        }
        long j3 = 0;
        setCurrentRowNumber(0L);
        Index index = this.topTableFilter.getIndex();
        SearchRow searchRow = null;
        int columnId = index.getColumns()[0].getColumnId();
        if (!z2) {
            j = 0;
        }
        while (true) {
            long j4 = j3 + 1;
            j3 = this;
            setCurrentRowNumber(j4);
            Cursor findNext = index.findNext(this.session, searchRow, null);
            if (findNext.next()) {
                Value value = findNext.getSearchRow().getValue(columnId);
                if (searchRow == null) {
                    searchRow = index.getRowFactory().createRow();
                }
                searchRow.setValue(columnId, value);
                if (j > 0) {
                    j--;
                } else {
                    resultTarget.addRow(value);
                    if (this.sort == null || this.sortUsingIndex) {
                        if (j2 > 0 && j3 >= j2 && !z) {
                            return;
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private LazyResult queryFlat(int i, ResultTarget resultTarget, long j, long j2, boolean z, boolean z2) {
        if (j2 > 0 && j > 0 && !z2) {
            j2 += j;
            if (j2 < 0) {
                j2 = Long.MAX_VALUE;
            }
        }
        LazyResultQueryFlat lazyResultQueryFlat = new LazyResultQueryFlat(this.expressionArray, i, this.forUpdate != null);
        skipOffset(lazyResultQueryFlat, j, z2);
        if (resultTarget == null) {
            return lazyResultQueryFlat;
        }
        if (j2 < 0 || ((this.sort != null && !this.sortUsingIndex) || (z && !z2))) {
            j2 = Long.MAX_VALUE;
        }
        Value[] valueArr = null;
        while (resultTarget.getRowCount() < j2 && lazyResultQueryFlat.next()) {
            valueArr = lazyResultQueryFlat.currentRow();
            resultTarget.addRow(valueArr);
        }
        if (j2 != Long.MAX_VALUE && z && this.sort != null && valueArr != null) {
            Value[] valueArr2 = valueArr;
            while (lazyResultQueryFlat.next()) {
                Value[] currentRow = lazyResultQueryFlat.currentRow();
                if (this.sort.compare(valueArr2, currentRow) != 0) {
                    break;
                }
                resultTarget.addRow(currentRow);
            }
            resultTarget.limitsWereApplied();
            return null;
        }
        return null;
    }

    private static void skipOffset(LazyResultSelect lazyResultSelect, long j, boolean z) {
        if (z) {
            while (j > 0 && lazyResultSelect.skip()) {
                j--;
            }
        }
    }

    private void queryQuick(int i, ResultTarget resultTarget, boolean z) {
        Value[] valueArr = new Value[i];
        for (int i2 = 0; i2 < i; i2++) {
            valueArr[i2] = this.expressions.get(i2).getValue(this.session);
        }
        if (!z) {
            resultTarget.addRow(valueArr);
        }
    }

    @Override // org.h2.command.query.Query
    protected ResultInterface queryWithoutCache(long j, ResultTarget resultTarget) {
        disableLazyForJoinSubqueries(this.topTableFilter);
        Query.OffsetFetch offsetFetch = getOffsetFetch(j);
        long j2 = offsetFetch.offset;
        long j3 = offsetFetch.fetch;
        boolean z = offsetFetch.fetchPercent;
        boolean z2 = this.session.isLazyQueryExecution() && resultTarget == null && this.forUpdate == null && !this.isQuickAggregateQuery && j3 != 0 && !z && !this.withTies && j2 == 0 && isReadOnly();
        int size = this.expressions.size();
        LocalResult localResult = null;
        if (!z2 && (resultTarget == null || !getDatabase().getSettings().optimizeInsertFromSelect)) {
            localResult = createLocalResult(null);
        }
        boolean z3 = !z;
        if (this.sort != null && (!this.sortUsingIndex || isAnyDistinct())) {
            localResult = createLocalResult(localResult);
            localResult.setSortOrder(this.sort);
            if (!this.sortUsingIndex) {
                z3 = false;
            }
        }
        if (this.distinct) {
            localResult = createLocalResult(localResult);
            if (!this.isDistinctQuery) {
                z3 = false;
                localResult.setDistinct();
            }
        } else if (this.distinctExpressions != null) {
            z3 = false;
            localResult = createLocalResult(localResult);
            localResult.setDistinct(this.distinctIndexes);
        }
        if (this.isWindowQuery || (this.isGroupQuery && !this.isGroupSortedQuery)) {
            localResult = createLocalResult(localResult);
        }
        if (!z2 && (j3 >= 0 || j2 > 0)) {
            localResult = createLocalResult(localResult);
        }
        this.topTableFilter.startQuery(this.session);
        this.topTableFilter.reset();
        this.topTableFilter.lock(this.session);
        ResultTarget resultTarget2 = localResult != null ? localResult : resultTarget;
        boolean z4 = z2 & (resultTarget2 == null);
        LazyResult lazyResult = null;
        if (j3 != 0) {
            long j4 = z ? -1L : j3;
            if (this.isQuickAggregateQuery) {
                queryQuick(size, resultTarget2, z3 && j2 > 0);
            } else if (this.isWindowQuery) {
                if (this.isGroupQuery) {
                    queryGroupWindow(size, localResult, j2, z3);
                } else {
                    queryWindow(size, localResult, j2, z3);
                }
            } else if (this.isGroupQuery) {
                if (this.isGroupSortedQuery) {
                    lazyResult = queryGroupSorted(size, resultTarget2, j2, z3);
                } else {
                    queryGroup(size, localResult, j2, z3);
                }
            } else if (this.isDistinctQuery) {
                queryDistinct(resultTarget2, j2, j4, this.withTies, z3);
            } else {
                lazyResult = queryFlat(size, resultTarget2, j2, j4, this.withTies, z3);
            }
            if (z3) {
                j2 = 0;
            }
        }
        if (!$assertionsDisabled) {
            if (z4 != (lazyResult != null)) {
                throw new AssertionError(z4);
            }
        }
        if (lazyResult != null) {
            if (j3 > 0) {
                lazyResult.setLimit(j3);
            }
            if (this.randomAccessResult) {
                return convertToDistinct(lazyResult);
            }
            return lazyResult;
        }
        if (localResult != null) {
            return finishResult(localResult, j2, j3, z, resultTarget);
        }
        return null;
    }

    private void disableLazyForJoinSubqueries(TableFilter tableFilter) {
        if (this.session.isLazyQueryExecution()) {
            tableFilter.visit(tableFilter2 -> {
                QueryExpressionIndex queryExpressionIndex;
                if (tableFilter2 != tableFilter && tableFilter2.getTable().getTableType() == TableType.VIEW && (queryExpressionIndex = (QueryExpressionIndex) tableFilter2.getIndex()) != null && queryExpressionIndex.getQuery() != null) {
                    queryExpressionIndex.getQuery().setNeverLazy(true);
                }
            });
        }
    }

    private LocalResult createLocalResult(LocalResult localResult) {
        return localResult != null ? localResult : new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0130  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0128 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void expandColumnList() {
        /*
            Method dump skipped, instructions count: 319
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.query.Select.expandColumnList():void");
    }

    private int expandColumnList(TableFilter tableFilter, int i, boolean z, HashMap<Column, ExpressionColumn> hashMap) {
        Expression expressionColumn;
        String schemaName = tableFilter.getSchemaName();
        String tableAlias = tableFilter.getTableAlias();
        if (z) {
            for (Column column : tableFilter.getTable().getColumns()) {
                i = addExpandedColumn(tableFilter, i, hashMap, schemaName, tableAlias, column);
            }
        } else {
            LinkedHashMap<Column, Column> commonJoinColumns = tableFilter.getCommonJoinColumns();
            if (commonJoinColumns != null) {
                TableFilter commonJoinColumnsFilter = tableFilter.getCommonJoinColumnsFilter();
                String schemaName2 = commonJoinColumnsFilter.getSchemaName();
                String tableAlias2 = commonJoinColumnsFilter.getTableAlias();
                for (Map.Entry<Column, Column> entry : commonJoinColumns.entrySet()) {
                    Column key = entry.getKey();
                    Column value = entry.getValue();
                    if (!tableFilter.isCommonJoinColumnToExclude(value) && (hashMap == null || (hashMap.remove(key) == null && hashMap.remove(value) == null))) {
                        Database database = getDatabase();
                        if (key == value || (DataType.hasTotalOrdering(key.getType().getValueType()) && DataType.hasTotalOrdering(value.getType().getValueType()))) {
                            expressionColumn = new ExpressionColumn(database, schemaName2, tableAlias2, commonJoinColumnsFilter.getColumnName(value));
                        } else {
                            expressionColumn = new Alias(new CoalesceFunction(0, new ExpressionColumn(database, schemaName, tableAlias, tableFilter.getColumnName(key)), new ExpressionColumn(database, schemaName2, tableAlias2, commonJoinColumnsFilter.getColumnName(value))), key.getName(), true);
                        }
                        int i2 = i;
                        i++;
                        this.expressions.add(i2, expressionColumn);
                    }
                }
            }
            for (Column column2 : tableFilter.getTable().getColumns()) {
                if ((commonJoinColumns == null || !commonJoinColumns.containsKey(column2)) && !tableFilter.isCommonJoinColumnToExclude(column2)) {
                    i = addExpandedColumn(tableFilter, i, hashMap, schemaName, tableAlias, column2);
                }
            }
        }
        return i;
    }

    private int addExpandedColumn(TableFilter tableFilter, int i, HashMap<Column, ExpressionColumn> hashMap, String str, String str2, Column column) {
        if ((hashMap == null || hashMap.remove(column) == null) && column.getVisible()) {
            i++;
            this.expressions.add(i, new ExpressionColumn(getDatabase(), str, str2, tableFilter.getColumnName(column)));
        }
        return i;
    }

    @Override // org.h2.command.query.Query
    public void init() {
        ArrayList<String> arrayList;
        if (this.checkInit) {
            throw DbException.getInternalError();
        }
        this.filters.sort(TableFilter.ORDER_IN_FROM_COMPARATOR);
        expandColumnList();
        int size = this.expressions.size();
        this.visibleColumnCount = size;
        if (size > 16384) {
            throw DbException.get(ErrorCode.TOO_MANY_COLUMNS_1, "16384");
        }
        if (this.distinctExpressions != null || this.orderList != null || this.group != null) {
            arrayList = new ArrayList<>(this.visibleColumnCount);
            for (int i = 0; i < this.visibleColumnCount; i++) {
                arrayList.add(this.expressions.get(i).getNonAliasExpression().getSQL(0, 2));
            }
        } else {
            arrayList = null;
        }
        if (this.distinctExpressions != null) {
            BitSet bitSet = new BitSet();
            for (Expression expression : this.distinctExpressions) {
                bitSet.set(initExpression(arrayList, expression, false, this.filters));
            }
            int i2 = 0;
            int cardinality = bitSet.cardinality();
            this.distinctIndexes = new int[cardinality];
            for (int i3 = 0; i3 < cardinality; i3++) {
                int nextSetBit = bitSet.nextSetBit(i2);
                this.distinctIndexes[i3] = nextSetBit;
                i2 = nextSetBit + 1;
            }
        }
        if (this.orderList != null) {
            initOrder(arrayList, isAnyDistinct(), this.filters);
        }
        this.resultColumnCount = this.expressions.size();
        if (this.having != null) {
            this.expressions.add(this.having);
            this.havingIndex = this.expressions.size() - 1;
            this.having = null;
        } else {
            this.havingIndex = -1;
        }
        if (this.qualify != null) {
            this.expressions.add(this.qualify);
            this.qualifyIndex = this.expressions.size() - 1;
            this.qualify = null;
        } else {
            this.qualifyIndex = -1;
        }
        if (this.withTies && !hasOrder()) {
            throw DbException.get(ErrorCode.WITH_TIES_WITHOUT_ORDER_BY);
        }
        Database database = getDatabase();
        if (this.group != null) {
            int size2 = this.group.size();
            int size3 = arrayList.size();
            int size4 = this.expressions.size();
            if (size4 > size3) {
                arrayList.ensureCapacity(size4);
                for (int i4 = size3; i4 < size4; i4++) {
                    arrayList.add(this.expressions.get(i4).getSQL(0, 2));
                }
            }
            this.groupIndex = new int[size2];
            for (int i5 = 0; i5 < size2; i5++) {
                Expression expression2 = this.group.get(i5);
                String sql = expression2.getSQL(0, 2);
                int i6 = -1;
                int i7 = 0;
                while (true) {
                    if (i7 >= size3) {
                        break;
                    }
                    if (!database.equalsIdentifiers(arrayList.get(i7), sql)) {
                        i7++;
                    } else {
                        i6 = mergeGroupByExpressions(database, i7, arrayList, false);
                        break;
                    }
                }
                if (i6 < 0) {
                    int i8 = 0;
                    while (true) {
                        if (i8 >= size3) {
                            break;
                        }
                        Expression expression3 = this.expressions.get(i8);
                        if (database.equalsIdentifiers(sql, expression3.getAlias(this.session, i8))) {
                            i6 = mergeGroupByExpressions(database, i8, arrayList, true);
                            break;
                        }
                        sql = expression2.getAlias(this.session, i8);
                        if (!database.equalsIdentifiers(sql, expression3.getAlias(this.session, i8))) {
                            i8++;
                        } else {
                            i6 = mergeGroupByExpressions(database, i8, arrayList, true);
                            break;
                        }
                    }
                }
                if (i6 < 0) {
                    this.groupIndex[i5] = this.expressions.size();
                    this.expressions.add(expression2);
                } else {
                    this.groupIndex[i5] = i6;
                }
            }
            if (this.groupByCopies != null) {
                int[] iArr = this.groupByCopies;
                int length = iArr.length;
                int i9 = 0;
                while (true) {
                    if (i9 < length) {
                        if (iArr[i9] >= 0) {
                            break;
                        } else {
                            i9++;
                        }
                    } else {
                        this.groupByCopies = null;
                        break;
                    }
                }
            }
            this.groupByExpression = new boolean[this.expressions.size()];
            for (int i10 : this.groupIndex) {
                this.groupByExpression[i10] = true;
            }
            this.group = null;
        }
        Iterator<TableFilter> it = this.filters.iterator();
        while (it.hasNext()) {
            mapColumns(it.next(), 0);
        }
        mapCondition(this.havingIndex);
        mapCondition(this.qualifyIndex);
        this.checkInit = true;
    }

    private void mapCondition(int i) {
        if (i >= 0) {
            this.expressions.get(i).mapColumns(new SelectListColumnResolver(this), 0, 0);
        }
    }

    private int mergeGroupByExpressions(Database database, int i, ArrayList<String> arrayList, boolean z) {
        if (this.groupByCopies != null) {
            int i2 = this.groupByCopies[i];
            if (i2 >= 0) {
                return i2;
            }
            if (i2 == -2) {
                return i;
            }
        } else {
            this.groupByCopies = new int[arrayList.size()];
            Arrays.fill(this.groupByCopies, -1);
        }
        String str = arrayList.get(i);
        if (z) {
            int i3 = 0;
            while (true) {
                if (i3 >= i) {
                    break;
                }
                if (!database.equalsIdentifiers(str, arrayList.get(i3))) {
                    i3++;
                } else {
                    i = i3;
                    break;
                }
            }
        }
        int size = arrayList.size();
        for (int i4 = i + 1; i4 < size; i4++) {
            if (database.equalsIdentifiers(str, arrayList.get(i4))) {
                this.groupByCopies[i4] = i;
            }
        }
        this.groupByCopies[i] = -2;
        return i;
    }

    @Override // org.h2.command.query.Query
    public void prepareExpressions() {
        if (this.orderList != null) {
            prepareOrder(this.orderList, this.expressions.size());
        }
        Mode.ExpressionNames expressionNames = this.session.getMode().expressionNames;
        if (expressionNames == Mode.ExpressionNames.ORIGINAL_SQL || expressionNames == Mode.ExpressionNames.POSTGRESQL_STYLE) {
            optimizeExpressionsAndPreserveAliases();
        } else {
            for (int i = 0; i < this.expressions.size(); i++) {
                this.expressions.set(i, this.expressions.get(i).optimize(this.session));
            }
        }
        if (this.sort != null) {
            cleanupOrder();
        }
        if (this.condition != null) {
            this.condition = this.condition.optimizeCondition(this.session);
        }
        if (this.isGroupQuery && this.groupIndex == null && this.havingIndex < 0 && this.qualifyIndex < 0 && this.condition == null && this.filters.size() == 1) {
            this.isQuickAggregateQuery = isEverything(ExpressionVisitor.getOptimizableVisitor(this.filters.get(0).getTable()));
        }
        this.expressionArray = (Expression[]) this.expressions.toArray(new Expression[0]);
    }

    @Override // org.h2.command.query.Query
    public void preparePlan() {
        Index groupSortedIndex;
        Index index;
        Index index2;
        if (this.condition != null) {
            Iterator<TableFilter> it = this.filters.iterator();
            while (it.hasNext()) {
                TableFilter next = it.next();
                if (!next.isJoinOuter() && !next.isJoinOuterIndirect()) {
                    this.condition.createIndexConditions(this.session, next);
                }
            }
        }
        this.cost = preparePlan(this.session.isParsingCreateView());
        if (this.distinct && getDatabase().getSettings().optimizeDistinct && !this.isGroupQuery && this.filters.size() == 1 && this.expressions.size() == 1 && this.condition == null) {
            Expression nonAliasExpression = this.expressions.get(0).getNonAliasExpression();
            if (nonAliasExpression instanceof ExpressionColumn) {
                Column column = ((ExpressionColumn) nonAliasExpression).getColumn();
                int selectivity = column.getSelectivity();
                Index indexForColumn = this.topTableFilter.getTable().getIndexForColumn(column, false, true);
                if (indexForColumn != null && selectivity != 50 && selectivity < 20 && ((index2 = this.topTableFilter.getIndex()) == null || index2.getIndexType().isScan() || indexForColumn == index2)) {
                    this.topTableFilter.setIndex(indexForColumn);
                    this.isDistinctQuery = true;
                }
            }
        }
        if (this.sort != null && !this.isQuickAggregateQuery && !this.isGroupQuery) {
            Index sortIndex = getSortIndex();
            Index index3 = this.topTableFilter.getIndex();
            if (sortIndex != null && index3 != null) {
                if (index3.getIndexType().isScan() || index3 == sortIndex) {
                    this.topTableFilter.setIndex(sortIndex);
                    if (!this.topTableFilter.hasInComparisons()) {
                        this.sortUsingIndex = true;
                    }
                } else if (sortIndex.getIndexColumns() != null && sortIndex.getIndexColumns().length >= index3.getIndexColumns().length) {
                    IndexColumn[] indexColumns = sortIndex.getIndexColumns();
                    IndexColumn[] indexColumns2 = index3.getIndexColumns();
                    boolean z = false;
                    int i = 0;
                    while (true) {
                        if (i >= indexColumns2.length) {
                            break;
                        }
                        if (indexColumns[i].column != indexColumns2[i].column) {
                            z = false;
                            break;
                        } else {
                            if (indexColumns[i].sortType != indexColumns2[i].sortType) {
                                z = true;
                            }
                            i++;
                        }
                    }
                    if (z) {
                        this.topTableFilter.setIndex(sortIndex);
                        this.sortUsingIndex = true;
                    }
                }
            }
            if (this.sortUsingIndex && this.forUpdate != null && !this.topTableFilter.getIndex().isRowIdIndex()) {
                this.sortUsingIndex = false;
            }
        }
        if (!this.isQuickAggregateQuery && this.isGroupQuery && (groupSortedIndex = getGroupSortedIndex()) != null && (index = this.topTableFilter.getIndex()) != null && (index.getIndexType().isScan() || index == groupSortedIndex)) {
            this.topTableFilter.setIndex(groupSortedIndex);
            this.isGroupSortedQuery = true;
        }
        this.isPrepared = true;
    }

    private void optimizeExpressionsAndPreserveAliases() {
        Expression optimize;
        for (int i = 0; i < this.expressions.size(); i++) {
            Expression expression = this.expressions.get(i);
            if (i < this.visibleColumnCount) {
                String alias = expression.getAlias(this.session, i);
                optimize = expression.optimize(this.session);
                if (!optimize.getAlias(this.session, i).equals(alias)) {
                    optimize = new Alias(optimize, alias, true);
                }
            } else {
                optimize = expression.optimize(this.session);
            }
            this.expressions.set(i, optimize);
        }
    }

    @Override // org.h2.command.query.Query
    public double getCost() {
        return this.cost;
    }

    @Override // org.h2.command.query.Query
    public HashSet<Table> getTables() {
        HashSet<Table> hashSet = new HashSet<>();
        Iterator<TableFilter> it = this.filters.iterator();
        while (it.hasNext()) {
            hashSet.add(it.next().getTable());
        }
        return hashSet;
    }

    @Override // org.h2.command.query.Query
    public void fireBeforeSelectTriggers() {
        Iterator<TableFilter> it = this.filters.iterator();
        while (it.hasNext()) {
            it.next().getTable().fire(this.session, 8, true);
        }
    }

    private double preparePlan(boolean z) {
        TableFilter[] tableFilterArr = (TableFilter[]) this.topFilters.toArray(new TableFilter[0]);
        for (TableFilter tableFilter : tableFilterArr) {
            tableFilter.createIndexConditions();
            tableFilter.setFullCondition(this.condition);
        }
        Optimizer optimizer = new Optimizer(tableFilterArr, this.condition, this.session);
        optimizer.optimize(z);
        this.topTableFilter = optimizer.getTopFilter();
        double cost = optimizer.getCost();
        setEvaluatableRecursive(this.topTableFilter);
        if (!z) {
            this.topTableFilter.prepare();
        }
        return cost;
    }

    private void setEvaluatableRecursive(TableFilter tableFilter) {
        while (tableFilter != null) {
            tableFilter.setEvaluatable(tableFilter, true);
            if (this.condition != null) {
                this.condition.setEvaluatable(tableFilter, true);
            }
            TableFilter nestedJoin = tableFilter.getNestedJoin();
            if (nestedJoin != null) {
                setEvaluatableRecursive(nestedJoin);
            }
            Expression joinCondition = tableFilter.getJoinCondition();
            if (joinCondition != null && !joinCondition.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
                Expression optimize = joinCondition.optimize(this.session);
                if (!tableFilter.isJoinOuter() && !tableFilter.isJoinOuterIndirect()) {
                    tableFilter.removeJoinCondition();
                    addCondition(optimize);
                }
            }
            Expression filterCondition = tableFilter.getFilterCondition();
            if (filterCondition != null && !filterCondition.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
                tableFilter.removeFilterCondition();
                addCondition(filterCondition);
            }
            Iterator<Expression> it = this.expressions.iterator();
            while (it.hasNext()) {
                it.next().setEvaluatable(tableFilter, true);
            }
            tableFilter = tableFilter.getJoin();
        }
    }

    @Override // org.h2.command.Prepared
    public String getPlanSQL(int i) {
        Expression[] expressionArr = (Expression[]) this.expressions.toArray(new Expression[0]);
        StringBuilder sb = new StringBuilder();
        Iterator<TableFilter> it = this.topFilters.iterator();
        while (it.hasNext()) {
            Table table = it.next().getTable();
            TableView tableView = table instanceof TableView ? (TableView) table : null;
            if (tableView != null && tableView.isRecursive() && tableView.isTableExpression() && tableView.isTemporary()) {
                sb.append("WITH RECURSIVE ");
                table.getSchema().getSQL(sb, i).append('.');
                ParserUtil.quoteIdentifier(sb, table.getName(), i).append('(');
                Column.writeColumns(sb, table.getColumns(), i);
                sb.append(") AS ");
                table.getSQL(sb, i).append('\n');
            }
        }
        if (this.isExplicitTable) {
            sb.append("TABLE ");
            this.filters.get(0).getPlanSQL(sb, false, i);
        } else {
            sb.append("SELECT");
            if (isAnyDistinct()) {
                sb.append(" DISTINCT");
                if (this.distinctExpressions != null) {
                    Expression.writeExpressions(sb.append(" ON("), this.distinctExpressions, i).append(')');
                }
            }
            for (int i2 = 0; i2 < this.visibleColumnCount; i2++) {
                if (i2 > 0) {
                    sb.append(',');
                }
                sb.append('\n');
                StringUtils.indent(sb, expressionArr[i2].getSQL(i, 2), 4, false);
            }
            TableFilter tableFilter = this.topTableFilter;
            if (tableFilter == null) {
                int size = this.topFilters.size();
                if (size != 1 || !this.topFilters.get(0).isNoFromClauseFilter()) {
                    sb.append("\nFROM ");
                    boolean z = false;
                    for (int i3 = 0; i3 < size; i3++) {
                        z = getPlanFromFilter(sb, i, this.topFilters.get(i3), z);
                    }
                }
            } else if (!tableFilter.isNoFromClauseFilter()) {
                getPlanFromFilter(sb.append("\nFROM "), i, tableFilter, false);
            }
            if (this.condition != null) {
                getFilterSQL(sb, "\nWHERE ", this.condition, i);
            }
            if (this.groupIndex != null) {
                sb.append("\nGROUP BY ");
                int length = this.groupIndex.length;
                for (int i4 = 0; i4 < length; i4++) {
                    if (i4 > 0) {
                        sb.append(", ");
                    }
                    expressionArr[this.groupIndex[i4]].getNonAliasExpression().getUnenclosedSQL(sb, i);
                }
            } else if (this.group != null) {
                sb.append("\nGROUP BY ");
                int size2 = this.group.size();
                for (int i5 = 0; i5 < size2; i5++) {
                    if (i5 > 0) {
                        sb.append(", ");
                    }
                    this.group.get(i5).getUnenclosedSQL(sb, i);
                }
            } else if (this.isGroupQuery && this.having == null && this.havingIndex < 0) {
                int i6 = 0;
                while (true) {
                    if (i6 < this.visibleColumnCount) {
                        if (containsAggregate(expressionArr[i6])) {
                            break;
                        }
                        i6++;
                    } else {
                        sb.append("\nGROUP BY ()");
                        break;
                    }
                }
            }
            getFilterSQL(sb, "\nHAVING ", expressionArr, this.having, this.havingIndex, i);
            getFilterSQL(sb, "\nQUALIFY ", expressionArr, this.qualify, this.qualifyIndex, i);
        }
        appendEndOfQueryToSQL(sb, i, expressionArr);
        if (this.forUpdate != null) {
            this.forUpdate.getSQL(sb, i);
        }
        if ((i & 8) != 0) {
            if (this.isQuickAggregateQuery) {
                sb.append("\n/* direct lookup */");
            }
            if (this.isDistinctQuery) {
                sb.append("\n/* distinct */");
            }
            if (this.sortUsingIndex) {
                sb.append("\n/* index sorted */");
            }
            if (this.isGroupQuery && this.isGroupSortedQuery) {
                sb.append("\n/* group sorted */");
            }
        }
        return sb.toString();
    }

    private static boolean getPlanFromFilter(StringBuilder sb, int i, TableFilter tableFilter, boolean z) {
        TableFilter join;
        do {
            if (z) {
                sb.append('\n');
            }
            tableFilter.getPlanSQL(sb, z, i);
            z = true;
            join = tableFilter.getJoin();
            tableFilter = join;
        } while (join != null);
        return true;
    }

    private static void getFilterSQL(StringBuilder sb, String str, Expression[] expressionArr, Expression expression, int i, int i2) {
        if (expression != null) {
            getFilterSQL(sb, str, expression, i2);
        } else if (i >= 0) {
            getFilterSQL(sb, str, expressionArr[i], i2);
        }
    }

    private static void getFilterSQL(StringBuilder sb, String str, Expression expression, int i) {
        expression.getNonAliasExpression().getUnenclosedSQL(sb.append(str), i);
    }

    private static boolean containsAggregate(Expression expression) {
        if ((expression instanceof DataAnalysisOperation) && ((DataAnalysisOperation) expression).isAggregate()) {
            return true;
        }
        int subexpressionCount = expression.getSubexpressionCount();
        for (int i = 0; i < subexpressionCount; i++) {
            if (containsAggregate(expression.getSubexpression(i))) {
                return true;
            }
        }
        return false;
    }

    public void setHaving(Expression expression) {
        this.having = expression;
    }

    public Expression getHaving() {
        return this.having;
    }

    public void setQualify(Expression expression) {
        this.qualify = expression;
    }

    public Expression getQualify() {
        return this.qualify;
    }

    public TableFilter getTopTableFilter() {
        return this.topTableFilter;
    }

    @Override // org.h2.command.query.Query
    public ForUpdate getForUpdate() {
        return this.forUpdate;
    }

    @Override // org.h2.command.query.Query
    public void setForUpdate(ForUpdate forUpdate) {
        if (forUpdate != null && (isAnyDistinct() || this.isGroupQuery)) {
            throw DbException.get(ErrorCode.FOR_UPDATE_IS_NOT_ALLOWED_IN_DISTINCT_OR_GROUPED_SELECT);
        }
        this.forUpdate = forUpdate;
    }

    @Override // org.h2.command.query.Query
    public void mapColumns(ColumnResolver columnResolver, int i) {
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            it.next().mapColumns(columnResolver, i, 0);
        }
        if (this.condition != null) {
            this.condition.mapColumns(columnResolver, i, 0);
        }
    }

    @Override // org.h2.command.query.Query
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            it.next().setEvaluatable(tableFilter, z);
        }
        if (this.condition != null) {
            this.condition.setEvaluatable(tableFilter, z);
        }
    }

    public boolean isQuickAggregateQuery() {
        return this.isQuickAggregateQuery;
    }

    public boolean isGroupQuery() {
        return this.isGroupQuery;
    }

    public boolean isWindowQuery() {
        return this.isWindowQuery;
    }

    public boolean isGroupWindowStage2() {
        return this.isGroupWindowStage2;
    }

    @Override // org.h2.command.query.Query
    public void addGlobalCondition(Parameter parameter, int i, int i2) {
        Comparison comparison;
        addParameter(parameter);
        Expression nonAliasExpression = this.expressions.get(i).getNonAliasExpression();
        if (nonAliasExpression.isEverything(ExpressionVisitor.QUERY_COMPARABLE_VISITOR)) {
            comparison = new Comparison(i2, nonAliasExpression, parameter, false);
        } else {
            comparison = new Comparison(6, parameter, parameter, false);
        }
        Expression optimize = comparison.optimize(this.session);
        if (this.isWindowQuery) {
            this.qualify = addGlobalCondition(this.qualify, optimize);
            return;
        }
        if (this.isGroupQuery) {
            for (int i3 = 0; this.groupIndex != null && i3 < this.groupIndex.length; i3++) {
                if (this.groupIndex[i3] == i) {
                    this.condition = addGlobalCondition(this.condition, optimize);
                    return;
                }
            }
            if (this.havingIndex >= 0) {
                this.having = this.expressions.get(this.havingIndex);
            }
            this.having = addGlobalCondition(this.having, optimize);
            return;
        }
        this.condition = addGlobalCondition(this.condition, optimize);
    }

    private static Expression addGlobalCondition(Expression expression, Expression expression2) {
        Expression subexpression;
        Expression subexpression2;
        if (!(expression instanceof ConditionLocalAndGlobal)) {
            return new ConditionLocalAndGlobal(expression, expression2);
        }
        if (expression.getSubexpressionCount() == 1) {
            subexpression = null;
            subexpression2 = expression.getSubexpression(0);
        } else {
            subexpression = expression.getSubexpression(0);
            subexpression2 = expression.getSubexpression(1);
        }
        return new ConditionLocalAndGlobal(subexpression, new ConditionAndOr(0, subexpression2, expression2));
    }

    @Override // org.h2.command.query.Query
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            it.next().updateAggregate(sessionLocal, i);
        }
        if (this.condition != null) {
            this.condition.updateAggregate(sessionLocal, i);
        }
        if (this.having != null) {
            this.having.updateAggregate(sessionLocal, i);
        }
        if (this.qualify != null) {
            this.qualify.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.command.query.Query
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                if (this.forUpdate != null) {
                    return false;
                }
                Iterator<TableFilter> it = this.filters.iterator();
                while (it.hasNext()) {
                    if (!it.next().getTable().isDeterministic()) {
                        return false;
                    }
                }
                break;
            case 3:
                if (!getDatabase().getSettings().optimizeEvaluatableSubqueries) {
                    return false;
                }
                break;
            case 4:
                Iterator<TableFilter> it2 = this.filters.iterator();
                while (it2.hasNext()) {
                    expressionVisitor.addDataModificationId(it2.next().getTable().getMaxDataModificationId());
                }
                break;
            case 7:
                Iterator<TableFilter> it3 = this.filters.iterator();
                while (it3.hasNext()) {
                    Table table = it3.next().getTable();
                    expressionVisitor.addDependency(table);
                    table.addDependencies(expressionVisitor.getDependencies());
                }
                break;
        }
        ExpressionVisitor incrementQueryLevel = expressionVisitor.incrementQueryLevel(1);
        Iterator<Expression> it4 = this.expressions.iterator();
        while (it4.hasNext()) {
            if (!it4.next().isEverything(incrementQueryLevel)) {
                return false;
            }
        }
        Iterator<TableFilter> it5 = this.filters.iterator();
        while (it5.hasNext()) {
            Expression joinCondition = it5.next().getJoinCondition();
            if (joinCondition != null && !joinCondition.isEverything(incrementQueryLevel)) {
                return false;
            }
        }
        if (this.condition != null && !this.condition.isEverything(incrementQueryLevel)) {
            return false;
        }
        if (this.having != null && !this.having.isEverything(incrementQueryLevel)) {
            return false;
        }
        if (this.qualify != null && !this.qualify.isEverything(incrementQueryLevel)) {
            return false;
        }
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isCacheable() {
        return this.forUpdate == null;
    }

    @Override // org.h2.command.query.Query
    public boolean allowGlobalConditions() {
        return this.offsetExpr == null && this.fetchExpr == null && this.distinctExpressions == null;
    }

    public SortOrder getSortOrder() {
        return this.sort;
    }

    public Select getParentSelect() {
        return this.parentSelect;
    }

    @Override // org.h2.command.query.Query
    public boolean isConstantQuery() {
        if (!super.isConstantQuery() || this.distinctExpressions != null || this.condition != null || this.isGroupQuery || this.isWindowQuery || !isNoFromClause()) {
            return false;
        }
        for (int i = 0; i < this.visibleColumnCount; i++) {
            if (!this.expressions.get(i).isConstant()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.command.query.Query
    public Expression getIfSingleRow() {
        if (this.offsetExpr != null || this.fetchExpr != null || this.condition != null || this.isGroupQuery || this.isWindowQuery || !isNoFromClause()) {
            return null;
        }
        if (this.visibleColumnCount == 1) {
            return this.expressions.get(0);
        }
        Expression[] expressionArr = new Expression[this.visibleColumnCount];
        for (int i = 0; i < this.visibleColumnCount; i++) {
            expressionArr[i] = this.expressions.get(i);
        }
        return new ExpressionList(expressionArr, false);
    }

    private boolean isNoFromClause() {
        if (this.topTableFilter != null) {
            return this.topTableFilter.isNoFromClauseFilter();
        }
        if (this.topFilters.size() == 1) {
            return this.topFilters.get(0).isNoFromClauseFilter();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/Select$LazyResultSelect.class */
    public abstract class LazyResultSelect extends LazyResult {
        long rowNumber;
        int columnCount;

        LazyResultSelect(Expression[] expressionArr, int i) {
            super(Select.this.getSession(), expressionArr);
            this.columnCount = i;
            Select.this.setCurrentRowNumber(0L);
        }

        @Override // org.h2.result.ResultInterface
        public final int getVisibleColumnCount() {
            return Select.this.visibleColumnCount;
        }

        @Override // org.h2.result.LazyResult, org.h2.result.ResultInterface
        public void reset() {
            super.reset();
            Select.this.topTableFilter.reset();
            Select.this.setCurrentRowNumber(0L);
            this.rowNumber = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/Select$LazyResultQueryFlat.class */
    public final class LazyResultQueryFlat extends LazyResultSelect {
        private boolean forUpdate;

        LazyResultQueryFlat(Expression[] expressionArr, int i, boolean z) {
            super(expressionArr, i);
            this.forUpdate = z;
        }

        /* JADX WARN: Removed duplicated region for block: B:12:0x0054 A[LOOP:1: B:10:0x004c->B:12:0x0054, LOOP_END] */
        @Override // org.h2.result.LazyResult
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected org.h2.value.Value[] fetchNextRow() {
            /*
                r6 = this;
            L0:
                r0 = r6
                org.h2.command.query.Select r0 = org.h2.command.query.Select.this
                org.h2.table.TableFilter r0 = r0.topTableFilter
                boolean r0 = r0.next()
                if (r0 == 0) goto L79
                r0 = r6
                org.h2.command.query.Select r0 = org.h2.command.query.Select.this
                r1 = r6
                long r1 = r1.rowNumber
                r2 = 1
                long r1 = r1 + r2
                r0.setCurrentRowNumber(r1)
                r0 = r6
                boolean r0 = r0.forUpdate
                if (r0 == 0) goto L2e
                r0 = r6
                org.h2.command.query.Select r0 = org.h2.command.query.Select.this
                boolean r0 = r0.isConditionMetForUpdate()
                if (r0 == 0) goto L0
                goto L38
            L2e:
                r0 = r6
                org.h2.command.query.Select r0 = org.h2.command.query.Select.this
                boolean r0 = r0.isConditionMet()
                if (r0 == 0) goto L0
            L38:
                r0 = r6
                r1 = r0
                long r1 = r1.rowNumber
                r2 = 1
                long r1 = r1 + r2
                r0.rowNumber = r1
                r0 = r6
                int r0 = r0.columnCount
                org.h2.value.Value[] r0 = new org.h2.value.Value[r0]
                r7 = r0
                r0 = 0
                r8 = r0
            L4c:
                r0 = r8
                r1 = r6
                int r1 = r1.columnCount
                if (r0 >= r1) goto L77
                r0 = r6
                org.h2.command.query.Select r0 = org.h2.command.query.Select.this
                java.util.ArrayList<org.h2.expression.Expression> r0 = r0.expressions
                r1 = r8
                java.lang.Object r0 = r0.get(r1)
                org.h2.expression.Expression r0 = (org.h2.expression.Expression) r0
                r9 = r0
                r0 = r7
                r1 = r8
                r2 = r9
                r3 = r6
                org.h2.command.query.Select r3 = org.h2.command.query.Select.this
                org.h2.engine.SessionLocal r3 = r3.getSession()
                org.h2.value.Value r2 = r2.getValue(r3)
                r0[r1] = r2
                int r8 = r8 + 1
                goto L4c
            L77:
                r0 = r7
                return r0
            L79:
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.h2.command.query.Select.LazyResultQueryFlat.fetchNextRow():org.h2.value.Value[]");
        }

        @Override // org.h2.result.LazyResult
        protected boolean skipNextRow() {
            while (Select.this.topTableFilter.next()) {
                Select.this.setCurrentRowNumber(this.rowNumber + 1);
                if (Select.this.isConditionMet()) {
                    this.rowNumber++;
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/Select$LazyResultGroupSorted.class */
    public final class LazyResultGroupSorted extends LazyResultSelect {
        private Value[] previousKeyValues;

        LazyResultGroupSorted(Expression[] expressionArr, int i) {
            super(expressionArr, i);
            if (Select.this.groupData == null) {
                Select.this.setGroupData(SelectGroups.getInstance(Select.this.getSession(), Select.this.expressions, Select.this.isGroupQuery, Select.this.groupIndex));
            } else {
                Select.this.updateAgg(i, 0);
                Select.this.groupData.resetLazy();
            }
        }

        @Override // org.h2.command.query.Select.LazyResultSelect, org.h2.result.LazyResult, org.h2.result.ResultInterface
        public void reset() {
            super.reset();
            Select.this.groupData.resetLazy();
            this.previousKeyValues = null;
        }

        @Override // org.h2.result.LazyResult
        protected Value[] fetchNextRow() {
            while (Select.this.topTableFilter.next()) {
                Select.this.setCurrentRowNumber(this.rowNumber + 1);
                if (Select.this.isConditionMet()) {
                    this.rowNumber++;
                    int length = Select.this.groupIndex.length;
                    Value[] valueArr = new Value[length];
                    for (int i = 0; i < length; i++) {
                        valueArr[i] = Select.this.expressions.get(Select.this.groupIndex[i]).getValue(Select.this.getSession());
                    }
                    Value[] valueArr2 = null;
                    if (this.previousKeyValues == null) {
                        this.previousKeyValues = valueArr;
                        Select.this.groupData.nextLazyGroup();
                    } else {
                        SessionLocal session = Select.this.getSession();
                        int i2 = 0;
                        while (true) {
                            if (i2 >= length) {
                                break;
                            }
                            if (session.compare(this.previousKeyValues[i2], valueArr[i2]) == 0) {
                                i2++;
                            } else {
                                valueArr2 = Select.this.createGroupSortedRow(this.previousKeyValues, this.columnCount);
                                this.previousKeyValues = valueArr;
                                Select.this.groupData.nextLazyGroup();
                                break;
                            }
                        }
                    }
                    Select.this.groupData.nextLazyRow();
                    Select.this.updateAgg(this.columnCount, 1);
                    if (valueArr2 != null) {
                        return valueArr2;
                    }
                }
            }
            Value[] valueArr3 = null;
            if (this.previousKeyValues != null) {
                valueArr3 = Select.this.createGroupSortedRow(this.previousKeyValues, this.columnCount);
                this.previousKeyValues = null;
            }
            return valueArr3;
        }
    }
}
