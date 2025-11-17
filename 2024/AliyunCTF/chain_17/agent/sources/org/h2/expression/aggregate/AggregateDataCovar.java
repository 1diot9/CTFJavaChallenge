package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataCovar.class */
final class AggregateDataCovar extends AggregateDataBinarySet {
    private final AggregateType aggregateType;
    private long count;
    private double sumY;
    private double sumX;
    private double sumYX;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataCovar(AggregateType aggregateType) {
        this.aggregateType = aggregateType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateDataBinarySet
    public void add(SessionLocal sessionLocal, Value value, Value value2) {
        double d = value.getDouble();
        double d2 = value2.getDouble();
        this.sumY += d;
        this.sumX += d2;
        this.sumYX += d * d2;
        this.count++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        double d;
        switch (this.aggregateType) {
            case COVAR_POP:
                if (this.count < 1) {
                    return ValueNull.INSTANCE;
                }
                d = (this.sumYX - ((this.sumX * this.sumY) / this.count)) / this.count;
                break;
            case COVAR_SAMP:
                if (this.count < 2) {
                    return ValueNull.INSTANCE;
                }
                d = (this.sumYX - ((this.sumX * this.sumY) / this.count)) / (this.count - 1);
                break;
            case REGR_SXY:
                if (this.count < 1) {
                    return ValueNull.INSTANCE;
                }
                d = this.sumYX - ((this.sumX * this.sumY) / this.count);
                break;
            default:
                throw DbException.getInternalError("type=" + this.aggregateType);
        }
        return ValueDouble.get(d);
    }
}
