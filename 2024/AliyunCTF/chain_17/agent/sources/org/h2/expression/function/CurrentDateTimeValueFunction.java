package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CurrentDateTimeValueFunction.class */
public final class CurrentDateTimeValueFunction extends Operation0 implements NamedExpression {
    public static final int CURRENT_DATE = 0;
    public static final int CURRENT_TIME = 1;
    public static final int LOCALTIME = 2;
    public static final int CURRENT_TIMESTAMP = 3;
    public static final int LOCALTIMESTAMP = 4;
    private static final int[] TYPES = {17, 19, 18, 21, 20};
    private static final String[] NAMES = {"CURRENT_DATE", "CURRENT_TIME", "LOCALTIME", "CURRENT_TIMESTAMP", "LOCALTIMESTAMP"};
    private final int function;
    private final int scale;
    private final TypeInfo type;

    public static String getName(int i) {
        return NAMES[i];
    }

    public CurrentDateTimeValueFunction(int i, int i2) {
        this.function = i;
        this.scale = i2;
        if (i2 < 0) {
            i2 = i >= 3 ? 6 : 0;
        }
        this.type = TypeInfo.getTypeInfo(TYPES[i], 0L, i2, null);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return sessionLocal.currentTimestamp().castTo(this.type, sessionLocal);
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
