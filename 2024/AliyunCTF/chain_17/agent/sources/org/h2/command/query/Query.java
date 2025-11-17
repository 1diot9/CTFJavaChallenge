package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.command.query.ForUpdate;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.DerivedTable;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.Utils;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/Query.class */
public abstract class Query extends Prepared {
    ArrayList<Expression> expressions;
    Expression[] expressionArray;
    ArrayList<QueryOrderBy> orderList;
    SortOrder sort;
    Expression fetchExpr;
    boolean fetchPercent;
    boolean withTies;
    Expression offsetExpr;
    boolean distinct;
    boolean randomAccessResult;
    int visibleColumnCount;
    int resultColumnCount;
    private boolean noCache;
    private long lastLimit;
    private long lastEvaluated;
    private ResultInterface lastResult;
    private Boolean lastExists;
    private Value[] lastParameters;
    private boolean cacheableChecked;
    private boolean neverLazy;
    boolean checkInit;
    boolean isPrepared;

    public abstract boolean isUnion();

    protected abstract ResultInterface queryWithoutCache(long j, ResultTarget resultTarget);

    public abstract void init();

    public abstract void prepareExpressions();

    public abstract void preparePlan();

    public abstract double getCost();

    public abstract HashSet<Table> getTables();

    public abstract void setForUpdate(ForUpdate forUpdate);

    public abstract void mapColumns(ColumnResolver columnResolver, int i);

    public abstract void setEvaluatable(TableFilter tableFilter, boolean z);

    public abstract void addGlobalCondition(Parameter parameter, int i, int i2);

    public abstract boolean allowGlobalConditions();

    public abstract boolean isEverything(ExpressionVisitor expressionVisitor);

    public abstract void updateAggregate(SessionLocal sessionLocal, int i);

    public abstract void fireBeforeSelectTriggers();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/Query$OffsetFetch.class */
    static final class OffsetFetch {
        final long offset;
        final long fetch;
        final boolean fetchPercent;

