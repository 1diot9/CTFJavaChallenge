package org.h2.expression.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFunction.class */
public class WindowFunction extends DataAnalysisOperation {
    private final WindowFunctionType type;
    private final Expression[] args;
    private boolean fromLast;
    private boolean ignoreNulls;

    public static int getMinArgumentCount(WindowFunctionType windowFunctionType) {
        switch (windowFunctionType) {
            case NTILE:
            case LEAD:
            case LAG:
            case FIRST_VALUE:
            case LAST_VALUE:
            case RATIO_TO_REPORT:
                return 1;
            case NTH_VALUE:
                return 2;
            default:
                return 0;
        }
    }

    public static int getMaxArgumentCount(WindowFunctionType windowFunctionType) {
        switch (windowFunctionType) {
            case NTILE:
            case FIRST_VALUE:
            case LAST_VALUE:
            case RATIO_TO_REPORT:
                return 1;
            case LEAD:
            case LAG:
                return 3;
            case NTH_VALUE:
                return 2;
            default:
                return 0;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.h2.value.Value[]] */
    /* JADX WARN: Type inference failed for: r0v8 */
    private static Value getNthValue(Iterator<Value[]> it, int i, boolean z) {
        ValueNull valueNull = ValueNull.INSTANCE;
        int i2 = 0;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ?? r0 = it.next()[0];
            if (!z || r0 != ValueNull.INSTANCE) {
                int i3 = i2;
                i2++;
                if (i3 == i) {
                    valueNull = r0;
                    break;
                }
            }
        }
        return valueNull;
    }

    public WindowFunction(WindowFunctionType windowFunctionType, Select select, Expression[] expressionArr) {
        super(select);
        this.type = windowFunctionType;
        this.args = expressionArr;
    }

    public WindowFunctionType getFunctionType() {
        return this.type;
    }

    public void setFromLast(boolean z) {
        this.fromLast = z;
    }

