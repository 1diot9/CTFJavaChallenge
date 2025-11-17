package org.h2.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.SessionLocal;
import org.h2.expression.function.DateTimeFunction;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/IntervalOperation.class */
public class IntervalOperation extends Operation2 {
    private static final int INTERVAL_YEAR_DIGITS = 20;
    private static final int INTERVAL_DAY_DIGITS = 32;
    private static final TypeInfo INTERVAL_DIVIDE_INTERVAL_YEAR_TYPE = TypeInfo.getTypeInfo(13, 60, 40, null);
    private static final TypeInfo INTERVAL_DIVIDE_INTERVAL_DAY_TYPE = TypeInfo.getTypeInfo(13, 96, 64, null);
    private final IntervalOpType opType;
    private TypeInfo forcedType;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/IntervalOperation$IntervalOpType.class */
    public enum IntervalOpType {
        INTERVAL_PLUS_INTERVAL,
        INTERVAL_MINUS_INTERVAL,
        INTERVAL_DIVIDE_INTERVAL,
        DATETIME_PLUS_INTERVAL,
        DATETIME_MINUS_INTERVAL,
        INTERVAL_MULTIPLY_NUMERIC,
        INTERVAL_DIVIDE_NUMERIC,
        DATETIME_MINUS_DATETIME
    }

