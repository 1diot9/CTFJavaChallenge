package org.h2.expression.function;

import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/StringFunction.class */
public final class StringFunction extends FunctionN {
    public static final int LOCATE = 0;
    public static final int INSERT = 1;
    public static final int REPLACE = 2;
    public static final int LPAD = 3;
    public static final int RPAD = 4;
    public static final int TRANSLATE = 5;
    private static final String[] NAMES = {"LOCATE", "INSERT", "REPLACE", "LPAD", "RPAD", "TRANSLATE"};
    private final int function;

    public StringFunction(Expression expression, Expression expression2, Expression expression3, int i) {
        super(expression3 == null ? new Expression[]{expression, expression2} : new Expression[]{expression, expression2, expression3});
        this.function = i;
    }

    public StringFunction(Expression expression, Expression expression2, Expression expression3, Expression expression4, int i) {
        super(new Expression[]{expression, expression2, expression3, expression4});
        this.function = i;
    }

    public StringFunction(Expression[] expressionArr, int i) {
        super(expressionArr);
        this.function = i;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        String str;
        String str2;
        Value value = this.args[0].getValue(sessionLocal);
        Value value2 = this.args[1].getValue(sessionLocal);
        switch (this.function) {
            case 0:
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                Value value3 = this.args.length >= 3 ? this.args[2].getValue(sessionLocal) : null;
                if (value3 != ValueNull.INSTANCE) {
                    value = ValueInteger.get(locate(value.getString(), value2.getString(), value3 == null ? 1 : value3.getInt()));
                    break;
                } else {
                    return ValueNull.INSTANCE;
                }
                break;
            case 1:
                Value value4 = this.args[2].getValue(sessionLocal);
                Value value5 = this.args[3].getValue(sessionLocal);
                if (value2 != ValueNull.INSTANCE && value4 != ValueNull.INSTANCE) {
                    String insert = insert(value.getString(), value2.getInt(), value4.getInt(), value5.getString());
                    value = insert != null ? ValueVarchar.get(insert, sessionLocal) : ValueNull.INSTANCE;
                    break;
                }
                break;
            case 2:
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                if (this.args.length >= 3) {
                    Value value6 = this.args[2].getValue(sessionLocal);
                    if (value6 == ValueNull.INSTANCE && sessionLocal.getMode().getEnum() != Mode.ModeEnum.Oracle) {
                        return ValueNull.INSTANCE;
                    }
                    str2 = value6.getString();
                    if (str2 == null) {
                        str2 = "";
                    }
                } else {
                    str2 = "";
                }
                value = ValueVarchar.get(StringUtils.replaceAll(value.getString(), value2.getString(), str2), sessionLocal);
                break;
            case 3:
            case 4:
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                if (this.args.length >= 3) {
                    Value value7 = this.args[2].getValue(sessionLocal);
                    if (value7 == ValueNull.INSTANCE) {
                        return ValueNull.INSTANCE;
                    }
                    str = value7.getString();
                } else {
                    str = null;
                }
                value = ValueVarchar.get(StringUtils.pad(value.getString(), value2.getInt(), str, this.function == 4), sessionLocal);
                break;
            case 5:
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                Value value8 = this.args[2].getValue(sessionLocal);
                if (value8 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                String string = value2.getString();
                String string2 = value8.getString();
                if (sessionLocal.getMode().getEnum() == Mode.ModeEnum.DB2) {
                    string = string2;
                    string2 = string;
                }
                value = ValueVarchar.get(translate(value.getString(), string, string2), sessionLocal);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return value;
    }

    private static int locate(String str, String str2, int i) {
        if (i < 0) {
            return str2.lastIndexOf(str, str2.length() + i) + 1;
        }
        return str2.indexOf(str, i == 0 ? 0 : i - 1) + 1;
    }

    private static String insert(String str, int i, int i2, String str2) {
        if (str == null) {
            return str2;
        }
        if (str2 == null) {
            return str;
        }
        int length = str.length();
        int length2 = str2.length();
        int i3 = i - 1;
        if (i3 < 0 || i2 <= 0 || length2 == 0 || i3 > length) {
            return str;
        }
        if (i3 + i2 > length) {
            i2 = length - i3;
        }
        return str.substring(0, i3) + str2 + str.substring(i3 + i2);
    }

    private static String translate(String str, String str2, String str3) {
        if (StringUtils.isNullOrEmpty(str) || StringUtils.isNullOrEmpty(str2)) {
            return str;
        }
        StringBuilder sb = null;
        int length = str3 == null ? 0 : str3.length();
        int length2 = str.length();
        for (int i = 0; i < length2; i++) {
            char charAt = str.charAt(i);
            int indexOf = str2.indexOf(charAt);
            if (indexOf >= 0) {
                if (sb == null) {
                    sb = new StringBuilder(length2);
                    if (i > 0) {
                        sb.append((CharSequence) str, 0, i);
                    }
                }
                if (indexOf < length) {
                    charAt = str3.charAt(indexOf);
                }
            }
            if (sb != null) {
                sb.append(charAt);
            }
        }
        return sb == null ? str : sb.toString();
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        switch (this.function) {
            case 0:
                this.type = TypeInfo.TYPE_INTEGER;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                this.type = TypeInfo.TYPE_VARCHAR;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
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
}