        OffsetFetch(long j, long j2, boolean z) {
            this.offset = j;
            this.fetch = j2;
            this.fetchPercent = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Query(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setNeverLazy(boolean z) {
        this.neverLazy = z;
    }

    public boolean isNeverLazy() {
        return this.neverLazy;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        LocalResult localResult = new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
        localResult.done();
        return localResult;
    }

    private ResultInterface queryWithoutCacheLazyCheck(long j, ResultTarget resultTarget) {
        boolean z = this.neverLazy && this.session.isLazyQueryExecution();
        if (z) {
            this.session.setLazyQueryExecution(false);
        }
        try {
            ResultInterface queryWithoutCache = queryWithoutCache(j, resultTarget);
            if (z) {
                this.session.setLazyQueryExecution(true);
            }
            return queryWithoutCache;
        } catch (Throwable th) {
            if (z) {
                this.session.setLazyQueryExecution(true);
            }
            throw th;
        }
    }

    @Override // org.h2.command.Prepared
    public final void prepare() {
        if (!this.checkInit) {
            throw DbException.getInternalError("not initialized");
        }
        if (this.isPrepared) {
            return;
        }
        prepareExpressions();
        preparePlan();
    }

    public ArrayList<Expression> getExpressions() {
        return this.expressions;
    }

    public int getCostAsExpression() {
        return (int) Math.min(1000000.0d, 10.0d + (10.0d * getCost()));
    }

    public void setOrder(ArrayList<QueryOrderBy> arrayList) {
        this.orderList = arrayList;
    }

    public boolean hasOrder() {
        return (this.orderList == null && this.sort == null) ? false : true;
    }

    public ForUpdate getForUpdate() {
        return null;
    }

    public int getColumnCount() {
        return this.visibleColumnCount;
    }

    public TypeInfo getRowDataType() {
        if (this.visibleColumnCount == 1) {
            return this.expressionArray[0].getType();
        }
        return TypeInfo.getTypeInfo(41, -1L, -1, new ExtTypeInfoRow(this.expressionArray, this.visibleColumnCount));
    }

    @Override // org.h2.command.Prepared
    public boolean isReadOnly() {
        return isEverything(ExpressionVisitor.READONLY_VISITOR);
    }

    public void setDistinctIfPossible() {
        if (!isAnyDistinct() && this.offsetExpr == null && this.fetchExpr == null) {
            this.distinct = true;
        }
    }

    public boolean isStandardDistinct() {
        return this.distinct;
    }

    public boolean isAnyDistinct() {
        return this.distinct;
    }

    public boolean isRandomAccessResult() {
        return this.randomAccessResult;
    }

    public void setRandomAccessResult(boolean z) {
        this.randomAccessResult = z;
    }

    @Override // org.h2.command.Prepared
    public boolean isQuery() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    public void disableCache() {
        this.noCache = true;
    }

    private boolean sameResultAsLast(Value[] valueArr, Value[] valueArr2, long j) {
        if (!this.cacheableChecked) {
            this.noCache = getMaxDataModificationId() == Long.MAX_VALUE;
            if (!isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) || !isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
                this.noCache = true;
            }
            this.cacheableChecked = true;
        }
        if (this.noCache) {
            return false;
        }
        for (int i = 0; i < valueArr.length; i++) {
            Value value = valueArr2[i];
            Value value2 = valueArr[i];
            if (value != null && !value.equals(value2)) {
                return false;
            }
        }
        return getMaxDataModificationId() <= j;
    }

    private Value[] getParameterValues() {
        ArrayList<Parameter> parameters = getParameters();
        if (parameters == null) {
            return Value.EMPTY_VALUES;
        }
        int size = parameters.size();
        Value[] valueArr = new Value[size];
        for (int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            valueArr[i] = parameter != null ? parameter.getParamValue() : null;
        }
        return valueArr;
    }

    @Override // org.h2.command.Prepared
    public final ResultInterface query(long j) {
        return query(j, null);
    }

    public final ResultInterface query(long j, ResultTarget resultTarget) {
        if (isUnion()) {
            return queryWithoutCacheLazyCheck(j, resultTarget);
        }
        fireBeforeSelectTriggers();
        if (this.noCache || !getDatabase().getOptimizeReuseResults() || (this.session.isLazyQueryExecution() && !this.neverLazy)) {
            return queryWithoutCacheLazyCheck(j, resultTarget);
        }
        Value[] parameterValues = getParameterValues();
        long modificationDataId = getDatabase().getModificationDataId();
        if (isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && this.lastResult != null && !this.lastResult.isClosed() && j == this.lastLimit && sameResultAsLast(parameterValues, this.lastParameters, this.lastEvaluated)) {
            this.lastResult = this.lastResult.createShallowCopy(this.session);
            if (this.lastResult != null) {
                this.lastResult.reset();
                return this.lastResult;
            }
        }
        this.lastParameters = parameterValues;
        closeLastResult();
        ResultInterface queryWithoutCacheLazyCheck = queryWithoutCacheLazyCheck(j, resultTarget);
        this.lastResult = queryWithoutCacheLazyCheck;
        this.lastExists = null;
        this.lastEvaluated = modificationDataId;
        this.lastLimit = j;
        return queryWithoutCacheLazyCheck;
    }

    private void closeLastResult() {
        if (this.lastResult != null) {
            this.lastResult.close();
        }
    }

    public final boolean exists() {
        if (isUnion()) {
            return executeExists();
        }
        fireBeforeSelectTriggers();
        if (this.noCache || !getDatabase().getOptimizeReuseResults()) {
            return executeExists();
        }
        Value[] parameterValues = getParameterValues();
        long modificationDataId = getDatabase().getModificationDataId();
        if (isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && this.lastExists != null && sameResultAsLast(parameterValues, this.lastParameters, this.lastEvaluated)) {
            return this.lastExists.booleanValue();
        }
        this.lastParameters = parameterValues;
        boolean executeExists = executeExists();
        this.lastExists = Boolean.valueOf(executeExists);
        this.lastResult = null;
        this.lastEvaluated = modificationDataId;
        return executeExists;
    }

    private boolean executeExists() {
        ResultInterface queryWithoutCacheLazyCheck = queryWithoutCacheLazyCheck(1L, null);
        boolean hasNext = queryWithoutCacheLazyCheck.hasNext();
        queryWithoutCacheLazyCheck.close();
        return hasNext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean initOrder(ArrayList<String> arrayList, boolean z, ArrayList<TableFilter> arrayList2) {
        Iterator<QueryOrderBy> it = this.orderList.iterator();
        while (it.hasNext()) {
            QueryOrderBy next = it.next();
            Expression expression = next.expression;
            if (expression != null) {
                if (expression.isConstant()) {
                    it.remove();
                } else {
                    int initExpression = initExpression(arrayList, expression, z, arrayList2);
                    next.columnIndexExpr = ValueExpression.get(ValueInteger.get(initExpression + 1));
                    next.expression = this.expressions.get(initExpression).getNonAliasExpression();
                }
            }
        }
        if (this.orderList.isEmpty()) {
            this.orderList = null;
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int initExpression(ArrayList<String> arrayList, Expression expression, boolean z, ArrayList<TableFilter> arrayList2) {
        Database database = getDatabase();
        if (expression instanceof ExpressionColumn) {
            ExpressionColumn expressionColumn = (ExpressionColumn) expression;
            String originalTableAliasName = expressionColumn.getOriginalTableAliasName();
            String originalColumnName = expressionColumn.getOriginalColumnName();
            int columnCount = getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                Expression expression2 = this.expressions.get(i);
                if (expression2 instanceof ExpressionColumn) {
                    ExpressionColumn expressionColumn2 = (ExpressionColumn) expression2;
                    if (!database.equalsIdentifiers(originalColumnName, expressionColumn2.getColumnName(this.session, i))) {
                        continue;
                    } else {
                        if (originalTableAliasName == null) {
                            return i;
                        }
                        String originalTableAliasName2 = expressionColumn2.getOriginalTableAliasName();
                        if (originalTableAliasName2 != null) {
                            if (database.equalsIdentifiers(originalTableAliasName2, originalTableAliasName)) {
                                return i;
                            }
                        } else if (arrayList2 != null) {
                            Iterator<TableFilter> it = arrayList2.iterator();
                            while (it.hasNext()) {
                                if (database.equalsIdentifiers(it.next().getTableAlias(), originalTableAliasName)) {
                                    return i;
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                } else if (!(expression2 instanceof Alias)) {
                    continue;
                } else {
                    if (originalTableAliasName == null && database.equalsIdentifiers(originalColumnName, expression2.getAlias(this.session, i))) {
                        return i;
                    }
                    Expression nonAliasExpression = expression2.getNonAliasExpression();
                    if (nonAliasExpression instanceof ExpressionColumn) {
                        ExpressionColumn expressionColumn3 = (ExpressionColumn) nonAliasExpression;
                        String sql = expressionColumn.getSQL(0, 2);
                        String sql2 = expressionColumn3.getSQL(0, 2);
                        if (database.equalsIdentifiers(originalColumnName, expressionColumn3.getColumnName(this.session, i)) && database.equalsIdentifiers(sql, sql2)) {
                            return i;
                        }
                    } else {
                        continue;
                    }
                }
            }
        } else if (arrayList != null) {
            String sql3 = expression.getSQL(0, 2);
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                if (database.equalsIdentifiers(arrayList.get(i2), sql3)) {
                    return i2;
                }
            }
        }
        if (arrayList == null || (z && !database.getMode().allowUnrelatedOrderByExpressionsInDistinctQueries && !checkOrderOther(this.session, expression, arrayList))) {
            throw DbException.get(ErrorCode.ORDER_BY_NOT_IN_RESULT, expression.getTraceSQL());
        }
        int size2 = this.expressions.size();
        this.expressions.add(expression);
        arrayList.add(expression.getSQL(0, 2));
        return size2;
    }

    private static boolean checkOrderOther(SessionLocal sessionLocal, Expression expression, ArrayList<String> arrayList) {
        if (expression == null || expression.isConstant()) {
            return true;
        }
        String sql = expression.getSQL(0, 2);
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            if (sessionLocal.getDatabase().equalsIdentifiers(sql, it.next())) {
                return true;
            }
        }
        int subexpressionCount = expression.getSubexpressionCount();
        if (!expression.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) || subexpressionCount <= 0) {
            return false;
        }
        for (int i = 0; i < subexpressionCount; i++) {
            if (!checkOrderOther(sessionLocal, expression.getSubexpression(i), arrayList)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void prepareOrder(ArrayList<QueryOrderBy> arrayList, int i) {
        int i2;
        int size = arrayList.size();
        int[] iArr = new int[size];
        int[] iArr2 = new int[size];
        for (int i3 = 0; i3 < size; i3++) {
            QueryOrderBy queryOrderBy = arrayList.get(i3);
            boolean z = false;
            Value value = queryOrderBy.columnIndexExpr.getValue(null);
            if (value == ValueNull.INSTANCE) {
                i2 = 0;
            } else {
                int i4 = value.getInt();
                if (i4 < 0) {
                    z = true;
                    i4 = -i4;
                }
                i2 = i4 - 1;
                if (i2 < 0 || i2 >= i) {
                    throw DbException.get(ErrorCode.ORDER_BY_NOT_IN_RESULT, Integer.toString(i2 + 1));
                }
            }
            iArr[i3] = i2;
            int i5 = queryOrderBy.sortType;
            if (z) {
                i5 ^= 1;
            }
            iArr2[i3] = i5;
        }
        this.sort = new SortOrder(this.session, iArr, iArr2, arrayList);
        this.orderList = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cleanupOrder() {
        int[] queryColumnIndexes = this.sort.getQueryColumnIndexes();
        int length = queryColumnIndexes.length;
        int i = 0;
        for (int i2 : queryColumnIndexes) {
            if (this.expressions.get(i2).isConstant()) {
                i++;
            }
        }
        if (i == 0) {
            return;
        }
        if (i == length) {
            this.sort = null;
            return;
        }
        int i3 = length - i;
        int[] iArr = new int[i3];
        int[] iArr2 = new int[i3];
        int[] sortTypes = this.sort.getSortTypes();
        ArrayList<QueryOrderBy> orderList = this.sort.getOrderList();
        int i4 = 0;
        int i5 = 0;
        while (i5 < i3) {
            if (!this.expressions.get(queryColumnIndexes[i4]).isConstant()) {
                iArr[i5] = queryColumnIndexes[i4];
                iArr2[i5] = sortTypes[i4];
                i5++;
            } else {
                orderList.remove(i5);
            }
            i4++;
        }
        this.sort = new SortOrder(this.session, iArr, iArr2, orderList);
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 66;
    }

    public void setOffset(Expression expression) {
        this.offsetExpr = expression;
    }

    public Expression getOffset() {
        return this.offsetExpr;
    }

    public void setFetch(Expression expression) {
        this.fetchExpr = expression;
    }

    public Expression getFetch() {
        return this.fetchExpr;
    }

    public void setFetchPercent(boolean z) {
        this.fetchPercent = z;
    }

    public boolean isFetchPercent() {
        return this.fetchPercent;
    }

    public void setWithTies(boolean z) {
        this.withTies = z;
    }

    public boolean isWithTies() {
        return this.withTies;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addParameter(Parameter parameter) {
        if (this.parameters == null) {
            this.parameters = Utils.newSmallArrayList();
        }
        this.parameters.add(parameter);
    }

    public final long getMaxDataModificationId() {
        ExpressionVisitor maxModificationIdVisitor = ExpressionVisitor.getMaxModificationIdVisitor();
        isEverything(maxModificationIdVisitor);
        return Math.max(maxModificationIdVisitor.getMaxDataModificationId(), this.session.getSnapshotDataModificationId());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void appendEndOfQueryToSQL(StringBuilder sb, int i, Expression[] expressionArr) {
        if (this.sort != null) {
            this.sort.getSQL(sb.append("\nORDER BY "), expressionArr, this.visibleColumnCount, i);
        } else if (this.orderList != null) {
            sb.append("\nORDER BY ");
            int size = this.orderList.size();
            for (int i2 = 0; i2 < size; i2++) {
                if (i2 > 0) {
                    sb.append(", ");
                }
                this.orderList.get(i2).getSQL(sb, i);
            }
        }
        if (this.offsetExpr != null) {
            String sql = this.offsetExpr.getSQL(i, 2);
            sb.append("\nOFFSET ").append(sql).append(CustomBooleanEditor.VALUE_1.equals(sql) ? " ROW" : " ROWS");
        }
        if (this.fetchExpr != null) {
            sb.append("\nFETCH ").append(this.offsetExpr != null ? "NEXT" : "FIRST");
            String sql2 = this.fetchExpr.getSQL(i, 2);
            boolean z = this.fetchPercent || !CustomBooleanEditor.VALUE_1.equals(sql2);
            if (z) {
                sb.append(' ').append(sql2);
                if (this.fetchPercent) {
                    sb.append(" PERCENT");
                }
            }
            sb.append(!z ? " ROW" : " ROWS").append(this.withTies ? " WITH TIES" : " ONLY");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0025, code lost:            if (r0 < 0) goto L8;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.command.query.Query.OffsetFetch getOffsetFetch(long r9) {
        /*
            r8 = this;
            r0 = r8
            org.h2.expression.Expression r0 = r0.offsetExpr
            if (r0 == 0) goto L33
            r0 = r8
            org.h2.expression.Expression r0 = r0.offsetExpr
            r1 = r8
            org.h2.engine.SessionLocal r1 = r1.session
            org.h2.value.Value r0 = r0.getValue(r1)
            r13 = r0
            r0 = r13
            org.h2.value.ValueNull r1 = org.h2.value.ValueNull.INSTANCE
            if (r0 == r1) goto L28
            r0 = r13
            long r0 = r0.getLong()
            r1 = r0; r0 = r0; 
            r11 = r1
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L30
        L28:
            java.lang.String r0 = "result OFFSET"
            r1 = r13
            org.h2.message.DbException r0 = org.h2.message.DbException.getInvalidValueException(r0, r1)
            throw r0
        L30:
            goto L35
        L33:
            r0 = 0
            r11 = r0
        L35:
            r0 = r9
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L41
            r0 = -1
            goto L42
        L41:
            r0 = r9
        L42:
            r13 = r0
            r0 = r8
            org.h2.expression.Expression r0 = r0.fetchExpr
            if (r0 == 0) goto L8a
            r0 = r8
            org.h2.expression.Expression r0 = r0.fetchExpr
            r1 = r8
            org.h2.engine.SessionLocal r1 = r1.session
            org.h2.value.Value r0 = r0.getValue(r1)
            r15 = r0
            r0 = r15
            org.h2.value.ValueNull r1 = org.h2.value.ValueNull.INSTANCE
            if (r0 == r1) goto L6d
            r0 = r15
            long r0 = r0.getLong()
            r1 = r0; r1 = r0; 
            r16 = r1
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L75
        L6d:
            java.lang.String r0 = "result FETCH"
            r1 = r15
            org.h2.message.DbException r0 = org.h2.message.DbException.getInvalidValueException(r0, r1)
            throw r0
        L75:
            r0 = r13
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L81
            r0 = r16
            goto L88
        L81:
            r0 = r16
            r1 = r13
            long r0 = java.lang.Math.min(r0, r1)
        L88:
            r13 = r0
        L8a:
            r0 = r8
            boolean r0 = r0.fetchPercent
            r15 = r0
            r0 = r15
            if (r0 == 0) goto Lb3
            r0 = r13
            r1 = 100
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto La9
            java.lang.String r0 = "result FETCH PERCENT"
            r1 = r13
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.h2.message.DbException r0 = org.h2.message.DbException.getInvalidValueException(r0, r1)
            throw r0
        La9:
            r0 = r13
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto Lb3
            r0 = 0
            r15 = r0
        Lb3:
            org.h2.command.query.Query$OffsetFetch r0 = new org.h2.command.query.Query$OffsetFetch
            r1 = r0
            r2 = r11
            r3 = r13
            r4 = r15
            r1.<init>(r2, r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.query.Query.getOffsetFetch(long):org.h2.command.query.Query$OffsetFetch");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocalResult finishResult(LocalResult localResult, long j, long j2, boolean z, ResultTarget resultTarget) {
        if (j != 0) {
            localResult.setOffset(j);
        }
        if (j2 >= 0) {
            localResult.setLimit(j2);
            localResult.setFetchPercent(z);
            if (this.withTies) {
                localResult.setWithTies(this.sort);
            }
        }
        localResult.done();
        if (this.randomAccessResult && !this.distinct) {
            localResult = convertToDistinct(localResult);
        }
        if (resultTarget != null) {
            while (localResult.next()) {
                resultTarget.addRow(localResult.currentRow());
            }
            localResult.close();
            return null;
        }
        return localResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocalResult convertToDistinct(ResultInterface resultInterface) {
        LocalResult localResult = new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
        localResult.setDistinct();
        resultInterface.reset();
        while (resultInterface.next()) {
            localResult.addRow(resultInterface.currentRow());
        }
        resultInterface.close();
        localResult.done();
        return localResult;
    }

    public Table toTable(String str, Column[] columnArr, ArrayList<Parameter> arrayList, boolean z, Query query) {
        setParameterList(new ArrayList<>(arrayList));
        if (!this.checkInit) {
            init();
        }
        return new DerivedTable(z ? getDatabase().getSystemSession() : this.session, str, columnArr, this, query);
    }

    @Override // org.h2.command.Prepared
    public void collectDependencies(HashSet<DbObject> hashSet) {
        isEverything(ExpressionVisitor.getDependenciesVisitor(hashSet));
    }

    public boolean isConstantQuery() {
        return !hasOrder() && (this.offsetExpr == null || this.offsetExpr.isConstant()) && (this.fetchExpr == null || this.fetchExpr.isConstant());
    }

    public Expression getIfSingleRow() {
        return null;
    }

    @Override // org.h2.command.Prepared
    public boolean isRetryable() {
        ForUpdate forUpdate = getForUpdate();
        return forUpdate == null || forUpdate.getType() == ForUpdate.Type.SKIP_LOCKED;
    }
}
