package org.h2.expression.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.WindowFrame;
import org.h2.expression.analysis.WindowFrameBound;
import org.h2.expression.analysis.WindowFrameBoundType;
import org.h2.expression.analysis.WindowFrameExclusion;
import org.h2.expression.analysis.WindowFrameUnits;
import org.h2.result.SortOrder;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AbstractAggregate.class */
public abstract class AbstractAggregate extends DataAnalysisOperation {
    protected final boolean distinct;
    protected final Expression[] args;
    protected Expression filterCondition;
    protected TypeInfo type;
    static final /* synthetic */ boolean $assertionsDisabled;

    protected abstract void updateFromExpressions(SessionLocal sessionLocal, Object obj, Value[] valueArr);

    protected abstract void updateAggregate(SessionLocal sessionLocal, Object obj);

    static {
        $assertionsDisabled = !AbstractAggregate.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractAggregate(Select select, Expression[] expressionArr, boolean z) {
        super(select);
        this.args = expressionArr;
        this.distinct = z;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public final boolean isAggregate() {
        return true;
    }

    public Expression getFilterCondition() {
        return this.filterCondition;
    }

    public void setFilterCondition(Expression expression) {
        this.filterCondition = expression;
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public void mapColumnsAnalysis(ColumnResolver columnResolver, int i, int i2) {
        for (Expression expression : this.args) {
            expression.mapColumns(columnResolver, i, i2);
        }
        if (this.filterCondition != null) {
            this.filterCondition.mapColumns(columnResolver, i, i2);
        }
        super.mapColumnsAnalysis(columnResolver, i, i2);
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        for (int i = 0; i < this.args.length; i++) {
            this.args[i] = this.args[i].optimize(sessionLocal);
        }
        if (this.filterCondition != null) {
            this.filterCondition = this.filterCondition.optimizeCondition(sessionLocal);
        }
        return super.optimize(sessionLocal);
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        for (Expression expression : this.args) {
            expression.setEvaluatable(tableFilter, z);
        }
        if (this.filterCondition != null) {
            this.filterCondition.setEvaluatable(tableFilter, z);
        }
        super.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected void getOrderedResultLoop(SessionLocal sessionLocal, HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        WindowFrame windowFrame = this.over.getWindowFrame();
        boolean z = windowFrame == null || (windowFrame.getUnits() != WindowFrameUnits.ROWS && windowFrame.getExclusion().isGroupOrNoOthers());
        if (windowFrame == null) {
            aggregateFastPartition(sessionLocal, hashMap, arrayList, i, z);
            return;
        }
        boolean isVariableBounds = windowFrame.isVariableBounds();
        if (isVariableBounds) {
            isVariableBounds = checkVariableBounds(windowFrame, arrayList);
        }
        if (isVariableBounds) {
            z = false;
        } else if (windowFrame.getExclusion() == WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
            WindowFrameBound following = windowFrame.getFollowing();
            boolean z2 = following != null && following.getType() == WindowFrameBoundType.UNBOUNDED_FOLLOWING;
            if (windowFrame.getStarting().getType() == WindowFrameBoundType.UNBOUNDED_PRECEDING) {
                if (z2) {
                    aggregateWholePartition(sessionLocal, hashMap, arrayList, i);
                    return;
                } else {
                    aggregateFastPartition(sessionLocal, hashMap, arrayList, i, z);
                    return;
                }
            }
            if (z2) {
                aggregateFastPartitionInReverse(sessionLocal, hashMap, arrayList, i, z);
                return;
            }
        }
        int size = arrayList.size();
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < size) {
                Object createAggregateData = createAggregateData();
                Iterator<Value[]> it = WindowFrame.iterator(this.over, sessionLocal, arrayList, getOverOrderBySort(), i3, false);
                while (it.hasNext()) {
                    updateFromExpressions(sessionLocal, createAggregateData, it.next());
                }
                i2 = processGroup(hashMap, getAggregatedValue(sessionLocal, createAggregateData), arrayList, i, i3, size, z);
            } else {
                return;
            }
        }
    }

    private static boolean checkVariableBounds(WindowFrame windowFrame, ArrayList<Value[]> arrayList) {
        int size = arrayList.size();
        WindowFrameBound starting = windowFrame.getStarting();
        if (starting.isVariable()) {
            int expressionIndex = starting.getExpressionIndex();
            Value value = arrayList.get(0)[expressionIndex];
            for (int i = 1; i < size; i++) {
                if (!value.equals(arrayList.get(i)[expressionIndex])) {
                    return true;
                }
            }
        }
        WindowFrameBound following = windowFrame.getFollowing();
        if (following != null && following.isVariable()) {
            int expressionIndex2 = following.getExpressionIndex();
            Value value2 = arrayList.get(0)[expressionIndex2];
            for (int i2 = 1; i2 < size; i2++) {
                if (!value2.equals(arrayList.get(i2)[expressionIndex2])) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void aggregateFastPartition(SessionLocal sessionLocal, HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i, boolean z) {
        Object createAggregateData = createAggregateData();
        int size = arrayList.size();
        int i2 = -1;
        Value value = null;
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 < size) {
                int endIndex = WindowFrame.getEndIndex(this.over, sessionLocal, arrayList, getOverOrderBySort(), i4);
                if (!$assertionsDisabled && endIndex < i2) {
                    throw new AssertionError();
                }
                if (endIndex > i2) {
                    for (int i5 = i2 + 1; i5 <= endIndex; i5++) {
                        updateFromExpressions(sessionLocal, createAggregateData, arrayList.get(i5));
                    }
                    i2 = endIndex;
                    value = getAggregatedValue(sessionLocal, createAggregateData);
                } else if (value == null) {
                    value = getAggregatedValue(sessionLocal, createAggregateData);
                }
                i3 = processGroup(hashMap, value, arrayList, i, i4, size, z);
            } else {
                return;
            }
        }
    }

    private void aggregateFastPartitionInReverse(SessionLocal sessionLocal, HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i, boolean z) {
        SortOrder sortOrder;
        Value[] valueArr;
        Object createAggregateData = createAggregateData();
        int size = arrayList.size();
        Value value = null;
        int i2 = size - 1;
        while (i2 >= 0) {
            int startIndex = this.over.getWindowFrame().getStartIndex(sessionLocal, arrayList, getOverOrderBySort(), i2);
            if (!$assertionsDisabled && startIndex > size) {
                throw new AssertionError();
            }
            if (startIndex < size) {
                for (int i3 = size - 1; i3 >= startIndex; i3--) {
                    updateFromExpressions(sessionLocal, createAggregateData, arrayList.get(i3));
                }
                size = startIndex;
                value = getAggregatedValue(sessionLocal, createAggregateData);
            } else if (value == null) {
                value = getAggregatedValue(sessionLocal, createAggregateData);
            }
            Value[] valueArr2 = arrayList.get(i2);
            Value[] valueArr3 = valueArr2;
            do {
                hashMap.put(Integer.valueOf(valueArr3[i].getInt()), value);
                i2--;
                if (i2 >= 0 && z) {
                    sortOrder = this.overOrderBySort;
                    valueArr = arrayList.get(i2);
                    valueArr3 = valueArr;
                }
            } while (sortOrder.compare(valueArr2, valueArr) == 0);
        }
    }

    private int processGroup(HashMap<Integer, Value> hashMap, Value value, ArrayList<Value[]> arrayList, int i, int i2, int i3, boolean z) {
        SortOrder sortOrder;
        Value[] valueArr;
        Value[] valueArr2 = arrayList.get(i2);
        Value[] valueArr3 = valueArr2;
        do {
            hashMap.put(Integer.valueOf(valueArr3[i].getInt()), value);
            i2++;
            if (i2 >= i3 || !z) {
                break;
            }
            sortOrder = this.overOrderBySort;
            valueArr = arrayList.get(i2);
            valueArr3 = valueArr;
        } while (sortOrder.compare(valueArr2, valueArr) == 0);
        return i2;
    }

    private void aggregateWholePartition(SessionLocal sessionLocal, HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        Object createAggregateData = createAggregateData();
        Iterator<Value[]> it = arrayList.iterator();
        while (it.hasNext()) {
            updateFromExpressions(sessionLocal, createAggregateData, it.next());
        }
        Value aggregatedValue = getAggregatedValue(sessionLocal, createAggregateData);
        Iterator<Value[]> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            hashMap.put(Integer.valueOf(it2.next()[i].getInt()), aggregatedValue);
        }
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected void updateAggregate(SessionLocal sessionLocal, SelectGroups selectGroups, int i) {
        if (this.filterCondition == null || this.filterCondition.getBooleanValue(sessionLocal)) {
            if (this.over != null) {
                if (this.over.isOrdered()) {
                    updateOrderedAggregate(sessionLocal, selectGroups, i, this.over.getOrderBy());
                    return;
                } else {
                    updateAggregate(sessionLocal, getWindowData(sessionLocal, selectGroups, false));
                    return;
                }
            }
            updateAggregate(sessionLocal, getGroupData(selectGroups, false));
            return;
        }
        if (this.over != null && this.over.isOrdered()) {
            updateOrderedAggregate(sessionLocal, selectGroups, i, this.over.getOrderBy());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public void updateGroupAggregates(SessionLocal sessionLocal, int i) {
        if (this.filterCondition != null) {
            this.filterCondition.updateAggregate(sessionLocal, i);
        }
        super.updateGroupAggregates(sessionLocal, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public StringBuilder appendTailConditions(StringBuilder sb, int i, boolean z) {
        if (this.filterCondition != null) {
            sb.append(" FILTER (WHERE ");
            this.filterCondition.getUnenclosedSQL(sb, i).append(')');
        }
        return super.appendTailConditions(sb, i, z);
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.args.length;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        return this.args[i];
    }
}
