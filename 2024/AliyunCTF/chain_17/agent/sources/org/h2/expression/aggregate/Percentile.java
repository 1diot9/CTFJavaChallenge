package org.h2.expression.aggregate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import org.h2.api.IntervalQualifier;
import org.h2.command.query.QueryOrderBy;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.SearchRow;
import org.h2.table.Column;
import org.h2.table.TableFilter;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/Percentile.class */
public final class Percentile {
    static final BigDecimal HALF = BigDecimal.valueOf(0.5d);

    private static boolean isNullsLast(DefaultNullOrdering defaultNullOrdering, Index index) {
        return defaultNullOrdering.compareNull(true, index.getIndexColumns()[0].sortType) > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Index getColumnIndex(Database database, Expression expression) {
        DefaultNullOrdering defaultNullOrdering = database.getDefaultNullOrdering();
        if (expression instanceof ExpressionColumn) {
            ExpressionColumn expressionColumn = (ExpressionColumn) expression;
            Column column = expressionColumn.getColumn();
            TableFilter tableFilter = expressionColumn.getTableFilter();
            if (tableFilter != null) {
                ArrayList<Index> indexes = tableFilter.getTable().getIndexes();
                Index index = null;
                if (indexes != null) {
                    boolean isNullable = column.isNullable();
                    int size = indexes.size();
                    for (int i = 1; i < size; i++) {
                        Index index2 = indexes.get(i);
                        if (index2.canFindNext() && index2.isFirstColumn(column) && (index == null || index.getColumns().length > index2.getColumns().length || (isNullable && isNullsLast(defaultNullOrdering, index) && !isNullsLast(defaultNullOrdering, index2)))) {
                            index = index2;
                        }
                    }
                }
                return index;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Value getValue(SessionLocal sessionLocal, Value[] valueArr, int i, ArrayList<QueryOrderBy> arrayList, BigDecimal bigDecimal, boolean z) {
        int i2;
        CompareMode compareMode = sessionLocal.getDatabase().getCompareMode();
        Arrays.sort(valueArr, compareMode);
        int length = valueArr.length;
        boolean z2 = (arrayList == null || (arrayList.get(0).sortType & 1) == 0) ? false : true;
        BigDecimal multiply = BigDecimal.valueOf(length - 1).multiply(bigDecimal);
        int intValue = multiply.intValue();
        BigDecimal subtract = multiply.subtract(BigDecimal.valueOf(intValue));
        if (subtract.signum() == 0) {
            z = false;
            i2 = intValue;
        } else {
            i2 = intValue + 1;
            if (!z) {
                if (subtract.compareTo(HALF) > 0) {
                    intValue = i2;
                } else {
                    i2 = intValue;
                }
            }
        }
        if (z2) {
            intValue = (length - 1) - intValue;
            i2 = (length - 1) - i2;
        }
        Value value = valueArr[intValue];
        if (!z) {
            return value;
        }
        return interpolate(value, valueArr[i2], subtract, i, sessionLocal, compareMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Value getFromIndex(SessionLocal sessionLocal, Expression expression, int i, ArrayList<QueryOrderBy> arrayList, BigDecimal bigDecimal, boolean z) {
        long j;
        boolean z2;
        Database database = sessionLocal.getDatabase();
        Index columnIndex = getColumnIndex(database, expression);
        long rowCount = columnIndex.getRowCount(sessionLocal);
        if (rowCount == 0) {
            return ValueNull.INSTANCE;
        }
        Cursor find = columnIndex.find(sessionLocal, null, null);
        find.next();
        int columnId = columnIndex.getColumns()[0].getColumnId();
        ExpressionColumn expressionColumn = (ExpressionColumn) expression;
        if (expressionColumn.getColumn().isNullable()) {
            boolean z3 = false;
            while (true) {
                z2 = z3;
                if (rowCount <= 0) {
                    break;
                }
                SearchRow searchRow = find.getSearchRow();
                if (searchRow == null) {
                    return ValueNull.INSTANCE;
                }
                if (searchRow.getValue(columnId) != ValueNull.INSTANCE) {
                    break;
                }
                rowCount--;
                find.next();
                z3 = true;
            }
            if (rowCount == 0) {
                return ValueNull.INSTANCE;
            }
            if (!z2 && isNullsLast(database.getDefaultNullOrdering(), columnIndex)) {
                SearchRow templateSimpleRow = expressionColumn.getTableFilter().getTable().getTemplateSimpleRow(true);
                templateSimpleRow.setValue(columnId, ValueNull.INSTANCE);
                while (columnIndex.find(sessionLocal, templateSimpleRow, templateSimpleRow).next()) {
                    rowCount--;
                }
                if (rowCount <= 0) {
                    return ValueNull.INSTANCE;
                }
            }
        }
        boolean z4 = (arrayList != null ? arrayList.get(0).sortType & 1 : 0) != (columnIndex.getIndexColumns()[0].sortType & 1);
        BigDecimal multiply = BigDecimal.valueOf(rowCount - 1).multiply(bigDecimal);
        long longValue = multiply.longValue();
        BigDecimal subtract = multiply.subtract(BigDecimal.valueOf(longValue));
        if (subtract.signum() == 0) {
            z = false;
            j = longValue;
        } else {
            j = longValue + 1;
            if (!z) {
                if (subtract.compareTo(HALF) > 0) {
                    longValue = j;
                } else {
                    j = longValue;
                }
            }
        }
        long j2 = z4 ? (rowCount - 1) - j : longValue;
        for (int i2 = 0; i2 < j2; i2++) {
            find.next();
        }
        SearchRow searchRow2 = find.getSearchRow();
        if (searchRow2 == null) {
            return ValueNull.INSTANCE;
        }
        Value value = searchRow2.getValue(columnId);
        if (value == ValueNull.INSTANCE) {
            return value;
        }
        if (z) {
            find.next();
            SearchRow searchRow3 = find.getSearchRow();
            if (searchRow3 == null) {
                return value;
            }
            Value value2 = searchRow3.getValue(columnId);
            if (value2 == ValueNull.INSTANCE) {
                return value;
            }
            if (z4) {
                value = value2;
                value2 = value;
            }
            return interpolate(value, value2, subtract, i, sessionLocal, database.getCompareMode());
        }
        return value;
    }

    private static Value interpolate(Value value, Value value2, BigDecimal bigDecimal, int i, SessionLocal sessionLocal, CompareMode compareMode) {
        if (value.compareTo(value2, sessionLocal, compareMode) == 0) {
            return value;
        }
        switch (i) {
            case 9:
            case 10:
            case 11:
                return ValueNumeric.get(interpolateDecimal(BigDecimal.valueOf(value.getInt()), BigDecimal.valueOf(value2.getInt()), bigDecimal));
            case 12:
                return ValueNumeric.get(interpolateDecimal(BigDecimal.valueOf(value.getLong()), BigDecimal.valueOf(value2.getLong()), bigDecimal));
            case 13:
            case 16:
                return ValueNumeric.get(interpolateDecimal(value.getBigDecimal(), value2.getBigDecimal(), bigDecimal));
            case 14:
            case 15:
                return ValueNumeric.get(interpolateDecimal(BigDecimal.valueOf(value.getDouble()), BigDecimal.valueOf(value2.getDouble()), bigDecimal));
            case 17:
                return ValueDate.fromDateValue(DateTimeUtils.dateValueFromAbsoluteDay(interpolateDecimal(BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(((ValueDate) value).getDateValue())), BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(((ValueDate) value2).getDateValue())), bigDecimal).longValue()));
            case 18:
                return ValueTime.fromNanos(interpolateDecimal(BigDecimal.valueOf(((ValueTime) value).getNanos()), BigDecimal.valueOf(((ValueTime) value2).getNanos()), bigDecimal).longValue());
            case 19:
                BigDecimal valueOf = BigDecimal.valueOf(((ValueTimeTimeZone) value).getNanos());
                BigDecimal valueOf2 = BigDecimal.valueOf(((ValueTimeTimeZone) value2).getNanos());
                BigDecimal add = BigDecimal.valueOf(r0.getTimeZoneOffsetSeconds()).multiply(BigDecimal.ONE.subtract(bigDecimal)).add(BigDecimal.valueOf(r0.getTimeZoneOffsetSeconds()).multiply(bigDecimal));
                int intValue = add.intValue();
                BigDecimal valueOf3 = BigDecimal.valueOf(intValue);
                BigDecimal interpolateDecimal = interpolateDecimal(valueOf, valueOf2, bigDecimal);
                if (add.compareTo(valueOf3) != 0) {
                    interpolateDecimal = interpolateDecimal.add(add.subtract(valueOf3).multiply(BigDecimal.valueOf(DateTimeUtils.NANOS_PER_SECOND)));
                }
                long longValue = interpolateDecimal.longValue();
                if (longValue < 0) {
                    longValue += DateTimeUtils.NANOS_PER_SECOND;
                    intValue++;
                } else if (longValue >= DateTimeUtils.NANOS_PER_DAY) {
                    longValue -= DateTimeUtils.NANOS_PER_SECOND;
                    intValue--;
                }
                return ValueTimeTimeZone.fromNanos(longValue, intValue);
            case 20:
                ValueTimestamp valueTimestamp = (ValueTimestamp) value;
                ValueTimestamp valueTimestamp2 = (ValueTimestamp) value2;
                BigInteger[] divideAndRemainder = interpolateDecimal(timestampToDecimal(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos()), timestampToDecimal(valueTimestamp2.getDateValue(), valueTimestamp2.getTimeNanos()), bigDecimal).toBigInteger().divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
                long longValue2 = divideAndRemainder[0].longValue();
                long longValue3 = divideAndRemainder[1].longValue();
                if (longValue3 < 0) {
                    longValue3 += DateTimeUtils.NANOS_PER_DAY;
                    longValue2--;
                }
                return ValueTimestamp.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(longValue2), longValue3);
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
                ValueTimestampTimeZone valueTimestampTimeZone2 = (ValueTimestampTimeZone) value2;
                BigDecimal timestampToDecimal = timestampToDecimal(valueTimestampTimeZone.getDateValue(), valueTimestampTimeZone.getTimeNanos());
                BigDecimal timestampToDecimal2 = timestampToDecimal(valueTimestampTimeZone2.getDateValue(), valueTimestampTimeZone2.getTimeNanos());
                BigDecimal add2 = BigDecimal.valueOf(valueTimestampTimeZone.getTimeZoneOffsetSeconds()).multiply(BigDecimal.ONE.subtract(bigDecimal)).add(BigDecimal.valueOf(valueTimestampTimeZone2.getTimeZoneOffsetSeconds()).multiply(bigDecimal));
                int intValue2 = add2.intValue();
                BigDecimal valueOf4 = BigDecimal.valueOf(intValue2);
                BigDecimal interpolateDecimal2 = interpolateDecimal(timestampToDecimal, timestampToDecimal2, bigDecimal);
                if (add2.compareTo(valueOf4) != 0) {
                    interpolateDecimal2 = interpolateDecimal2.add(add2.subtract(valueOf4).multiply(BigDecimal.valueOf(DateTimeUtils.NANOS_PER_SECOND)));
                }
                BigInteger[] divideAndRemainder2 = interpolateDecimal2.toBigInteger().divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
                long longValue4 = divideAndRemainder2[0].longValue();
                long longValue5 = divideAndRemainder2[1].longValue();
                if (longValue5 < 0) {
                    longValue5 += DateTimeUtils.NANOS_PER_DAY;
                    longValue4--;
                }
                return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(longValue4), longValue5, intValue2);
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(i - 22), interpolateDecimal(new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval) value)), new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval) value2)), bigDecimal).toBigInteger());
            default:
                return bigDecimal.compareTo(HALF) > 0 ? value2 : value;
        }
    }

    private static BigDecimal timestampToDecimal(long j, long j2) {
        return new BigDecimal(BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(j)).multiply(IntervalUtils.NANOS_PER_DAY_BI).add(BigInteger.valueOf(j2)));
    }

    private static BigDecimal interpolateDecimal(BigDecimal bigDecimal, BigDecimal bigDecimal2, BigDecimal bigDecimal3) {
        return bigDecimal.multiply(BigDecimal.ONE.subtract(bigDecimal3)).add(bigDecimal2.multiply(bigDecimal3));
    }

    private Percentile() {
    }
}
