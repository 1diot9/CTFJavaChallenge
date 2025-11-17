package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.schema.Sequence;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/SequenceOptions.class */
public class SequenceOptions {
    private TypeInfo dataType;
    private Expression start;
    private Expression restart;
    private Expression increment;
    private Expression maxValue;
    private Expression minValue;
    private Sequence.Cycle cycle;
    private Expression cacheSize;
    private long[] bounds;
    private final Sequence oldSequence;

    private static Long getLong(SessionLocal sessionLocal, Expression expression) {
        Value value;
        if (expression != null && (value = expression.optimize(sessionLocal).getValue(sessionLocal)) != ValueNull.INSTANCE) {
            return Long.valueOf(value.getLong());
        }
        return null;
    }

    public SequenceOptions() {
        this.oldSequence = null;
    }

    public SequenceOptions(Sequence sequence, TypeInfo typeInfo) {
        this.oldSequence = sequence;
        this.dataType = typeInfo;
        getBounds();
    }

    public TypeInfo getDataType() {
        if (this.oldSequence != null) {
            synchronized (this.oldSequence) {
                copyFromOldSequence();
            }
        }
        return this.dataType;
    }

    private void copyFromOldSequence() {
        long[] bounds = getBounds();
        long max = Math.max(this.oldSequence.getMinValue(), bounds[0]);
        long min = Math.min(this.oldSequence.getMaxValue(), bounds[1]);
        if (min < max) {
            max = bounds[0];
            min = bounds[1];
        }
        this.minValue = ValueExpression.get(ValueBigint.get(max));
        this.maxValue = ValueExpression.get(ValueBigint.get(min));
        long startValue = this.oldSequence.getStartValue();
        if (startValue >= max && startValue <= min) {
            this.start = ValueExpression.get(ValueBigint.get(startValue));
        }
        long baseValue = this.oldSequence.getBaseValue();
        if (baseValue >= max && baseValue <= min) {
            this.restart = ValueExpression.get(ValueBigint.get(baseValue));
        }
        this.increment = ValueExpression.get(ValueBigint.get(this.oldSequence.getIncrement()));
        this.cycle = this.oldSequence.getCycle();
        this.cacheSize = ValueExpression.get(ValueBigint.get(this.oldSequence.getCacheSize()));
    }

    public void setDataType(TypeInfo typeInfo) {
        this.dataType = typeInfo;
    }

    public Long getStartValue(SessionLocal sessionLocal) {
        return check(getLong(sessionLocal, this.start));
    }

    public void setStartValue(Expression expression) {
        this.start = expression;
    }

    public Long getRestartValue(SessionLocal sessionLocal, long j) {
        return check(this.restart == ValueExpression.DEFAULT ? Long.valueOf(j) : getLong(sessionLocal, this.restart));
    }

    public void setRestartValue(Expression expression) {
        this.restart = expression;
    }

    public Long getIncrement(SessionLocal sessionLocal) {
        return check(getLong(sessionLocal, this.increment));
    }

    public void setIncrement(Expression expression) {
        this.increment = expression;
    }

    public Long getMaxValue(Sequence sequence, SessionLocal sessionLocal) {
        Long l;
        if (this.maxValue == ValueExpression.NULL && sequence != null) {
            l = Long.valueOf(Sequence.getDefaultMaxValue(Long.valueOf(getCurrentStart(sequence, sessionLocal)), this.increment != null ? getIncrement(sessionLocal).longValue() : sequence.getIncrement(), getBounds()));
        } else {
            l = getLong(sessionLocal, this.maxValue);
        }
        return check(l);
    }

    public void setMaxValue(Expression expression) {
        this.maxValue = expression;
    }

    public Long getMinValue(Sequence sequence, SessionLocal sessionLocal) {
        Long l;
        if (this.minValue == ValueExpression.NULL && sequence != null) {
            l = Long.valueOf(Sequence.getDefaultMinValue(Long.valueOf(getCurrentStart(sequence, sessionLocal)), this.increment != null ? getIncrement(sessionLocal).longValue() : sequence.getIncrement(), getBounds()));
        } else {
            l = getLong(sessionLocal, this.minValue);
        }
        return check(l);
    }

    public void setMinValue(Expression expression) {
        this.minValue = expression;
    }

    private Long check(Long l) {
        if (l == null) {
            return null;
        }
        long[] bounds = getBounds();
        long longValue = l.longValue();
        if (longValue < bounds[0] || longValue > bounds[1]) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, Long.toString(longValue));
        }
        return l;
    }

    public long[] getBounds() {
        long[] jArr = this.bounds;
        if (jArr == null) {
            long[] bounds = getBounds(this.dataType);
            jArr = bounds;
            this.bounds = bounds;
        }
        return jArr;
    }

    public static long[] getBounds(TypeInfo typeInfo) {
        long j;
        long j2;
        switch (typeInfo.getValueType()) {
            case 9:
                j2 = -128;
                j = 127;
                break;
            case 10:
                j2 = -32768;
                j = 32767;
                break;
            case 11:
                j2 = -2147483648L;
                j = 2147483647L;
                break;
            case 12:
                j2 = Long.MIN_VALUE;
                j = Long.MAX_VALUE;
                break;
            case 13:
                if (typeInfo.getScale() != 0) {
                    throw DbException.getUnsupportedException(typeInfo.getTraceSQL());
                }
                long precision = typeInfo.getPrecision() - typeInfo.getScale();
                if (precision <= 0) {
                    throw DbException.getUnsupportedException(typeInfo.getTraceSQL());
                }
                if (precision > 18) {
                    j2 = Long.MIN_VALUE;
                    j = Long.MAX_VALUE;
                    break;
                } else {
                    long j3 = 10;
                    for (int i = 1; i < precision; i++) {
                        j3 *= 10;
                    }
                    long j4 = j3 - 1;
                    j = j4;
                    j2 = -j4;
                    break;
                }
            case 14:
                j2 = -16777216;
                j = 16777216;
                break;
            case 15:
                j2 = -9007199254740992L;
                j = 9007199254740992L;
                break;
            case 16:
                long precision2 = typeInfo.getPrecision();
                if (precision2 > 18) {
                    j2 = Long.MIN_VALUE;
                    j = Long.MAX_VALUE;
                    break;
                } else {
                    j = 10;
                    for (int i2 = 1; i2 < precision2; i2++) {
                        j *= 10;
                    }
                    j2 = -j;
                    break;
                }
            default:
                throw DbException.getUnsupportedException(typeInfo.getTraceSQL());
        }
        return new long[]{j2, j};
    }

    public Sequence.Cycle getCycle() {
        return this.cycle;
    }

    public void setCycle(Sequence.Cycle cycle) {
        this.cycle = cycle;
    }

    public Long getCacheSize(SessionLocal sessionLocal) {
        return getLong(sessionLocal, this.cacheSize);
    }

    public void setCacheSize(Expression expression) {
        this.cacheSize = expression;
    }

    private long getCurrentStart(Sequence sequence, SessionLocal sessionLocal) {
        return this.start != null ? getStartValue(sessionLocal).longValue() : sequence.getBaseValue();
    }
}
