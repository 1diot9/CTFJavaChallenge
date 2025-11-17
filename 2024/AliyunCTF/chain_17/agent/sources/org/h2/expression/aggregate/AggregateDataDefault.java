package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.expression.function.BitFunction;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataDefault.class */
public final class AggregateDataDefault extends AggregateData {
    private final AggregateType aggregateType;
    private final TypeInfo dataType;
    private Value value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataDefault(AggregateType aggregateType, TypeInfo typeInfo) {
        this.aggregateType = aggregateType;
        this.dataType = typeInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public void add(SessionLocal sessionLocal, Value value) {
        if (value == ValueNull.INSTANCE) {
            return;
        }
        switch (this.aggregateType) {
            case SUM:
                if (this.value == null) {
                    this.value = value.convertTo(this.dataType.getValueType());
                    return;
                } else {
                    this.value = this.value.add(value.convertTo(this.value.getValueType()));
                    return;
                }
            case MIN:
                if (this.value == null || sessionLocal.compare(value, this.value) < 0) {
                    this.value = value;
                    return;
                }
                return;
            case MAX:
                if (this.value == null || sessionLocal.compare(value, this.value) > 0) {
                    this.value = value;
                    return;
                }
                return;
            case EVERY:
                ValueBoolean convertToBoolean = value.convertToBoolean();
                if (this.value == null) {
                    this.value = convertToBoolean;
                    return;
                } else {
                    this.value = ValueBoolean.get(this.value.getBoolean() && convertToBoolean.getBoolean());
                    return;
                }
            case ANY:
                ValueBoolean convertToBoolean2 = value.convertToBoolean();
                if (this.value == null) {
                    this.value = convertToBoolean2;
                    return;
                } else {
                    this.value = ValueBoolean.get(this.value.getBoolean() || convertToBoolean2.getBoolean());
                    return;
                }
            case BIT_AND_AGG:
            case BIT_NAND_AGG:
                if (this.value == null) {
                    this.value = value;
                    return;
                } else {
                    this.value = BitFunction.getBitwise(0, this.dataType, this.value, value);
                    return;
                }
            case BIT_OR_AGG:
            case BIT_NOR_AGG:
                if (this.value == null) {
                    this.value = value;
                    return;
                } else {
                    this.value = BitFunction.getBitwise(1, this.dataType, this.value, value);
                    return;
                }
            case BIT_XOR_AGG:
            case BIT_XNOR_AGG:
                if (this.value == null) {
                    this.value = value;
                    return;
                } else {
                    this.value = BitFunction.getBitwise(2, this.dataType, this.value, value);
                    return;
                }
            default:
                throw DbException.getInternalError("type=" + this.aggregateType);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.value;
        if (value == null) {
            return ValueNull.INSTANCE;
        }
        switch (this.aggregateType) {
            case BIT_NAND_AGG:
            case BIT_NOR_AGG:
            case BIT_XNOR_AGG:
                value = BitFunction.getBitwise(3, this.dataType, value, null);
                break;
        }
        return value.convertTo(this.dataType);
    }
}
