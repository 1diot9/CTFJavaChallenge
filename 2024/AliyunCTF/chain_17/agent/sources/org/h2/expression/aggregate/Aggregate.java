package org.h2.expression.aggregate;

import ch.qos.logback.core.joran.JoranConstants;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ExpressionWithFlags;
import org.h2.expression.analysis.Window;
import org.h2.expression.function.JsonConstructorFunction;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVSpatialIndex;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.util.json.JsonConstructorUtils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/Aggregate.class */
public class Aggregate extends AbstractAggregate implements ExpressionWithFlags {
    private static final int ADDITIONAL_SUM_PRECISION = 10;
    private static final int ADDITIONAL_AVG_SCALE = 10;
    private static final HashMap<String, AggregateType> AGGREGATES = new HashMap<>(128);
    private final AggregateType aggregateType;
    private ArrayList<QueryOrderBy> orderByList;
    private SortOrder orderBySort;
    private Object extraArguments;
    private int flags;

    static {
        addAggregate("COUNT", AggregateType.COUNT);
        addAggregate("SUM", AggregateType.SUM);
        addAggregate("MIN", AggregateType.MIN);
        addAggregate("MAX", AggregateType.MAX);
        addAggregate("AVG", AggregateType.AVG);
        addAggregate("LISTAGG", AggregateType.LISTAGG);
        addAggregate("GROUP_CONCAT", AggregateType.LISTAGG);
        addAggregate("STRING_AGG", AggregateType.LISTAGG);
        addAggregate("STDDEV_SAMP", AggregateType.STDDEV_SAMP);
        addAggregate("STDDEV", AggregateType.STDDEV_SAMP);
        addAggregate("STDDEV_POP", AggregateType.STDDEV_POP);
        addAggregate("STDDEVP", AggregateType.STDDEV_POP);
        addAggregate("VAR_POP", AggregateType.VAR_POP);
        addAggregate("VARP", AggregateType.VAR_POP);
        addAggregate("VAR_SAMP", AggregateType.VAR_SAMP);
        addAggregate("VAR", AggregateType.VAR_SAMP);
        addAggregate("VARIANCE", AggregateType.VAR_SAMP);
        addAggregate("ANY_VALUE", AggregateType.ANY_VALUE);
        addAggregate("ANY", AggregateType.ANY);
        addAggregate("SOME", AggregateType.ANY);
        addAggregate("BOOL_OR", AggregateType.ANY);
        addAggregate("EVERY", AggregateType.EVERY);
        addAggregate("BOOL_AND", AggregateType.EVERY);
        addAggregate("HISTOGRAM", AggregateType.HISTOGRAM);
        addAggregate("BIT_AND_AGG", AggregateType.BIT_AND_AGG);
        addAggregate("BIT_AND", AggregateType.BIT_AND_AGG);
        addAggregate("BIT_OR_AGG", AggregateType.BIT_OR_AGG);
        addAggregate("BIT_OR", AggregateType.BIT_OR_AGG);
        addAggregate("BIT_XOR_AGG", AggregateType.BIT_XOR_AGG);
        addAggregate("BIT_NAND_AGG", AggregateType.BIT_NAND_AGG);
        addAggregate("BIT_NOR_AGG", AggregateType.BIT_NOR_AGG);
        addAggregate("BIT_XNOR_AGG", AggregateType.BIT_XNOR_AGG);
        addAggregate("COVAR_POP", AggregateType.COVAR_POP);
        addAggregate("COVAR_SAMP", AggregateType.COVAR_SAMP);
        addAggregate("CORR", AggregateType.CORR);
        addAggregate("REGR_SLOPE", AggregateType.REGR_SLOPE);
        addAggregate("REGR_INTERCEPT", AggregateType.REGR_INTERCEPT);
        addAggregate("REGR_COUNT", AggregateType.REGR_COUNT);
        addAggregate("REGR_R2", AggregateType.REGR_R2);
        addAggregate("REGR_AVGX", AggregateType.REGR_AVGX);
        addAggregate("REGR_AVGY", AggregateType.REGR_AVGY);
        addAggregate("REGR_SXX", AggregateType.REGR_SXX);
        addAggregate("REGR_SYY", AggregateType.REGR_SYY);
        addAggregate("REGR_SXY", AggregateType.REGR_SXY);
        addAggregate("RANK", AggregateType.RANK);
        addAggregate("DENSE_RANK", AggregateType.DENSE_RANK);
        addAggregate("PERCENT_RANK", AggregateType.PERCENT_RANK);
        addAggregate("CUME_DIST", AggregateType.CUME_DIST);
        addAggregate("PERCENTILE_CONT", AggregateType.PERCENTILE_CONT);
        addAggregate("PERCENTILE_DISC", AggregateType.PERCENTILE_DISC);
        addAggregate("MEDIAN", AggregateType.MEDIAN);
        addAggregate("ARRAY_AGG", AggregateType.ARRAY_AGG);
        addAggregate("MODE", AggregateType.MODE);
        addAggregate("STATS_MODE", AggregateType.MODE);
        addAggregate("ENVELOPE", AggregateType.ENVELOPE);
        addAggregate("JSON_OBJECTAGG", AggregateType.JSON_OBJECTAGG);
        addAggregate("JSON_ARRAYAGG", AggregateType.JSON_ARRAYAGG);
    }

