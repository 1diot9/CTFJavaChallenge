package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.util.DateTimeUtils;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueNull;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/CompatibilityDatePlusTimeOperation.class */
public class CompatibilityDatePlusTimeOperation extends Operation2 {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0015. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:10:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0053  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public CompatibilityDatePlusTimeOperation(org.h2.expression.Expression r8, org.h2.expression.Expression r9) {
        /*
            r7 = this;
            r0 = r7
            r1 = r8
            r2 = r9
            r0.<init>(r1, r2)
            r0 = r8
            org.h2.value.TypeInfo r0 = r0.getType()
            r10 = r0
            r0 = r9
            org.h2.value.TypeInfo r0 = r0.getType()
            r11 = r0
            r0 = r10
            int r0 = r0.getValueType()
            switch(r0) {
                case 18: goto L44;
                case 19: goto L5c;
                case 20: goto L84;
                case 21: goto L34;
                default: goto L9a;
            }
        L34:
            r0 = r11
            int r0 = r0.getValueType()
            r1 = 19
            if (r0 != r1) goto L44
            java.lang.String r0 = "TIMESTAMP WITH TIME ZONE + TIME WITH TIME ZONE"
            org.h2.message.DbException r0 = org.h2.message.DbException.getUnsupportedException(r0)
            throw r0
        L44:
            r0 = r11
            int r0 = r0.getValueType()
            r1 = 17
            if (r0 != r1) goto L53
            r0 = 20
            goto L57
        L53:
            r0 = r10
            int r0 = r0.getValueType()
        L57:
            r12 = r0
            goto Lc2
        L5c:
            r0 = r11
            int r0 = r0.getValueType()
            r1 = 19
            if (r0 != r1) goto L6c
            java.lang.String r0 = "TIME WITH TIME ZONE + TIME WITH TIME ZONE"
            org.h2.message.DbException r0 = org.h2.message.DbException.getUnsupportedException(r0)
            throw r0
        L6c:
            r0 = r11
            int r0 = r0.getValueType()
            r1 = 17
            if (r0 != r1) goto L7b
            r0 = 21
            goto L7f
        L7b:
            r0 = r10
            int r0 = r0.getValueType()
        L7f:
            r12 = r0
            goto Lc2
        L84:
            r0 = r11
            int r0 = r0.getValueType()
            r1 = 19
            if (r0 != r1) goto L93
            r0 = 21
            goto L95
        L93:
            r0 = 20
        L95:
            r12 = r0
            goto Lc2
        L9a:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = r0
            r1.<init>()
            r1 = r10
            int r1 = r1.getValueType()
            java.lang.String r1 = org.h2.value.Value.getTypeName(r1)
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = " + "
            java.lang.StringBuilder r0 = r0.append(r1)
            r1 = r11
            int r1 = r1.getValueType()
            java.lang.String r1 = org.h2.value.Value.getTypeName(r1)
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.h2.message.DbException r0 = org.h2.message.DbException.getUnsupportedException(r0)
            throw r0
        Lc2:
            r0 = r7
            r1 = r12
            r2 = 0
            r3 = r10
            int r3 = r3.getScale()
            r4 = r11
            int r4 = r4.getScale()
            int r3 = java.lang.Math.max(r3, r4)
            r4 = 0
            org.h2.value.TypeInfo r1 = org.h2.value.TypeInfo.getTypeInfo(r1, r2, r3, r4)
            r0.type = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.CompatibilityDatePlusTimeOperation.<init>(org.h2.expression.Expression, org.h2.expression.Expression):void");
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.left.getSQL(sb, i, 0).append(" + ");
        return this.right.getSQL(sb, i, 0);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.left.getValue(sessionLocal);
        Value value2 = this.right.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        switch (value.getValueType()) {
            case 18:
                if (value2.getValueType() == 17) {
                    return ValueTimestamp.fromDateValueAndNanos(((ValueDate) value2).getDateValue(), ((ValueTime) value).getNanos());
                }
                break;
            case 19:
                if (value2.getValueType() == 17) {
                    ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) value;
                    return ValueTimestampTimeZone.fromDateValueAndNanos(((ValueDate) value2).getDateValue(), valueTimeTimeZone.getNanos(), valueTimeTimeZone.getTimeZoneOffsetSeconds());
                }
                break;
            case 20:
                if (value2.getValueType() == 19) {
                    ValueTimestamp valueTimestamp = (ValueTimestamp) value;
                    value = ValueTimestampTimeZone.fromDateValueAndNanos(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos(), ((ValueTimeTimeZone) value2).getTimeZoneOffsetSeconds());
                    break;
                }
                break;
        }
        long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
        long j = dateAndTimeFromValue[0];
        long nanos = dateAndTimeFromValue[1] + (value2 instanceof ValueTime ? ((ValueTime) value2).getNanos() : ((ValueTimeTimeZone) value2).getNanos());
        if (nanos >= DateTimeUtils.NANOS_PER_DAY) {
            nanos -= DateTimeUtils.NANOS_PER_DAY;
            j = DateTimeUtils.incrementDateValue(j);
        }
        return DateTimeUtils.dateTimeToValue(value, j, nanos);
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
