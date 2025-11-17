package org.h2.expression.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import org.h2.api.ErrorCode;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.result.SortOrder;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueInteger;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/DataAnalysisOperation.class */
public abstract class DataAnalysisOperation extends Expression {
    public static final int STAGE_RESET = 0;
    public static final int STAGE_GROUP = 1;
    public static final int STAGE_WINDOW = 2;
    protected final Select select;
    protected Window over;
    protected SortOrder overOrderBySort;
    private int numFrameExpressions;
    private int lastGroupRowId;

    public abstract boolean isAggregate();

    protected abstract void updateAggregate(SessionLocal sessionLocal, SelectGroups selectGroups, int i);

    protected abstract int getNumExpressions();

    protected abstract void rememberExpressions(SessionLocal sessionLocal, Value[] valueArr);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Object createAggregateData();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Value getAggregatedValue(SessionLocal sessionLocal, Object obj);

    protected abstract void getOrderedResultLoop(SessionLocal sessionLocal, HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public static SortOrder createOrder(SessionLocal sessionLocal, ArrayList<QueryOrderBy> arrayList, int i) {
        int size = arrayList.size();
        int[] iArr = new int[size];
        int[] iArr2 = new int[size];
        for (int i2 = 0; i2 < size; i2++) {
            QueryOrderBy queryOrderBy = arrayList.get(i2);
            iArr[i2] = i2 + i;
            iArr2[i2] = queryOrderBy.sortType;
        }
        return new SortOrder(sessionLocal, iArr, iArr2, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DataAnalysisOperation(Select select) {
        this.select = select;
    }

    public Window getOverCondition() {
        return this.over;
    }

    public void setOverCondition(Window window) {
        this.over = window;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SortOrder getOverOrderBySort() {
        return this.overOrderBySort;
    }

    @Override // org.h2.expression.Expression
    public final void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        int i3;
        if (this.over != null) {
            if (i2 != 0) {
                throw DbException.get(ErrorCode.INVALID_USE_OF_AGGREGATE_FUNCTION_1, getTraceSQL());
            }
            i3 = 1;
        } else {
            if (i2 == 2) {
                throw DbException.get(ErrorCode.INVALID_USE_OF_AGGREGATE_FUNCTION_1, getTraceSQL());
            }
            i3 = 2;
        }
        mapColumnsAnalysis(columnResolver, i, i3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void mapColumnsAnalysis(ColumnResolver columnResolver, int i, int i2) {
        if (this.over != null) {
            this.over.mapColumns(columnResolver, i);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        if (this.over != null) {
            this.over.optimize(sessionLocal);
            ArrayList<QueryOrderBy> orderBy = this.over.getOrderBy();
            if (orderBy != null) {
                this.overOrderBySort = createOrder(sessionLocal, orderBy, getNumExpressions());
            } else if (!isAggregate()) {
                this.overOrderBySort = new SortOrder(sessionLocal, new int[getNumExpressions()]);
            }
            WindowFrame windowFrame = this.over.getWindowFrame();
            if (windowFrame != null) {
                int numExpressions = getNumExpressions();
                int i = 0;
                if (orderBy != null) {
                    i = orderBy.size();
                    numExpressions += i;
                }
                int i2 = 0;
                WindowFrameBound starting = windowFrame.getStarting();
                if (starting.isParameterized()) {
                    checkOrderBy(windowFrame.getUnits(), i);
                    if (starting.isVariable()) {
                        starting.setExpressionIndex(numExpressions);
                        i2 = 0 + 1;
                    }
                }
                WindowFrameBound following = windowFrame.getFollowing();
                if (following != null && following.isParameterized()) {
                    checkOrderBy(windowFrame.getUnits(), i);
                    if (following.isVariable()) {
                        following.setExpressionIndex(numExpressions + i2);
                        i2++;
                    }
                }
                this.numFrameExpressions = i2;
            }
        }
        return this;
    }

    private void checkOrderBy(WindowFrameUnits windowFrameUnits, int i) {
        switch (windowFrameUnits) {
            case RANGE:
                if (i != 1) {
                    String traceSQL = getTraceSQL();
                    throw DbException.getSyntaxError(traceSQL, traceSQL.length() - 1, "exactly one sort key is required for RANGE units");
                }
                return;
            case GROUPS:
                if (i < 1) {
                    String traceSQL2 = getTraceSQL();
                    throw DbException.getSyntaxError(traceSQL2, traceSQL2.length() - 1, "a sort key is required for GROUPS units");
                }
                return;
            default:
                return;
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        if (this.over != null) {
            this.over.setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public final void updateAggregate(SessionLocal sessionLocal, int i) {
        int currentGroupRowId;
        if (i == 0) {
            updateGroupAggregates(sessionLocal, 0);
            this.lastGroupRowId = 0;
            return;
        }
        boolean z = i == 2;
        if (z != (this.over != null)) {
            if (!z && this.select.isWindowQuery()) {
                updateGroupAggregates(sessionLocal, i);
                return;
            }
            return;
        }
        SelectGroups groupDataIfCurrent = this.select.getGroupDataIfCurrent(z);
        if (groupDataIfCurrent == null || this.lastGroupRowId == (currentGroupRowId = groupDataIfCurrent.getCurrentGroupRowId())) {
            return;
        }
        this.lastGroupRowId = currentGroupRowId;
        if (this.over != null && !this.select.isGroupQuery()) {
            this.over.updateAggregate(sessionLocal, i);
        }
        updateAggregate(sessionLocal, groupDataIfCurrent, currentGroupRowId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateGroupAggregates(SessionLocal sessionLocal, int i) {
        if (this.over != null) {
            this.over.updateAggregate(sessionLocal, i);
        }
    }

    private int getNumFrameExpressions() {
        return this.numFrameExpressions;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getWindowData(SessionLocal sessionLocal, SelectGroups selectGroups, boolean z) {
        Object data;
        Value currentKey = this.over.getCurrentKey(sessionLocal);
        PartitionData windowExprData = selectGroups.getWindowExprData(this, currentKey);
        if (windowExprData == null) {
            data = z ? new ArrayList() : createAggregateData();
            selectGroups.setWindowExprData(this, currentKey, new PartitionData(data));
        } else {
            data = windowExprData.getData();
        }
        return data;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getGroupData(SelectGroups selectGroups, boolean z) {
        Object currentGroupExprData = selectGroups.getCurrentGroupExprData(this);
        if (currentGroupExprData == null) {
            if (z) {
                return null;
            }
            currentGroupExprData = createAggregateData();
            selectGroups.setCurrentGroupExprData(this, currentGroupExprData);
        }
        return currentGroupExprData;
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (this.over == null) {
            return true;
        }
        switch (expressionVisitor.getType()) {
            case 0:
            case 1:
            case 2:
            case 8:
            case 11:
                return false;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            default:
                return true;
        }
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        SelectGroups groupDataIfCurrent = this.select.getGroupDataIfCurrent(this.over != null);
        if (groupDataIfCurrent == null) {
            throw DbException.get(ErrorCode.INVALID_USE_OF_AGGREGATE_FUNCTION_1, getTraceSQL());
        }
        return this.over == null ? getAggregatedValue(sessionLocal, getGroupData(groupDataIfCurrent, true)) : getWindowResult(sessionLocal, groupDataIfCurrent);
    }

    private Value getWindowResult(SessionLocal sessionLocal, SelectGroups selectGroups) {
        Object data;
        boolean isOrdered = this.over.isOrdered();
        Value currentKey = this.over.getCurrentKey(sessionLocal);
        PartitionData windowExprData = selectGroups.getWindowExprData(this, currentKey);
        if (windowExprData == null) {
            data = isOrdered ? new ArrayList() : createAggregateData();
            windowExprData = new PartitionData(data);
            selectGroups.setWindowExprData(this, currentKey, windowExprData);
        } else {
            data = windowExprData.getData();
        }
        if (isOrdered || !isAggregate()) {
            Value orderedResult = getOrderedResult(sessionLocal, selectGroups, windowExprData, data);
            if (orderedResult == null) {
                return getAggregatedValue(sessionLocal, null);
            }
            return orderedResult;
        }
        Value result = windowExprData.getResult();
        if (result == null) {
            result = getAggregatedValue(sessionLocal, data);
            windowExprData.setResult(result);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateOrderedAggregate(SessionLocal sessionLocal, SelectGroups selectGroups, int i, ArrayList<QueryOrderBy> arrayList) {
        int numExpressions = getNumExpressions();
        int size = arrayList != null ? arrayList.size() : 0;
        int numFrameExpressions = getNumFrameExpressions();
        Value[] valueArr = new Value[numExpressions + size + numFrameExpressions + 1];
        rememberExpressions(sessionLocal, valueArr);
        for (int i2 = 0; i2 < size; i2++) {
            int i3 = numExpressions;
            numExpressions++;
            valueArr[i3] = arrayList.get(i2).expression.getValue(sessionLocal);
        }
        if (numFrameExpressions > 0) {
            WindowFrame windowFrame = this.over.getWindowFrame();
            WindowFrameBound starting = windowFrame.getStarting();
            if (starting.isVariable()) {
                int i4 = numExpressions;
                numExpressions++;
                valueArr[i4] = starting.getValue().getValue(sessionLocal);
            }
            WindowFrameBound following = windowFrame.getFollowing();
            if (following != null && following.isVariable()) {
                int i5 = numExpressions;
                numExpressions++;
                valueArr[i5] = following.getValue().getValue(sessionLocal);
            }
        }
        valueArr[numExpressions] = ValueInteger.get(i);
        ((ArrayList) getWindowData(sessionLocal, selectGroups, true)).add(valueArr);
    }

    private Value getOrderedResult(SessionLocal sessionLocal, SelectGroups selectGroups, PartitionData partitionData, Object obj) {
        HashMap<Integer, Value> orderedResult = partitionData.getOrderedResult();
        if (orderedResult == null) {
            orderedResult = new HashMap<>();
            ArrayList<Value[]> arrayList = (ArrayList) obj;
            int numExpressions = getNumExpressions();
            ArrayList<QueryOrderBy> orderBy = this.over.getOrderBy();
            if (orderBy != null) {
                numExpressions += orderBy.size();
                arrayList.sort(this.overOrderBySort);
            }
            getOrderedResultLoop(sessionLocal, orderedResult, arrayList, numExpressions + getNumFrameExpressions());
            partitionData.setOrderedResult(orderedResult);
        }
        return orderedResult.get(Integer.valueOf(selectGroups.getCurrentGroupRowId()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StringBuilder appendTailConditions(StringBuilder sb, int i, boolean z) {
        if (this.over != null) {
            sb.append(' ');
            this.over.getSQL(sb, i, z);
        }
        return sb;
    }
}