    public Aggregate(AggregateType aggregateType, Expression[] expressionArr, Select select, boolean z) {
        super(select, expressionArr, z);
        if (z && aggregateType == AggregateType.COUNT_ALL) {
            throw DbException.getInternalError();
        }
        this.aggregateType = aggregateType;
    }

    private static void addAggregate(String str, AggregateType aggregateType) {
        AGGREGATES.put(str, aggregateType);
    }

    public static AggregateType getAggregateType(String str) {
        return AGGREGATES.get(str);
    }

    public void setOrderByList(ArrayList<QueryOrderBy> arrayList) {
        this.orderByList = arrayList;
    }

    public AggregateType getAggregateType() {
        return this.aggregateType;
    }

    public void setExtraArguments(Object obj) {
        this.extraArguments = obj;
    }

    public Object getExtraArguments() {
        return this.extraArguments;
    }

    @Override // org.h2.expression.ExpressionWithFlags
    public void setFlags(int i) {
        this.flags = i;
    }

    @Override // org.h2.expression.ExpressionWithFlags
    public int getFlags() {
        return this.flags;
    }

    private void sortWithOrderBy(Value[] valueArr) {
        SortOrder sortOrder = this.orderBySort;
        Arrays.sort(valueArr, sortOrder != null ? (value, value2) -> {
            return sortOrder.compare(((ValueRow) value).getList(), ((ValueRow) value2).getList());
        } : this.select.getSession().getDatabase().getCompareMode());
    }

    @Override // org.h2.expression.aggregate.AbstractAggregate
    protected void updateAggregate(SessionLocal sessionLocal, Object obj) {
        updateData(sessionLocal, (AggregateData) obj, this.args.length == 0 ? null : this.args[0].getValue(sessionLocal), null);
    }

    private void updateData(SessionLocal sessionLocal, AggregateData aggregateData, Value value, Value[] valueArr) {
        Value secondValue;
        switch (this.aggregateType) {
            case COVAR_POP:
            case COVAR_SAMP:
            case CORR:
            case REGR_SLOPE:
            case REGR_INTERCEPT:
            case REGR_R2:
            case REGR_SXY:
                if (value == ValueNull.INSTANCE || (secondValue = getSecondValue(sessionLocal, valueArr)) == ValueNull.INSTANCE) {
                    return;
                }
                ((AggregateDataBinarySet) aggregateData).add(sessionLocal, value, secondValue);
                return;
            case REGR_COUNT:
            case REGR_AVGY:
            case REGR_SYY:
                if (value == ValueNull.INSTANCE || getSecondValue(sessionLocal, valueArr) == ValueNull.INSTANCE) {
                    return;
                }
                break;
            case REGR_AVGX:
            case REGR_SXX:
                if (value == ValueNull.INSTANCE) {
                    return;
                }
                Value secondValue2 = getSecondValue(sessionLocal, valueArr);
                value = secondValue2;
                if (secondValue2 == ValueNull.INSTANCE) {
                    return;
                }
                break;
            case LISTAGG:
                if (value == ValueNull.INSTANCE) {
                    return;
                }
                value = updateCollecting(sessionLocal, value.convertTo(TypeInfo.TYPE_VARCHAR), valueArr);
                break;
            case ARRAY_AGG:
                value = updateCollecting(sessionLocal, value, valueArr);
                break;
            case RANK:
            case DENSE_RANK:
            case PERCENT_RANK:
            case CUME_DIST:
                int length = this.args.length;
                Value[] valueArr2 = new Value[length];
                for (int i = 0; i < length; i++) {
                    valueArr2[i] = valueArr != null ? valueArr[i] : this.args[i].getValue(sessionLocal);
                }
                ((AggregateDataCollecting) aggregateData).setSharedArgument(ValueRow.get(valueArr2));
                Value[] valueArr3 = new Value[length];
                for (int i2 = 0; i2 < length; i2++) {
                    valueArr3[i2] = valueArr != null ? valueArr[length + i2] : this.orderByList.get(i2).expression.getValue(sessionLocal);
                }
                value = ValueRow.get(valueArr3);
                break;
            case PERCENTILE_CONT:
            case PERCENTILE_DISC:
                ((AggregateDataCollecting) aggregateData).setSharedArgument(value);
                value = valueArr != null ? valueArr[1] : this.orderByList.get(0).expression.getValue(sessionLocal);
                break;
            case MODE:
                value = valueArr != null ? valueArr[0] : this.orderByList.get(0).expression.getValue(sessionLocal);
                break;
            case JSON_ARRAYAGG:
                value = updateCollecting(sessionLocal, value, valueArr);
                break;
            case JSON_OBJECTAGG:
                Value secondValue3 = getSecondValue(sessionLocal, valueArr);
                if (value == ValueNull.INSTANCE) {
                    throw DbException.getInvalidValueException("JSON_OBJECTAGG key", JoranConstants.NULL);
                }
                value = ValueRow.get(new Value[]{value, secondValue3});
                break;
        }
        aggregateData.add(sessionLocal, value);
    }

