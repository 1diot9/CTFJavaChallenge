package org.h2.expression.aggregate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.SessionLocal;
import org.h2.util.IntervalUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataAvg.class */
final class AggregateDataAvg extends AggregateData {
    private final TypeInfo dataType;
    private long count;
    private double doubleValue;
    private BigDecimal decimalValue;
    private BigInteger integerValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataAvg(TypeInfo typeInfo) {
        this.dataType = typeInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public void add(SessionLocal sessionLocal, Value value) {
        if (value == ValueNull.INSTANCE) {
            return;
        }
        this.count++;
        switch (this.dataType.getValueType()) {
            case 13:
            case 16:
                BigDecimal bigDecimal = value.getBigDecimal();
                this.decimalValue = this.decimalValue == null ? bigDecimal : this.decimalValue.add(bigDecimal);
                return;
            case 14:
            default:
                BigInteger intervalToAbsolute = IntervalUtils.intervalToAbsolute((ValueInterval) value);
                this.integerValue = this.integerValue == null ? intervalToAbsolute : this.integerValue.add(intervalToAbsolute);
                return;
            case 15:
                this.doubleValue += value.getDouble();
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        Value intervalFromAbsolute;
        if (this.count == 0) {
            return ValueNull.INSTANCE;
        }
        int valueType = this.dataType.getValueType();
        switch (valueType) {
            case 13:
                intervalFromAbsolute = ValueNumeric.get(this.decimalValue.divide(BigDecimal.valueOf(this.count), this.dataType.getScale(), RoundingMode.HALF_DOWN));
                break;
            case 14:
            default:
                intervalFromAbsolute = IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(valueType - 22), this.integerValue.divide(BigInteger.valueOf(this.count)));
                break;
            case 15:
                intervalFromAbsolute = ValueDouble.get(this.doubleValue / this.count);
                break;
            case 16:
                intervalFromAbsolute = ValueDecfloat.divide(this.decimalValue, BigDecimal.valueOf(this.count), this.dataType);
                break;
        }
        return intervalFromAbsolute.castTo(this.dataType, sessionLocal);
    }
}