    public void setIgnoreNulls(boolean z) {
        this.ignoreNulls = z;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public boolean isAggregate() {
        return false;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected void updateAggregate(SessionLocal sessionLocal, SelectGroups selectGroups, int i) {
        updateOrderedAggregate(sessionLocal, selectGroups, i, this.over.getOrderBy());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public void updateGroupAggregates(SessionLocal sessionLocal, int i) {
        super.updateGroupAggregates(sessionLocal, i);
        if (this.args != null) {
            for (Expression expression : this.args) {
                expression.updateAggregate(sessionLocal, i);
            }
        }
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected int getNumExpressions() {
        if (this.args != null) {
            return this.args.length;
        }
        return 0;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected void rememberExpressions(SessionLocal sessionLocal, Value[] valueArr) {
        if (this.args != null) {
            int length = this.args.length;
            for (int i = 0; i < length; i++) {
                valueArr[i] = this.args[i].getValue(sessionLocal);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public Object createAggregateData() {
        throw DbException.getUnsupportedException("Window function");
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected void getOrderedResultLoop(SessionLocal sessionLocal, HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        switch (this.type) {
            case NTILE:
                getNtile(hashMap, arrayList, i);
                return;
            case LEAD:
            case LAG:
                getLeadLag(hashMap, arrayList, i, sessionLocal);
                return;
            case FIRST_VALUE:
            case LAST_VALUE:
            case NTH_VALUE:
                getNth(sessionLocal, hashMap, arrayList, i);
                return;
            case RATIO_TO_REPORT:
                getRatioToReport(hashMap, arrayList, i);
                return;
            case ROW_NUMBER:
                int i2 = 0;
                int size = arrayList.size();
                while (i2 < size) {
                    Integer valueOf = Integer.valueOf(arrayList.get(i2)[i].getInt());
                    i2++;
                    hashMap.put(valueOf, ValueBigint.get(i2));
                }
                return;
            case RANK:
            case DENSE_RANK:
            case PERCENT_RANK:
                getRank(hashMap, arrayList, i);
                return;
            case CUME_DIST:
                getCumeDist(hashMap, arrayList, i);
                return;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
    }

    private void getRank(HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        Value value;
        int size = arrayList.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            Value[] valueArr = arrayList.get(i3);
            if (i3 == 0) {
                i2 = 1;
            } else if (getOverOrderBySort().compare(arrayList.get(i3 - 1), valueArr) != 0) {
                if (this.type == WindowFunctionType.DENSE_RANK) {
                    i2++;
                } else {
                    i2 = i3 + 1;
                }
            }
            if (this.type == WindowFunctionType.PERCENT_RANK) {
                int i4 = i2 - 1;
                value = i4 == 0 ? ValueDouble.ZERO : ValueDouble.get(i4 / (size - 1));
            } else {
                value = ValueBigint.get(i2);
            }
            hashMap.put(Integer.valueOf(valueArr[i].getInt()), value);
        }
    }

    private void getCumeDist(HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        int size = arrayList.size();
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < size) {
                Value[] valueArr = arrayList.get(i3);
                int i4 = i3 + 1;
                while (i4 < size && this.overOrderBySort.compare(valueArr, arrayList.get(i4)) == 0) {
                    i4++;
                }
                ValueDouble valueDouble = ValueDouble.get(i4 / size);
                for (int i5 = i3; i5 < i4; i5++) {
                    hashMap.put(Integer.valueOf(arrayList.get(i5)[i].getInt()), valueDouble);
                }
                i2 = i4;
            } else {
                return;
            }
        }
    }

    private static void getNtile(HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        long j;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            long j2 = arrayList.get(i2)[0].getLong();
            if (j2 <= 0) {
                throw DbException.getInvalidValueException("number of tiles", Long.valueOf(j2));
            }
            long j3 = size / j2;
            long j4 = size - (j3 * j2);
            long j5 = j4 * (j3 + 1);
            if (i2 >= j5) {
                j = ((i2 - j5) / j3) + j4;
            } else {
                j = i2 / (j3 + 1);
            }
            hashMap.put(Integer.valueOf(arrayList.get(i2)[i].getInt()), ValueBigint.get(j + 1));
        }
    }

    private void getLeadLag(HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i, SessionLocal sessionLocal) {
        int i2;
        int size = arrayList.size();
        int numExpressions = getNumExpressions();
        TypeInfo type = this.args[0].getType();
        for (int i3 = 0; i3 < size; i3++) {
            Value[] valueArr = arrayList.get(i3);
            int i4 = valueArr[i].getInt();
            if (numExpressions >= 2) {
                i2 = valueArr[1].getInt();
                if (i2 < 0) {
                    throw DbException.getInvalidValueException("nth row", Integer.valueOf(i2));
                }
            } else {
                i2 = 1;
            }
            Value value = null;
            if (i2 == 0) {
                value = arrayList.get(i3)[0];
            } else if (this.type == WindowFunctionType.LEAD) {
                if (this.ignoreNulls) {
                    for (int i5 = i3 + 1; i2 > 0 && i5 < size; i5++) {
                        value = arrayList.get(i5)[0];
                        if (value != ValueNull.INSTANCE) {
                            i2--;
                        }
                    }
                    if (i2 > 0) {
                        value = null;
                    }
                } else if (i2 <= (size - i3) - 1) {
                    value = arrayList.get(i3 + i2)[0];
                }
            } else if (this.ignoreNulls) {
                for (int i6 = i3 - 1; i2 > 0 && i6 >= 0; i6--) {
                    value = arrayList.get(i6)[0];
                    if (value != ValueNull.INSTANCE) {
                        i2--;
                    }
                }
                if (i2 > 0) {
                    value = null;
                }
            } else if (i2 <= i3) {
                value = arrayList.get(i3 - i2)[0];
            }
            if (value == null) {
                if (numExpressions >= 3) {
                    value = valueArr[2].convertTo(type, sessionLocal);
                } else {
                    value = ValueNull.INSTANCE;
                }
            }
            hashMap.put(Integer.valueOf(i4), value);
        }
    }

    private void getNth(SessionLocal sessionLocal, HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        Value nthValue;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            Value[] valueArr = arrayList.get(i2);
            int i3 = valueArr[i].getInt();
            switch (this.type) {
                case FIRST_VALUE:
                    nthValue = getNthValue(WindowFrame.iterator(this.over, sessionLocal, arrayList, getOverOrderBySort(), i2, false), 0, this.ignoreNulls);
                    break;
                case LAST_VALUE:
                    nthValue = getNthValue(WindowFrame.iterator(this.over, sessionLocal, arrayList, getOverOrderBySort(), i2, true), 0, this.ignoreNulls);
                    break;
                case RATIO_TO_REPORT:
                default:
                    throw DbException.getInternalError("type=" + this.type);
                case NTH_VALUE:
                    int i4 = valueArr[1].getInt();
                    if (i4 <= 0) {
                        throw DbException.getInvalidValueException("nth row", Integer.valueOf(i4));
                    }
                    nthValue = getNthValue(WindowFrame.iterator(this.over, sessionLocal, arrayList, getOverOrderBySort(), i2, this.fromLast), i4 - 1, this.ignoreNulls);
                    break;
            }
            hashMap.put(Integer.valueOf(i3), nthValue);
        }
    }

    private static void getRatioToReport(HashMap<Integer, Value> hashMap, ArrayList<Value[]> arrayList, int i) {
        Value value;
        int size = arrayList.size();
        Value value2 = null;
        for (int i2 = 0; i2 < size; i2++) {
            Value value3 = arrayList.get(i2)[0];
            if (value3 != ValueNull.INSTANCE) {
                if (value2 == null) {
                    value2 = value3.convertToDouble();
                } else {
                    value2 = value2.add(value3.convertToDouble());
                }
            }
        }
        if (value2 != null && value2.getSignum() == 0) {
            value2 = null;
        }
        for (int i3 = 0; i3 < size; i3++) {
            Value[] valueArr = arrayList.get(i3);
            if (value2 == null) {
                value = ValueNull.INSTANCE;
            } else {
                value = valueArr[0];
                if (value != ValueNull.INSTANCE) {
                    value = value.convertToDouble().divide(value2, TypeInfo.TYPE_DOUBLE);
                }
            }
            hashMap.put(Integer.valueOf(valueArr[i].getInt()), value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public Value getAggregatedValue(SessionLocal sessionLocal, Object obj) {
        throw DbException.getUnsupportedException("Window function");
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public void mapColumnsAnalysis(ColumnResolver columnResolver, int i, int i2) {
        if (this.args != null) {
            for (Expression expression : this.args) {
                expression.mapColumns(columnResolver, i, i2);
            }
        }
        super.mapColumnsAnalysis(columnResolver, i, i2);
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        if (this.over.getWindowFrame() != null) {
            switch (this.type) {
                case FIRST_VALUE:
                case LAST_VALUE:
                case NTH_VALUE:
                    break;
                case RATIO_TO_REPORT:
                default:
                    String traceSQL = getTraceSQL();
                    throw DbException.getSyntaxError(traceSQL, traceSQL.length() - 1);
            }
        }
        if (this.over.getOrderBy() == null) {
            if (this.type.requiresWindowOrdering()) {
                String traceSQL2 = getTraceSQL();
                throw DbException.getSyntaxError(traceSQL2, traceSQL2.length() - 1, "ORDER BY");
            }
        } else if (this.type == WindowFunctionType.RATIO_TO_REPORT) {
            String traceSQL3 = getTraceSQL();
            throw DbException.getSyntaxError(traceSQL3, traceSQL3.length() - 1);
        }
        super.optimize(sessionLocal);
        if (this.over.getOrderBy() == null) {
            switch (this.type) {
                case RANK:
                case DENSE_RANK:
                    return ValueExpression.get(ValueBigint.get(1L));
                case PERCENT_RANK:
                    return ValueExpression.get(ValueDouble.ZERO);
                case CUME_DIST:
                    return ValueExpression.get(ValueDouble.ONE);
            }
        }
        if (this.args != null) {
            for (int i = 0; i < this.args.length; i++) {
                this.args[i] = this.args[i].optimize(sessionLocal);
            }
        }
        return this;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        if (this.args != null) {
            for (Expression expression : this.args) {
                expression.setEvaluatable(tableFilter, z);
            }
        }
        super.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        switch (this.type) {
            case NTILE:
            case ROW_NUMBER:
            case RANK:
            case DENSE_RANK:
                return TypeInfo.TYPE_BIGINT;
            case LEAD:
            case LAG:
            case FIRST_VALUE:
            case LAST_VALUE:
            case NTH_VALUE:
                return this.args[0].getType();
            case RATIO_TO_REPORT:
            case PERCENT_RANK:
            case CUME_DIST:
                return TypeInfo.TYPE_DOUBLE;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append(this.type.getSQL()).append('(');
        if (this.args != null) {
            writeExpressions(sb, this.args, i);
        }
        sb.append(')');
        if (this.fromLast && this.type == WindowFunctionType.NTH_VALUE) {
            sb.append(" FROM LAST");
        }
        if (this.ignoreNulls) {
            switch (this.type) {
                case LEAD:
                case LAG:
                case FIRST_VALUE:
                case LAST_VALUE:
                case NTH_VALUE:
                    sb.append(" IGNORE NULLS");
                    break;
            }
        }
        return appendTailConditions(sb, i, this.type.requiresWindowOrdering());
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = 1;
        if (this.args != null) {
            for (Expression expression : this.args) {
                i += expression.getCost();
            }
        }
        return i;
    }
}
