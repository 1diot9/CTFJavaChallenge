package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/TimeZoneOperation.class */
public final class TimeZoneOperation extends Operation1_2 {
    public TimeZoneOperation(Expression expression, Expression expression2) {
        super(expression, expression2);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.left.getSQL(sb, i, 0).append(" AT ");
        if (this.right != null) {
            this.right.getSQL(sb.append("TIME ZONE "), i, 0);
        } else {
            sb.append("LOCAL");
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value;
        int timeZoneOffsetUTC;
        int timeZoneOffsetUTC2;
        Value value2 = this.left.getValue(sessionLocal);
        Value convertTo = value2.convertTo(this.type, sessionLocal);
        if (convertTo == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        if (this.right == null) {
            int valueType = value2.getValueType();
            if (valueType == 18 || valueType == 20) {
                return convertTo;
            }
            value = null;
        } else {
            value = this.right.getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
        }
        if (convertTo.getValueType() == 21) {
            ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) convertTo;
            long dateValue = valueTimestampTimeZone.getDateValue();
            long timeNanos = valueTimestampTimeZone.getTimeNanos();
            int timeZoneOffsetSeconds = valueTimestampTimeZone.getTimeZoneOffsetSeconds();
            if (value != null) {
                timeZoneOffsetUTC2 = parseTimeZone(value, dateValue, timeNanos, timeZoneOffsetSeconds, true);
            } else {
                timeZoneOffsetUTC2 = sessionLocal.currentTimeZone().getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(dateValue, timeNanos, timeZoneOffsetSeconds));
            }
            int i = timeZoneOffsetUTC2;
            if (timeZoneOffsetSeconds != i) {
                convertTo = DateTimeUtils.timestampTimeZoneAtOffset(dateValue, timeNanos, timeZoneOffsetSeconds, i);
            }
        } else {
            ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) convertTo;
            long nanos = valueTimeTimeZone.getNanos();
            int timeZoneOffsetSeconds2 = valueTimeTimeZone.getTimeZoneOffsetSeconds();
            if (value != null) {
                timeZoneOffsetUTC = parseTimeZone(value, 1008673L, nanos, timeZoneOffsetSeconds2, false);
            } else {
                timeZoneOffsetUTC = sessionLocal.currentTimeZone().getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(sessionLocal.currentTimestamp().getDateValue(), nanos, timeZoneOffsetSeconds2));
            }
            int i2 = timeZoneOffsetUTC;
            if (timeZoneOffsetSeconds2 != i2) {
                convertTo = ValueTimeTimeZone.fromNanos(DateTimeUtils.normalizeNanosOfDay(nanos + ((i2 - timeZoneOffsetSeconds2) * DateTimeUtils.NANOS_PER_SECOND)), i2);
            }
        }
        return convertTo;
    }

    private static int parseTimeZone(Value value, long j, long j2, int i, boolean z) {
        if (DataType.isCharacterStringType(value.getValueType())) {
            try {
                TimeZoneProvider ofId = TimeZoneProvider.ofId(value.getString());
                if (!z && !ofId.hasFixedOffset()) {
                    throw DbException.getInvalidValueException("time zone", value.getTraceSQL());
                }
                return ofId.getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(j, j2, i));
            } catch (RuntimeException e) {
                throw DbException.getInvalidValueException("time zone", value.getTraceSQL());
            }
        }
        return parseInterval(value);
    }

    public static int parseInterval(Value value) {
        ValueInterval valueInterval = (ValueInterval) value.convertTo(TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND);
        long leading = valueInterval.getLeading();
        long remaining = valueInterval.getRemaining();
        if (leading > 18 || ((leading == 18 && remaining != 0) || remaining % DateTimeUtils.NANOS_PER_SECOND != 0)) {
            throw DbException.getInvalidValueException("time zone", valueInterval.getTraceSQL());
        }
        int i = (int) ((leading * 3600) + (remaining / DateTimeUtils.NANOS_PER_SECOND));
        if (valueInterval.isNegative()) {
            i = -i;
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        int scale;
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        TypeInfo type = this.left.getType();
        int i = 21;
        int valueType = type.getValueType();
        switch (valueType) {
            case 18:
            case 19:
                i = 19;
                scale = type.getScale();
                break;
            case 20:
            case 21:
                scale = type.getScale();
                break;
            default:
                StringBuilder sql = this.left.getSQL(new StringBuilder(), 3, 0);
                int length = sql.length();
                sql.append(" AT ");
                if (this.right != null) {
                    this.right.getSQL(sql.append("TIME ZONE "), 3, 0);
                } else {
                    sql.append("LOCAL");
                }
                throw DbException.getSyntaxError(sql.toString(), length, "time, timestamp");
        }
        this.type = TypeInfo.getTypeInfo(i, -1L, scale, null);
        if (this.left.isConstant() && ((valueType == 19 || valueType == 21) && this.right != null && this.right.isConstant())) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.Operation1_2, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        int valueType;
        return !(expressionVisitor.getType() == 2 && (this.right == null || (valueType = this.left.getType().getValueType()) == 18 || valueType == 20)) && this.left.isEverything(expressionVisitor) && (this.right == null || this.right.isEverything(expressionVisitor));
    }
}
