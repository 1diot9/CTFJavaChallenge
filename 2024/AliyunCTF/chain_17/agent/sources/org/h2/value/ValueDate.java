package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueDate.class */
public final class ValueDate extends Value {
    public static final int PRECISION = 10;
    private final long dateValue;

    private ValueDate(long j) {
        if (j < DateTimeUtils.MIN_DATE_VALUE || j > DateTimeUtils.MAX_DATE_VALUE) {
            throw new IllegalArgumentException("dateValue out of range " + j);
        }
        this.dateValue = j;
    }

    public static ValueDate fromDateValue(long j) {
        return (ValueDate) Value.cache(new ValueDate(j));
    }

    public static ValueDate parse(String str) {
        try {
            return fromDateValue(DateTimeUtils.parseDateValue(str, 0, str.length()));
        } catch (Exception e) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "DATE", str);
        }
    }

    public long getDateValue() {
        return this.dateValue;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_DATE;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 17;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return DateTimeUtils.appendDate(new StringBuilder(10), this.dateValue).toString();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return DateTimeUtils.appendDate(sb.append("DATE '"), this.dateValue).append('\'');
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Long.compare(this.dateValue, ((ValueDate) value).dateValue);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return this == obj || ((obj instanceof ValueDate) && this.dateValue == ((ValueDate) obj).dateValue);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) (this.dateValue ^ (this.dateValue >>> 32));
    }
}
