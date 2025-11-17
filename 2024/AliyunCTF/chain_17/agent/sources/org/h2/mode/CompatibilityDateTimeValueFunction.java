package org.h2.mode;

import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.expression.function.NamedExpression;
import org.h2.util.DateTimeUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/CompatibilityDateTimeValueFunction.class */
public final class CompatibilityDateTimeValueFunction extends Operation0 implements NamedExpression {
    static final int SYSDATE = 0;
    static final int SYSTIMESTAMP = 1;
    private static final String[] NAMES = {"SYSDATE", "SYSTIMESTAMP"};
    private final int function;
    private final int scale;
    private final TypeInfo type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompatibilityDateTimeValueFunction(int i, int i2) {
        this.function = i;
        this.scale = i2;
        if (i == 0) {
            this.type = TypeInfo.getTypeInfo(20, 0L, 0, null);
        } else {
            this.type = TypeInfo.getTypeInfo(21, 0L, i2, null);
        }
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        ValueTimestampTimeZone currentTimestamp = sessionLocal.currentTimestamp();
        long dateValue = currentTimestamp.getDateValue();
        long timeNanos = currentTimestamp.getTimeNanos();
        int timeZoneOffsetSeconds = currentTimestamp.getTimeZoneOffsetSeconds();
        int timeZoneOffsetUTC = TimeZoneProvider.getDefault().getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(dateValue, timeNanos, timeZoneOffsetSeconds));
        if (timeZoneOffsetSeconds != timeZoneOffsetUTC) {
            currentTimestamp = DateTimeUtils.timestampTimeZoneAtOffset(dateValue, timeNanos, timeZoneOffsetSeconds, timeZoneOffsetUTC);
        }
        if (this.function == 0) {
            return ValueTimestamp.fromDateValueAndNanos(currentTimestamp.getDateValue(), (currentTimestamp.getTimeNanos() / DateTimeUtils.NANOS_PER_SECOND) * DateTimeUtils.NANOS_PER_SECOND);
        }
        return currentTimestamp.castTo(this.type, sessionLocal);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append(getName());
        if (this.scale >= 0) {
            sb.append('(').append(this.scale).append(')');
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                return false;
            default:
                return true;
        }
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 1;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
