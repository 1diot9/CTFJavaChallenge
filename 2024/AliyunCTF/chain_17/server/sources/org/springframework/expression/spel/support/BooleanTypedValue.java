package org.springframework.expression.spel.support;

import org.springframework.expression.TypedValue;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/support/BooleanTypedValue.class */
public final class BooleanTypedValue extends TypedValue {
    public static final BooleanTypedValue TRUE = new BooleanTypedValue(true);
    public static final BooleanTypedValue FALSE = new BooleanTypedValue(false);

    private BooleanTypedValue(boolean b) {
        super(Boolean.valueOf(b));
    }

    public static BooleanTypedValue forValue(boolean b) {
        return b ? TRUE : FALSE;
    }
}
