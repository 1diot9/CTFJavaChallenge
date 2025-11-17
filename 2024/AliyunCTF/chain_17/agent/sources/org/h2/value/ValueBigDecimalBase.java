package org.h2.value;

import java.math.BigDecimal;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueBigDecimalBase.class */
public abstract class ValueBigDecimalBase extends Value {
    final BigDecimal value;
    TypeInfo type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueBigDecimalBase(BigDecimal bigDecimal) {
        if (bigDecimal != null) {
            if (bigDecimal.getClass() != BigDecimal.class) {
                throw DbException.get(ErrorCode.INVALID_CLASS_2, BigDecimal.class.getName(), bigDecimal.getClass().getName());
            }
            int precision = bigDecimal.precision();
            if (precision > 100000) {
                throw DbException.getValueTooLongException(getTypeName(getValueType()), bigDecimal.toString(), precision);
            }
        }
        this.value = bigDecimal;
    }
}