    private Value getSecondValue(SessionLocal sessionLocal, Value[] valueArr) {
        return valueArr != null ? valueArr[1] : this.args[1].getValue(sessionLocal);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.aggregate.AbstractAggregate, org.h2.expression.analysis.DataAnalysisOperation
    public void updateGroupAggregates(SessionLocal sessionLocal, int i) {
        super.updateGroupAggregates(sessionLocal, i);
        for (Expression expression : this.args) {
            expression.updateAggregate(sessionLocal, i);
        }
        if (this.orderByList != null) {
            Iterator<QueryOrderBy> it = this.orderByList.iterator();
            while (it.hasNext()) {
                it.next().expression.updateAggregate(sessionLocal, i);
            }
        }
    }

    private Value updateCollecting(SessionLocal sessionLocal, Value value, Value[] valueArr) {
        if (this.orderByList != null) {
            int size = this.orderByList.size();
            Value[] valueArr2 = new Value[1 + size];
            valueArr2[0] = value;
            if (valueArr == null) {
                for (int i = 0; i < size; i++) {
                    valueArr2[i + 1] = this.orderByList.get(i).expression.getValue(sessionLocal);
                }
            } else {
                System.arraycopy(valueArr, 1, valueArr2, 1, size);
            }
            value = ValueRow.get(valueArr2);
        }
        return value;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected int getNumExpressions() {
        int length = this.args.length;
        if (this.orderByList != null) {
            length += this.orderByList.size();
        }
        if (this.filterCondition != null) {
            length++;
        }
        return length;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected void rememberExpressions(SessionLocal sessionLocal, Value[] valueArr) {
        int i = 0;
        for (Expression expression : this.args) {
            int i2 = i;
            i++;
            valueArr[i2] = expression.getValue(sessionLocal);
        }
        if (this.orderByList != null) {
            Iterator<QueryOrderBy> it = this.orderByList.iterator();
            while (it.hasNext()) {
                int i3 = i;
                i++;
                valueArr[i3] = it.next().expression.getValue(sessionLocal);
            }
        }
        if (this.filterCondition != null) {
            valueArr[i] = ValueBoolean.get(this.filterCondition.getBooleanValue(sessionLocal));
        }
    }

    @Override // org.h2.expression.aggregate.AbstractAggregate
    protected void updateFromExpressions(SessionLocal sessionLocal, Object obj, Value[] valueArr) {
        if (this.filterCondition == null || valueArr[getNumExpressions() - 1].isTrue()) {
            updateData(sessionLocal, (AggregateData) obj, this.args.length == 0 ? null : valueArr[0], valueArr);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x00ec, code lost:            if (r7.distinct != false) goto L60;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0106, code lost:            if (r7.distinct != false) goto L60;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x011c, code lost:            if (r7.distinct != false) goto L60;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x000b. Please report as an issue. */
    @Override // org.h2.expression.analysis.DataAnalysisOperation
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object createAggregateData() {
        /*
            Method dump skipped, instructions count: 499
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.aggregate.Aggregate.createAggregateData():java.lang.Object");
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return this.select.isQuickAggregateQuery() ? getValueQuick(sessionLocal) : super.getValue(sessionLocal);
    }

    private Value getValueQuick(SessionLocal sessionLocal) {
        Value value;
        switch (this.aggregateType) {
            case PERCENTILE_CONT:
            case PERCENTILE_DISC:
                Value value2 = this.args[0].getValue(sessionLocal);
                if (value2 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                BigDecimal bigDecimal = value2.getBigDecimal();
                if (bigDecimal.signum() < 0 || bigDecimal.compareTo(BigDecimal.ONE) > 0) {
                    throw DbException.getInvalidValueException(this.aggregateType == AggregateType.PERCENTILE_CONT ? "PERCENTILE_CONT argument" : "PERCENTILE_DISC argument", bigDecimal);
                }
                return Percentile.getFromIndex(sessionLocal, this.orderByList.get(0).expression, this.type.getValueType(), this.orderByList, bigDecimal, this.aggregateType == AggregateType.PERCENTILE_CONT);
            case MODE:
            case JSON_ARRAYAGG:
            case JSON_OBJECTAGG:
            case SUM:
            case BIT_XOR_AGG:
            case BIT_XNOR_AGG:
            case BIT_AND_AGG:
            case BIT_OR_AGG:
            case BIT_NAND_AGG:
            case BIT_NOR_AGG:
            case ANY:
            case EVERY:
            case AVG:
            case STDDEV_POP:
            case STDDEV_SAMP:
            case VAR_POP:
            case VAR_SAMP:
            case HISTOGRAM:
            case ANY_VALUE:
            default:
                throw DbException.getInternalError("type=" + this.aggregateType);
            case COUNT_ALL:
            case COUNT:
                return ValueBigint.get(this.select.getTopTableFilter().getTable().getRowCount(sessionLocal));
            case MEDIAN:
                return Percentile.getFromIndex(sessionLocal, this.args[0], this.type.getValueType(), this.orderByList, Percentile.HALF, true);
            case MIN:
            case MAX:
                boolean z = this.aggregateType == AggregateType.MIN;
                Index minMaxColumnIndex = getMinMaxColumnIndex();
                if ((minMaxColumnIndex.getIndexColumns()[0].sortType & 1) != 0) {
                    z = !z;
                }
                SearchRow searchRow = minMaxColumnIndex.findFirstOrLast(sessionLocal, z).getSearchRow();
                if (searchRow == null) {
                    value = ValueNull.INSTANCE;
                } else {
                    value = searchRow.getValue(minMaxColumnIndex.getColumns()[0].getColumnId());
                }
                return value;
            case ENVELOPE:
                return ((MVSpatialIndex) AggregateDataEnvelope.getGeometryColumnIndex(this.args[0])).getBounds(sessionLocal);
        }
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public Value getAggregatedValue(SessionLocal sessionLocal, Object obj) {
        AggregateData aggregateData = (AggregateData) obj;
        if (aggregateData == null) {
            aggregateData = (AggregateData) createAggregateData();
        }
        switch (this.aggregateType) {
            case LISTAGG:
                return getListagg(sessionLocal, aggregateData);
            case ARRAY_AGG:
                Value[] array = ((AggregateDataCollecting) aggregateData).getArray();
                if (array == null) {
                    return ValueNull.INSTANCE;
                }
                if (this.orderByList != null || this.distinct) {
                    sortWithOrderBy(array);
                }
                if (this.orderByList != null) {
                    for (int i = 0; i < array.length; i++) {
                        array[i] = ((ValueRow) array[i]).getList()[0];
                    }
                }
                return ValueArray.get((TypeInfo) this.type.getExtTypeInfo(), array, sessionLocal);
            case RANK:
            case DENSE_RANK:
            case PERCENT_RANK:
            case CUME_DIST:
                return getHypotheticalSet(sessionLocal, aggregateData);
            case PERCENTILE_CONT:
            case PERCENTILE_DISC:
                AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting) aggregateData;
                Value[] array2 = aggregateDataCollecting.getArray();
                if (array2 == null) {
                    return ValueNull.INSTANCE;
                }
                Value sharedArgument = aggregateDataCollecting.getSharedArgument();
                if (sharedArgument == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                BigDecimal bigDecimal = sharedArgument.getBigDecimal();
                if (bigDecimal.signum() < 0 || bigDecimal.compareTo(BigDecimal.ONE) > 0) {
                    throw DbException.getInvalidValueException(this.aggregateType == AggregateType.PERCENTILE_CONT ? "PERCENTILE_CONT argument" : "PERCENTILE_DISC argument", bigDecimal);
                }
                return Percentile.getValue(sessionLocal, array2, this.type.getValueType(), this.orderByList, bigDecimal, this.aggregateType == AggregateType.PERCENTILE_CONT);
            case MODE:
                return getMode(sessionLocal, aggregateData);
            case JSON_ARRAYAGG:
                Value[] array3 = ((AggregateDataCollecting) aggregateData).getArray();
                if (array3 == null) {
                    return ValueNull.INSTANCE;
                }
                if (this.orderByList != null) {
                    sortWithOrderBy(array3);
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(91);
                int length = array3.length;
                for (int i2 = 0; i2 < length; i2++) {
                    Value value = array3[i2];
                    if (this.orderByList != null) {
                        value = ((ValueRow) value).getList()[0];
                    }
                    JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, value != ValueNull.INSTANCE ? value : ValueJson.NULL, this.flags);
                }
                byteArrayOutputStream.write(93);
                return ValueJson.getInternal(byteArrayOutputStream.toByteArray());
            case JSON_OBJECTAGG:
                Value[] array4 = ((AggregateDataCollecting) aggregateData).getArray();
                if (array4 == null) {
                    return ValueNull.INSTANCE;
                }
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                byteArrayOutputStream2.write(123);
                for (Value value2 : array4) {
                    Value[] list = ((ValueRow) value2).getList();
                    String string = list[0].getString();
                    if (string == null) {
                        throw DbException.getInvalidValueException("JSON_OBJECTAGG key", JoranConstants.NULL);
                    }
                    Value value3 = list[1];
                    if (value3 == ValueNull.INSTANCE) {
                        if ((this.flags & 1) == 0) {
                            value3 = ValueJson.NULL;
                        }
                    }
                    JsonConstructorUtils.jsonObjectAppend(byteArrayOutputStream2, string, value3);
                }
                return JsonConstructorUtils.jsonObjectFinish(byteArrayOutputStream2, this.flags);
            case COUNT:
                if (this.distinct) {
                    return ValueBigint.get(((AggregateDataCollecting) aggregateData).getCount());
                }
                break;
            case MEDIAN:
                Value[] array5 = ((AggregateDataCollecting) aggregateData).getArray();
                if (array5 == null) {
                    return ValueNull.INSTANCE;
                }
                return Percentile.getValue(sessionLocal, array5, this.type.getValueType(), this.orderByList, Percentile.HALF, true);
            case SUM:
            case BIT_XOR_AGG:
            case BIT_XNOR_AGG:
                if (this.distinct) {
                    AggregateDataCollecting aggregateDataCollecting2 = (AggregateDataCollecting) aggregateData;
                    if (aggregateDataCollecting2.getCount() == 0) {
                        return ValueNull.INSTANCE;
                    }
                    return collect(sessionLocal, aggregateDataCollecting2, new AggregateDataDefault(this.aggregateType, this.type));
                }
                break;
            case AVG:
                if (this.distinct) {
                    AggregateDataCollecting aggregateDataCollecting3 = (AggregateDataCollecting) aggregateData;
                    if (aggregateDataCollecting3.getCount() == 0) {
                        return ValueNull.INSTANCE;
                    }
                    return collect(sessionLocal, aggregateDataCollecting3, new AggregateDataAvg(this.type));
                }
                break;
            case STDDEV_POP:
            case STDDEV_SAMP:
            case VAR_POP:
            case VAR_SAMP:
                if (this.distinct) {
                    AggregateDataCollecting aggregateDataCollecting4 = (AggregateDataCollecting) aggregateData;
                    if (aggregateDataCollecting4.getCount() == 0) {
                        return ValueNull.INSTANCE;
                    }
                    return collect(sessionLocal, aggregateDataCollecting4, new AggregateDataStdVar(this.aggregateType));
                }
                break;
            case HISTOGRAM:
                return getHistogram(sessionLocal, aggregateData);
            case ANY_VALUE:
                if (this.distinct) {
                    Value[] array6 = ((AggregateDataCollecting) aggregateData).getArray();
                    if (array6 == null) {
                        return ValueNull.INSTANCE;
                    }
                    return array6[sessionLocal.getRandom().nextInt(array6.length)];
                }
                break;
        }
        return aggregateData.getValue(sessionLocal);
    }

    private static Value collect(SessionLocal sessionLocal, AggregateDataCollecting aggregateDataCollecting, AggregateData aggregateData) {
        Iterator<Value> it = aggregateDataCollecting.iterator();
        while (it.hasNext()) {
            aggregateData.add(sessionLocal, it.next());
        }
        return aggregateData.getValue(sessionLocal);
    }

    private Value getHypotheticalSet(SessionLocal sessionLocal, AggregateData aggregateData) {
        AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting) aggregateData;
        Value sharedArgument = aggregateDataCollecting.getSharedArgument();
        if (sharedArgument == null) {
            switch (this.aggregateType) {
                case RANK:
                case DENSE_RANK:
                    return ValueInteger.get(1);
                case PERCENT_RANK:
                    return ValueDouble.ZERO;
                case CUME_DIST:
                    return ValueDouble.ONE;
                default:
                    throw DbException.getUnsupportedException("aggregateType=" + this.aggregateType);
            }
        }
        aggregateDataCollecting.add(sessionLocal, sharedArgument);
        Value[] array = aggregateDataCollecting.getArray();
        Comparator<Value> rowValueComparator = this.orderBySort.getRowValueComparator();
        Arrays.sort(array, rowValueComparator);
        return this.aggregateType == AggregateType.CUME_DIST ? getCumeDist(array, sharedArgument, rowValueComparator) : getRank(array, sharedArgument, rowValueComparator);
    }

    private Value getRank(Value[] valueArr, Value value, Comparator<Value> comparator) {
        Value value2;
        int length = valueArr.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            Value value3 = valueArr[i2];
            if (i2 == 0) {
                i = 1;
            } else if (comparator.compare(valueArr[i2 - 1], value3) != 0) {
                if (this.aggregateType == AggregateType.DENSE_RANK) {
                    i++;
                } else {
                    i = i2 + 1;
                }
            }
            if (this.aggregateType == AggregateType.PERCENT_RANK) {
                int i3 = i - 1;
                value2 = i3 == 0 ? ValueDouble.ZERO : ValueDouble.get(i3 / (length - 1));
            } else {
                value2 = ValueBigint.get(i);
            }
            if (comparator.compare(value3, value) == 0) {
                return value2;
            }
        }
        throw DbException.getInternalError();
    }

    private static Value getCumeDist(Value[] valueArr, Value value, Comparator<Value> comparator) {
        int length = valueArr.length;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < length) {
                Value value2 = valueArr[i2];
                int i3 = i2 + 1;
                while (i3 < length && comparator.compare(value2, valueArr[i3]) == 0) {
                    i3++;
                }
                ValueDouble valueDouble = ValueDouble.get(i3 / length);
                for (int i4 = i2; i4 < i3; i4++) {
                    if (comparator.compare(valueArr[i4], value) == 0) {
                        return valueDouble;
                    }
                }
                i = i3;
            } else {
                throw DbException.getInternalError();
            }
        }
    }

    private Value getListagg(SessionLocal sessionLocal, AggregateData aggregateData) {
        StringBuilder listaggError;
        Value[] array = ((AggregateDataCollecting) aggregateData).getArray();
        if (array == null) {
            return ValueNull.INSTANCE;
        }
        if (array.length == 1) {
            Value value = array[0];
            if (this.orderByList != null) {
                value = ((ValueRow) value).getList()[0];
            }
            return value.convertTo(2, sessionLocal);
        }
        if (this.orderByList != null || this.distinct) {
            sortWithOrderBy(array);
        }
        ListaggArguments listaggArguments = (ListaggArguments) this.extraArguments;
        String effectiveSeparator = listaggArguments.getEffectiveSeparator();
        if (listaggArguments.getOnOverflowTruncate()) {
            listaggError = getListaggTruncate(array, effectiveSeparator, listaggArguments.getEffectiveFilter(), listaggArguments.isWithoutCount());
        } else {
            listaggError = getListaggError(array, effectiveSeparator);
        }
        return ValueVarchar.get(listaggError.toString(), sessionLocal);
    }

    private StringBuilder getListaggError(Value[] valueArr, String str) {
        StringBuilder sb = new StringBuilder(getListaggItem(valueArr[0]));
        int length = valueArr.length;
        for (int i = 1; i < length; i++) {
            String listaggItem = getListaggItem(valueArr[i]);
            if (sb.length() + str.length() + listaggItem.length() > DateTimeUtils.NANOS_PER_SECOND) {
                StringUtils.appendToLength(sb, str, 81);
                StringUtils.appendToLength(sb, listaggItem, 81);
                throw DbException.getValueTooLongException("CHARACTER VARYING", sb.substring(0, 81), -1L);
            }
            sb.append(str).append(listaggItem);
        }
        return sb;
    }

    private StringBuilder getListaggTruncate(Value[] valueArr, String str, String str2, boolean z) {
        int length = valueArr.length;
        String[] strArr = new String[length];
        String listaggItem = getListaggItem(valueArr[0]);
        strArr[0] = listaggItem;
        StringBuilder sb = new StringBuilder((int) Math.min(DateTimeUtils.NANOS_PER_SECOND, listaggItem.length() * length));
        sb.append(listaggItem);
        int i = 1;
        while (true) {
            if (i >= length) {
                break;
            }
            String listaggItem2 = getListaggItem(valueArr[i]);
            strArr[i] = listaggItem2;
            int length2 = sb.length();
            long length3 = length2 + str.length() + listaggItem2.length();
            if (length3 > DateTimeUtils.NANOS_PER_SECOND) {
                if (length3 - listaggItem2.length() >= DateTimeUtils.NANOS_PER_SECOND) {
                    i--;
                } else {
                    sb.append(str);
                    length2 = (int) length3;
                }
                while (true) {
                    if (i > 0) {
                        int length4 = length2 - strArr[i].length();
                        sb.setLength(length4);
                        StringUtils.appendToLength(sb, str2, 1000000001);
                        if (!z) {
                            sb.append('(').append(length - i).append(')');
                        }
                        if (sb.length() <= 1000000000) {
                            break;
                        }
                        length2 = length4 - str.length();
                        i--;
                    } else {
                        sb.setLength(0);
                        sb.append(str2).append('(').append(length).append(')');
                        break;
                    }
                }
            } else {
                sb.append(str).append(listaggItem2);
                i++;
            }
        }
        return sb;
    }

    private String getListaggItem(Value value) {
        if (this.orderByList != null) {
            value = ((ValueRow) value).getList()[0];
        }
        return value.getString();
    }

    private Value getHistogram(SessionLocal sessionLocal, AggregateData aggregateData) {
        TreeMap<Value, LongDataCounter> values = ((AggregateDataDistinctWithCounts) aggregateData).getValues();
        TypeInfo typeInfo = (TypeInfo) this.type.getExtTypeInfo();
        if (values == null) {
            return ValueArray.get(typeInfo, Value.EMPTY_VALUES, sessionLocal);
        }
        ValueRow[] valueRowArr = new ValueRow[values.size()];
        int i = 0;
        for (Map.Entry<Value, LongDataCounter> entry : values.entrySet()) {
            valueRowArr[i] = ValueRow.get(typeInfo, new Value[]{entry.getKey(), ValueBigint.get(entry.getValue().count)});
            i++;
        }
        CompareMode compareMode = sessionLocal.getDatabase().getCompareMode();
        Arrays.sort(valueRowArr, (valueRow, valueRow2) -> {
            return valueRow.getList()[0].compareTo(valueRow2.getList()[0], sessionLocal, compareMode);
        });
        return ValueArray.get(typeInfo, valueRowArr, sessionLocal);
    }

    private Value getMode(SessionLocal sessionLocal, AggregateData aggregateData) {
        Value value = ValueNull.INSTANCE;
        TreeMap<Value, LongDataCounter> values = ((AggregateDataDistinctWithCounts) aggregateData).getValues();
        if (values == null) {
            return value;
        }
        long j = 0;
        if (this.orderByList != null) {
            boolean z = (this.orderByList.get(0).sortType & 1) != 0;
            for (Map.Entry<Value, LongDataCounter> entry : values.entrySet()) {
                long j2 = entry.getValue().count;
                if (j2 > j) {
                    value = entry.getKey();
                    j = j2;
                } else if (j2 == j) {
                    Value key = entry.getKey();
                    int compareTypeSafe = sessionLocal.compareTypeSafe(value, key);
                    if (z) {
                        if (compareTypeSafe < 0) {
                            value = key;
                        }
                    } else if (compareTypeSafe > 0) {
                        value = key;
                    }
                }
            }
        } else {
            for (Map.Entry<Value, LongDataCounter> entry2 : values.entrySet()) {
                long j3 = entry2.getValue().count;
                if (j3 > j) {
                    value = entry2.getKey();
                    j = j3;
                }
            }
        }
        return value;
    }

    @Override // org.h2.expression.aggregate.AbstractAggregate, org.h2.expression.analysis.DataAnalysisOperation
    public void mapColumnsAnalysis(ColumnResolver columnResolver, int i, int i2) {
        if (this.orderByList != null) {
            Iterator<QueryOrderBy> it = this.orderByList.iterator();
            while (it.hasNext()) {
                it.next().expression.mapColumns(columnResolver, i, i2);
            }
        }
        super.mapColumnsAnalysis(columnResolver, i, i2);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:31:0x00c7. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0308  */
    @Override // org.h2.expression.aggregate.AbstractAggregate, org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.expression.Expression optimize(org.h2.engine.SessionLocal r14) {
        /*
            Method dump skipped, instructions count: 906
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.aggregate.Aggregate.optimize(org.h2.engine.SessionLocal):org.h2.expression.Expression");
    }

    private static TypeInfo getSumType(TypeInfo typeInfo) {
        int valueType = typeInfo.getValueType();
        switch (valueType) {
            case 8:
            case 9:
            case 10:
            case 11:
                return TypeInfo.TYPE_BIGINT;
            case 12:
                return TypeInfo.getTypeInfo(13, 29L, -1, null);
            case 13:
                return TypeInfo.getTypeInfo(13, typeInfo.getPrecision() + 10, typeInfo.getDeclaredScale(), null);
            case 14:
                return TypeInfo.TYPE_DOUBLE;
            case 15:
                return TypeInfo.getTypeInfo(16, 27L, -1, null);
            case 16:
                return TypeInfo.getTypeInfo(16, typeInfo.getPrecision() + 10, -1, null);
            default:
                if (DataType.isIntervalType(valueType)) {
                    return TypeInfo.getTypeInfo(valueType, 18L, typeInfo.getDeclaredScale(), null);
                }
                return null;
        }
    }

    private static TypeInfo getAvgType(TypeInfo typeInfo) {
        switch (typeInfo.getValueType()) {
            case 9:
            case 10:
            case 11:
            case 14:
                return TypeInfo.TYPE_DOUBLE;
            case 12:
                return TypeInfo.getTypeInfo(13, 29L, 10, null);
            case 13:
                int min = Math.min(100000 - typeInfo.getScale(), Math.min(100000 - ((int) typeInfo.getPrecision()), 10));
                return TypeInfo.getTypeInfo(13, typeInfo.getPrecision() + min, typeInfo.getScale() + min, null);
            case 15:
                return TypeInfo.getTypeInfo(16, 27L, -1, null);
            case 16:
                return TypeInfo.getTypeInfo(16, typeInfo.getPrecision() + 10, -1, null);
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            default:
                return null;
            case 22:
            case 28:
                return TypeInfo.getTypeInfo(28, typeInfo.getDeclaredPrecision(), 0, null);
            case 23:
                return TypeInfo.getTypeInfo(23, typeInfo.getDeclaredPrecision(), 0, null);
            case 24:
            case 29:
            case 30:
            case 31:
                return TypeInfo.getTypeInfo(31, typeInfo.getDeclaredPrecision(), 9, null);
            case 25:
            case 32:
            case 33:
                return TypeInfo.getTypeInfo(33, typeInfo.getDeclaredPrecision(), 9, null);
            case 26:
            case 34:
                return TypeInfo.getTypeInfo(34, typeInfo.getDeclaredPrecision(), 9, null);
            case 27:
                return TypeInfo.getTypeInfo(27, typeInfo.getDeclaredPrecision(), 9, null);
        }
    }

    @Override // org.h2.expression.aggregate.AbstractAggregate, org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        if (this.orderByList != null) {
            Iterator<QueryOrderBy> it = this.orderByList.iterator();
            while (it.hasNext()) {
                it.next().expression.setEvaluatable(tableFilter, z);
            }
        }
        super.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        switch (this.aggregateType) {
            case LISTAGG:
                return getSQLListagg(sb, i);
            case ARRAY_AGG:
                return getSQLArrayAggregate(sb, i);
            case RANK:
            case DENSE_RANK:
            case PERCENT_RANK:
            case CUME_DIST:
            case PERCENTILE_CONT:
            case PERCENTILE_DISC:
            case MODE:
            default:
                sb.append(this.aggregateType.name());
                if (this.distinct) {
                    sb.append("(DISTINCT ");
                } else {
                    sb.append('(');
                }
                writeExpressions(sb, this.args, i).append(')');
                if (this.orderByList != null) {
                    sb.append(" WITHIN GROUP (");
                    Window.appendOrderBy(sb, this.orderByList, i, false);
                    sb.append(')');
                }
                return appendTailConditions(sb, i, false);
            case JSON_ARRAYAGG:
                return getSQLJsonArrayAggregate(sb, i);
            case JSON_OBJECTAGG:
                return getSQLJsonObjectAggregate(sb, i);
            case COUNT_ALL:
                return appendTailConditions(sb.append("COUNT(*)"), i, false);
        }
    }

    private StringBuilder getSQLArrayAggregate(StringBuilder sb, int i) {
        sb.append("ARRAY_AGG(");
        if (this.distinct) {
            sb.append("DISTINCT ");
        }
        this.args[0].getUnenclosedSQL(sb, i);
        Window.appendOrderBy(sb, this.orderByList, i, false);
        sb.append(')');
        return appendTailConditions(sb, i, false);
    }

    private StringBuilder getSQLListagg(StringBuilder sb, int i) {
        sb.append("LISTAGG(");
        if (this.distinct) {
            sb.append("DISTINCT ");
        }
        this.args[0].getUnenclosedSQL(sb, i);
        ListaggArguments listaggArguments = (ListaggArguments) this.extraArguments;
        Expression separator = listaggArguments.getSeparator();
        if (separator != null) {
            separator.getUnenclosedSQL(sb.append(", "), i);
        }
        if (listaggArguments.getOnOverflowTruncate()) {
            sb.append(" ON OVERFLOW TRUNCATE ");
            Expression filter = listaggArguments.getFilter();
            if (filter != null) {
                filter.getUnenclosedSQL(sb, i).append(' ');
            }
            sb.append(listaggArguments.isWithoutCount() ? "WITHOUT" : "WITH").append(" COUNT");
        }
        sb.append(')');
        sb.append(" WITHIN GROUP (");
        Window.appendOrderBy(sb, this.orderByList, i, true);
        sb.append(')');
        return appendTailConditions(sb, i, false);
    }

    private StringBuilder getSQLJsonObjectAggregate(StringBuilder sb, int i) {
        sb.append("JSON_OBJECTAGG(");
        this.args[0].getUnenclosedSQL(sb, i).append(": ");
        this.args[1].getUnenclosedSQL(sb, i);
        JsonConstructorFunction.getJsonFunctionFlagsSQL(sb, this.flags, false).append(')');
        return appendTailConditions(sb, i, false);
    }

    private StringBuilder getSQLJsonArrayAggregate(StringBuilder sb, int i) {
        sb.append("JSON_ARRAYAGG(");
        if (this.distinct) {
            sb.append("DISTINCT ");
        }
        this.args[0].getUnenclosedSQL(sb, i);
        JsonConstructorFunction.getJsonFunctionFlagsSQL(sb, this.flags, true);
        Window.appendOrderBy(sb, this.orderByList, i, false);
        sb.append(')');
        return appendTailConditions(sb, i, false);
    }

    private Index getMinMaxColumnIndex() {
        Expression expression = this.args[0];
        if (expression instanceof ExpressionColumn) {
            ExpressionColumn expressionColumn = (ExpressionColumn) expression;
            Column column = expressionColumn.getColumn();
            TableFilter tableFilter = expressionColumn.getTableFilter();
            if (tableFilter != null) {
                return tableFilter.getTable().getIndexForColumn(column, true, false);
            }
            return null;
        }
        return null;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (!super.isEverything(expressionVisitor)) {
            return false;
        }
        if (this.filterCondition != null && !this.filterCondition.isEverything(expressionVisitor)) {
            return false;
        }
        switch (expressionVisitor.getType()) {
            case 1:
                switch (this.aggregateType) {
                    case PERCENTILE_CONT:
                    case PERCENTILE_DISC:
                        return this.args[0].isConstant() && Percentile.getColumnIndex(this.select.getSession().getDatabase(), this.orderByList.get(0).expression) != null;
                    case MODE:
                    case JSON_ARRAYAGG:
                    case JSON_OBJECTAGG:
                    case SUM:
                    case BIT_XOR_AGG:
                    case BIT_XNOR_AGG:
                    case BIT_AND_AGG:
                    case BIT_OR_AGG:
                    case BIT_NAND_AGG:
                    case BIT_NOR_AGG:
                    case ANY:
                    case EVERY:
                    case AVG:
                    case STDDEV_POP:
                    case STDDEV_SAMP:
                    case VAR_POP:
                    case VAR_SAMP:
                    case HISTOGRAM:
                    case ANY_VALUE:
                    default:
                        return false;
                    case COUNT_ALL:
                        break;
                    case COUNT:
                        if (this.distinct || this.args[0].getNullable() != 0) {
                            return false;
                        }
                        break;
                    case MEDIAN:
                        return (this.distinct || Percentile.getColumnIndex(this.select.getSession().getDatabase(), this.args[0]) == null) ? false : true;
                    case MIN:
                    case MAX:
                        return getMinMaxColumnIndex() != null;
                    case ENVELOPE:
                        return AggregateDataEnvelope.getGeometryColumnIndex(this.args[0]) != null;
                }
                return expressionVisitor.getTable().canGetRowCount(this.select.getSession());
            case 2:
                if (this.aggregateType == AggregateType.ANY_VALUE) {
                    return false;
                }
                break;
        }
        for (Expression expression : this.args) {
            if (!expression.isEverything(expressionVisitor)) {
                return false;
            }
        }
        if (this.orderByList != null) {
            Iterator<QueryOrderBy> it = this.orderByList.iterator();
            while (it.hasNext()) {
                if (!it.next().expression.isEverything(expressionVisitor)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = 1;
        for (Expression expression : this.args) {
            i += expression.getCost();
        }
        if (this.orderByList != null) {
            Iterator<QueryOrderBy> it = this.orderByList.iterator();
            while (it.hasNext()) {
                i += it.next().expression.getCost();
            }
        }
        if (this.filterCondition != null) {
            i += this.filterCondition.getCost();
        }
        return i;
    }

    public Select getSelect() {
        return this.select;
    }

    public boolean isDistinct() {
        return this.distinct;
    }
}
