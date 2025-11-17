package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/ConcatFunction.class */
public final class ConcatFunction extends FunctionN {
    public static final int CONCAT = 0;
    public static final int CONCAT_WS = 1;
    private static final String[] NAMES = {"CONCAT", "CONCAT_WS"};
    private final int function;

    public ConcatFunction(int i) {
        this(i, new Expression[4]);
    }

    public ConcatFunction(int i, Expression... expressionArr) {
        super(expressionArr);
        this.function = i;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        int i = 0;
        String str = null;
        if (this.function == 1) {
            i = 1;
            str = this.args[0].getValue(sessionLocal).getString();
        }
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        int length = this.args.length;
        while (i < length) {
            Value value = this.args[i].getValue(sessionLocal);
            if (value != ValueNull.INSTANCE) {
                if (str != null) {
                    if (z) {
                        sb.append(str);
                    }
                    z = true;
                }
                sb.append(value.getString());
            }
            i++;
        }
        return ValueVarchar.get(sb.toString(), sessionLocal);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        int i = 0;
        long j = 0;
        if (this.function == 1) {
            i = 1;
            j = getPrecision(0);
        }
        long j2 = 0;
        int length = this.args.length;
        boolean z = false;
        while (i < length) {
            if (!this.args[i].isNullConstant()) {
                j2 = DataType.addPrecision(j2, getPrecision(i));
                if (j != 0 && z) {
                    j2 = DataType.addPrecision(j2, j);
                }
                z = true;
            }
            i++;
        }
        this.type = TypeInfo.getTypeInfo(2, j2, 0, null);
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    private long getPrecision(int i) {
        TypeInfo type = this.args[i].getType();
        int valueType = type.getValueType();
        if (valueType == 0) {
            return 0L;
        }
        if (DataType.isCharacterStringType(valueType)) {
            return type.getPrecision();
        }
        return Long.MAX_VALUE;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
