package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CoalesceFunction.class */
public final class CoalesceFunction extends FunctionN {
    public static final int COALESCE = 0;
    public static final int GREATEST = 1;
    public static final int LEAST = 2;
    private static final String[] NAMES = {"COALESCE", "GREATEST", "LEAST"};
    private final int function;
    private boolean ignoreNulls;

    public CoalesceFunction(int i) {
        this(i, new Expression[4]);
    }

    public CoalesceFunction(int i, Expression... expressionArr) {
        super(expressionArr);
        this.function = i;
    }

    public void setIgnoreNulls(boolean z) {
        this.ignoreNulls = z;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value greatestOrLeast;
        switch (this.function) {
            case 0:
                greatestOrLeast = ValueNull.INSTANCE;
                int i = 0;
                int length = this.args.length;
                while (true) {
                    if (i >= length) {
                        break;
                    } else {
                        Value value = this.args[i].getValue(sessionLocal);
                        if (value == ValueNull.INSTANCE) {
                            i++;
                        } else {
                            greatestOrLeast = value.convertTo(this.type, sessionLocal);
                            break;
                        }
                    }
                }
            case 1:
            case 2:
                greatestOrLeast = greatestOrLeast(sessionLocal);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return greatestOrLeast;
    }

    private Value greatestOrLeast(SessionLocal sessionLocal) {
        Value value = ValueNull.INSTANCE;
        Value value2 = null;
        int length = this.args.length;
        for (int i = 0; i < length; i++) {
            Value value3 = this.args[i].getValue(sessionLocal);
            if (value3 != ValueNull.INSTANCE) {
                Value convertTo = value3.convertTo(this.type, sessionLocal);
                if (value == ValueNull.INSTANCE) {
                    if (value2 == null) {
                        value = convertTo;
                    } else {
                        int compareWithNull = sessionLocal.compareWithNull(value2, convertTo, false);
                        if (compareWithNull == Integer.MIN_VALUE) {
                            value2 = getWithNull(value2, convertTo);
                        } else if (test(compareWithNull)) {
                            value = convertTo;
                            value2 = null;
                        }
                    }
                } else {
                    int compareWithNull2 = sessionLocal.compareWithNull(value, convertTo, false);
                    if (compareWithNull2 == Integer.MIN_VALUE) {
                        if (i + 1 == length) {
                            return ValueNull.INSTANCE;
                        }
                        value2 = getWithNull(value, convertTo);
                        value = ValueNull.INSTANCE;
                    } else if (test(compareWithNull2)) {
                        value = convertTo;
                    }
                }
            } else if (!this.ignoreNulls) {
                return ValueNull.INSTANCE;
            }
        }
        return value;
    }

    private static Value getWithNull(Value value, Value value2) {
        Value valueWithFirstNull = value.getValueWithFirstNull(value2);
        return valueWithFirstNull != null ? valueWithFirstNull : value;
    }

    private boolean test(int i) {
        return this.function == 1 ? i < 0 : i > 0;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        this.type = TypeInfo.getHigherType(this.args);
        if (this.type.getValueType() <= 0) {
            this.type = TypeInfo.TYPE_VARCHAR;
        }
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        super.getUnenclosedSQL(sb, i);
        if (this.function == 1 || this.function == 2) {
            sb.append(this.ignoreNulls ? " IGNORE NULLS" : " RESPECT NULLS");
        }
        return sb;
    }
}