    private static BigInteger nanosFromValue(SessionLocal sessionLocal, Value value) {
        long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
        return BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(dateAndTimeFromValue[0])).multiply(IntervalUtils.NANOS_PER_DAY_BI).add(BigInteger.valueOf(dateAndTimeFromValue[1]));
    }

    public IntervalOperation(IntervalOpType intervalOpType, Expression expression, Expression expression2, TypeInfo typeInfo) {
        this(intervalOpType, expression, expression2);
        this.forcedType = typeInfo;
    }

    public IntervalOperation(IntervalOpType intervalOpType, Expression expression, Expression expression2) {
        super(expression, expression2);
        this.opType = intervalOpType;
        int valueType = expression.getType().getValueType();
        int valueType2 = expression2.getType().getValueType();
        switch (intervalOpType) {
            case INTERVAL_PLUS_INTERVAL:
            case INTERVAL_MINUS_INTERVAL:
                this.type = TypeInfo.getTypeInfo(Value.getHigherOrder(valueType, valueType2));
                return;
            case INTERVAL_DIVIDE_INTERVAL:
                this.type = DataType.isYearMonthIntervalType(valueType) ? INTERVAL_DIVIDE_INTERVAL_YEAR_TYPE : INTERVAL_DIVIDE_INTERVAL_DAY_TYPE;
                return;
            case DATETIME_PLUS_INTERVAL:
            case DATETIME_MINUS_INTERVAL:
            case INTERVAL_MULTIPLY_NUMERIC:
            case INTERVAL_DIVIDE_NUMERIC:
                this.type = expression.getType();
                return;
            case DATETIME_MINUS_DATETIME:
                if (this.forcedType != null) {
                    this.type = this.forcedType;
                    return;
                }
                if ((valueType == 18 || valueType == 19) && (valueType2 == 18 || valueType2 == 19)) {
                    this.type = TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND;
                    return;
                } else if (valueType == 17 && valueType2 == 17) {
                    this.type = TypeInfo.TYPE_INTERVAL_DAY;
                    return;
                } else {
                    this.type = TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND;
                    return;
                }
            default:
                return;
        }
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return this.forcedType == null;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        if (this.forcedType != null) {
            getInnerSQL2(sb.append('('), i);
            getForcedTypeSQL(sb.append(") "), this.forcedType);
        } else {
            getInnerSQL2(sb, i);
        }
        return sb;
    }

    private void getInnerSQL2(StringBuilder sb, int i) {
        this.left.getSQL(sb, i, 0).append(' ').append(getOperationToken()).append(' ');
        this.right.getSQL(sb, i, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StringBuilder getForcedTypeSQL(StringBuilder sb, TypeInfo typeInfo) {
        int precision = (int) typeInfo.getPrecision();
        int scale = typeInfo.getScale();
        return IntervalQualifier.valueOf(typeInfo.getValueType() - 22).getTypeName(sb, precision == 2 ? -1 : precision, scale == 6 ? -1 : scale, true);
    }

    private char getOperationToken() {
        switch (this.opType) {
            case INTERVAL_PLUS_INTERVAL:
            case DATETIME_PLUS_INTERVAL:
                return '+';
            case INTERVAL_MINUS_INTERVAL:
            case DATETIME_MINUS_INTERVAL:
            case DATETIME_MINUS_DATETIME:
                return '-';
            case INTERVAL_DIVIDE_INTERVAL:
            case INTERVAL_DIVIDE_NUMERIC:
                return '/';
            case INTERVAL_MULTIPLY_NUMERIC:
                return '*';
            default:
                throw DbException.getInternalError("opType=" + this.opType);
        }
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        long nanos;
        ValueInterval from;
        boolean z;
        Value value = this.left.getValue(sessionLocal);
        Value value2 = this.right.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int valueType = value.getValueType();
        int valueType2 = value2.getValueType();
        switch (this.opType) {
            case INTERVAL_PLUS_INTERVAL:
            case INTERVAL_MINUS_INTERVAL:
                BigInteger intervalToAbsolute = IntervalUtils.intervalToAbsolute((ValueInterval) value);
                BigInteger intervalToAbsolute2 = IntervalUtils.intervalToAbsolute((ValueInterval) value2);
                return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(Value.getHigherOrder(valueType, valueType2) - 22), this.opType == IntervalOpType.INTERVAL_PLUS_INTERVAL ? intervalToAbsolute.add(intervalToAbsolute2) : intervalToAbsolute.subtract(intervalToAbsolute2));
            case INTERVAL_DIVIDE_INTERVAL:
                return ValueNumeric.get(IntervalUtils.intervalToAbsolute((ValueInterval) value)).divide(ValueNumeric.get(IntervalUtils.intervalToAbsolute((ValueInterval) value2)), this.type);
            case DATETIME_PLUS_INTERVAL:
            case DATETIME_MINUS_INTERVAL:
                return getDateTimeWithInterval(sessionLocal, value, value2, valueType, valueType2);
            case INTERVAL_MULTIPLY_NUMERIC:
            case INTERVAL_DIVIDE_NUMERIC:
                BigDecimal bigDecimal = new BigDecimal(IntervalUtils.intervalToAbsolute((ValueInterval) value));
                BigDecimal bigDecimal2 = value2.getBigDecimal();
                return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(valueType - 22), (this.opType == IntervalOpType.INTERVAL_MULTIPLY_NUMERIC ? bigDecimal.multiply(bigDecimal2) : bigDecimal.divide(bigDecimal2)).toBigInteger());
            case DATETIME_MINUS_DATETIME:
                if ((valueType == 18 || valueType == 19) && (valueType2 == 18 || valueType2 == 19)) {
                    if (valueType == 18 && valueType2 == 18) {
                        nanos = ((ValueTime) value).getNanos() - ((ValueTime) value2).getNanos();
                    } else {
                        nanos = (((ValueTimeTimeZone) value.convertTo(TypeInfo.TYPE_TIME_TZ, sessionLocal)).getNanos() - ((ValueTimeTimeZone) value2.convertTo(TypeInfo.TYPE_TIME_TZ, sessionLocal)).getNanos()) + ((r0.getTimeZoneOffsetSeconds() - r0.getTimeZoneOffsetSeconds()) * DateTimeUtils.NANOS_PER_SECOND);
                    }
                    boolean z2 = nanos < 0;
                    if (z2) {
                        nanos = -nanos;
                    }
                    from = ValueInterval.from(IntervalQualifier.HOUR_TO_SECOND, z2, nanos / DateTimeUtils.NANOS_PER_HOUR, nanos % DateTimeUtils.NANOS_PER_HOUR);
                } else if (this.forcedType != null && DataType.isYearMonthIntervalType(this.forcedType.getValueType())) {
                    long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
                    long[] dateAndTimeFromValue2 = DateTimeUtils.dateAndTimeFromValue(value2, sessionLocal);
                    long dateValue = (valueType == 18 || valueType == 19) ? sessionLocal.currentTimestamp().getDateValue() : dateAndTimeFromValue[0];
                    long dateValue2 = (valueType2 == 18 || valueType2 == 19) ? sessionLocal.currentTimestamp().getDateValue() : dateAndTimeFromValue2[0];
                    long yearFromDateValue = ((12 * (DateTimeUtils.yearFromDateValue(dateValue) - DateTimeUtils.yearFromDateValue(dateValue2))) + DateTimeUtils.monthFromDateValue(dateValue)) - DateTimeUtils.monthFromDateValue(dateValue2);
                    int dayFromDateValue = DateTimeUtils.dayFromDateValue(dateValue);
                    int dayFromDateValue2 = DateTimeUtils.dayFromDateValue(dateValue2);
                    if (yearFromDateValue >= 0) {
                        if (dayFromDateValue < dayFromDateValue2 || (dayFromDateValue == dayFromDateValue2 && dateAndTimeFromValue[1] < dateAndTimeFromValue2[1])) {
                            yearFromDateValue--;
                        }
                    } else if (dayFromDateValue > dayFromDateValue2 || (dayFromDateValue == dayFromDateValue2 && dateAndTimeFromValue[1] > dateAndTimeFromValue2[1])) {
                        yearFromDateValue++;
                    }
                    if (yearFromDateValue < 0) {
                        z = true;
                        yearFromDateValue = -yearFromDateValue;
                    } else {
                        z = false;
                    }
                    from = ValueInterval.from(IntervalQualifier.MONTH, z, yearFromDateValue, 0L);
                } else if (valueType == 17 && valueType2 == 17) {
                    long absoluteDayFromDateValue = DateTimeUtils.absoluteDayFromDateValue(((ValueDate) value).getDateValue()) - DateTimeUtils.absoluteDayFromDateValue(((ValueDate) value2).getDateValue());
                    boolean z3 = absoluteDayFromDateValue < 0;
                    if (z3) {
                        absoluteDayFromDateValue = -absoluteDayFromDateValue;
                    }
                    from = ValueInterval.from(IntervalQualifier.DAY, z3, absoluteDayFromDateValue, 0L);
                } else {
                    BigInteger subtract = nanosFromValue(sessionLocal, value).subtract(nanosFromValue(sessionLocal, value2));
                    if (valueType == 21 || valueType2 == 21) {
                        subtract = subtract.add(BigInteger.valueOf((((ValueTimestampTimeZone) value2.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, sessionLocal)).getTimeZoneOffsetSeconds() - ((ValueTimestampTimeZone) value.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, sessionLocal)).getTimeZoneOffsetSeconds()) * DateTimeUtils.NANOS_PER_SECOND));
                    }
                    from = IntervalUtils.intervalFromAbsolute(IntervalQualifier.DAY_TO_SECOND, subtract);
                }
                if (this.forcedType != null) {
                    from = from.castTo(this.forcedType, sessionLocal);
                }
                return from;
            default:
                throw DbException.getInternalError("type=" + this.opType);
        }
    }

    private Value getDateTimeWithInterval(SessionLocal sessionLocal, Value value, Value value2, int i, int i2) {
        long longValue;
        long longValue2;
        switch (i) {
            case 17:
            case 20:
            case 21:
                if (DataType.isYearMonthIntervalType(i2)) {
                    long longValue3 = IntervalUtils.intervalToAbsolute((ValueInterval) value2).longValue();
                    if (this.opType == IntervalOpType.DATETIME_MINUS_INTERVAL) {
                        longValue3 = -longValue3;
                    }
                    return DateTimeFunction.dateadd(sessionLocal, 1, longValue3, value);
                }
                BigInteger intervalToAbsolute = IntervalUtils.intervalToAbsolute((ValueInterval) value2);
                if (i == 17) {
                    BigInteger valueOf = BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(((ValueDate) value).getDateValue()));
                    BigInteger divide = intervalToAbsolute.divide(IntervalUtils.NANOS_PER_DAY_BI);
                    return ValueDate.fromDateValue(DateTimeUtils.dateValueFromAbsoluteDay((this.opType == IntervalOpType.DATETIME_PLUS_INTERVAL ? valueOf.add(divide) : valueOf.subtract(divide)).longValue()));
                }
                long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
                long absoluteDayFromDateValue = DateTimeUtils.absoluteDayFromDateValue(dateAndTimeFromValue[0]);
                long j = dateAndTimeFromValue[1];
                BigInteger[] divideAndRemainder = intervalToAbsolute.divideAndRemainder(IntervalUtils.NANOS_PER_DAY_BI);
                if (this.opType == IntervalOpType.DATETIME_PLUS_INTERVAL) {
                    longValue = absoluteDayFromDateValue + divideAndRemainder[0].longValue();
                    longValue2 = j + divideAndRemainder[1].longValue();
                } else {
                    longValue = absoluteDayFromDateValue - divideAndRemainder[0].longValue();
                    longValue2 = j - divideAndRemainder[1].longValue();
                }
                if (longValue2 >= DateTimeUtils.NANOS_PER_DAY) {
                    longValue2 -= DateTimeUtils.NANOS_PER_DAY;
                    longValue++;
                } else if (longValue2 < 0) {
                    longValue2 += DateTimeUtils.NANOS_PER_DAY;
                    longValue--;
                }
                return DateTimeUtils.dateTimeToValue(value, DateTimeUtils.dateValueFromAbsoluteDay(longValue), longValue2);
            case 18:
                if (DataType.isYearMonthIntervalType(i2)) {
                    throw DbException.getInternalError("type=" + i2);
                }
                return ValueTime.fromNanos(getTimeWithInterval(value2, ((ValueTime) value).getNanos()));
            case 19:
                if (DataType.isYearMonthIntervalType(i2)) {
                    throw DbException.getInternalError("type=" + i2);
                }
                ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) value;
                return ValueTimeTimeZone.fromNanos(getTimeWithInterval(value2, valueTimeTimeZone.getNanos()), valueTimeTimeZone.getTimeZoneOffsetSeconds());
            default:
                throw DbException.getInternalError("type=" + this.opType);
        }
    }

    private long getTimeWithInterval(Value value, long j) {
        BigInteger valueOf = BigInteger.valueOf(j);
        BigInteger intervalToAbsolute = IntervalUtils.intervalToAbsolute((ValueInterval) value);
        BigInteger add = this.opType == IntervalOpType.DATETIME_PLUS_INTERVAL ? valueOf.add(intervalToAbsolute) : valueOf.subtract(intervalToAbsolute);
        if (add.signum() < 0 || add.compareTo(IntervalUtils.NANOS_PER_DAY_BI) >= 0) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, add.toString());
        }
        return add.longValue();
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        if (this.left.isConstant() && this.right.isConstant()) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }
}
