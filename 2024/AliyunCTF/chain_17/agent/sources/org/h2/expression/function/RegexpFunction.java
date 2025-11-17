package org.h2.expression.function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.h2.api.ErrorCode;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/RegexpFunction.class */
public final class RegexpFunction extends FunctionN {
    public static final int REGEXP_LIKE = 0;
    public static final int REGEXP_REPLACE = 1;
    public static final int REGEXP_SUBSTR = 2;
    private static final String[] NAMES = {"REGEXP_LIKE", "REGEXP_REPLACE", "REGEXP_SUBSTR"};
    private final int function;

    public RegexpFunction(int i) {
        super(new Expression[i == 0 ? 3 : 6]);
        this.function = i;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value regexpSubstr;
        Value value = this.args[0].getValue(sessionLocal);
        Value value2 = this.args[1].getValue(sessionLocal);
        int length = this.args.length;
        switch (this.function) {
            case 0:
                Value value3 = length >= 3 ? this.args[2].getValue(sessionLocal) : null;
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                String string = value2.getString();
                try {
                    regexpSubstr = ValueBoolean.get(Pattern.compile(string, makeRegexpFlags(value3 != null ? value3.getString() : null, false)).matcher(value.getString()).find());
                    break;
                } catch (PatternSyntaxException e) {
                    throw DbException.get(ErrorCode.LIKE_ESCAPE_ERROR_1, e, string);
                }
            case 1:
                String string2 = value.getString();
                if (sessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle) {
                    String string3 = this.args[2].getValue(sessionLocal).getString();
                    int i = length >= 4 ? this.args[3].getValue(sessionLocal).getInt() : 1;
                    int i2 = length >= 5 ? this.args[4].getValue(sessionLocal).getInt() : 0;
                    String string4 = length >= 6 ? this.args[5].getValue(sessionLocal).getString() : null;
                    if (string2 == null) {
                        regexpSubstr = ValueNull.INSTANCE;
                        break;
                    } else {
                        String string5 = value2.getString();
                        regexpSubstr = regexpReplace(sessionLocal, string2, string5 != null ? string5 : "", string3 != null ? string3 : "", i, i2, string4);
                        break;
                    }
                } else {
                    if (length > 4) {
                        throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "3..4");
                    }
                    Value value4 = this.args[2].getValue(sessionLocal);
                    Value value5 = length == 4 ? this.args[3].getValue(sessionLocal) : null;
                    if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE || value4 == ValueNull.INSTANCE || value5 == ValueNull.INSTANCE) {
                        regexpSubstr = ValueNull.INSTANCE;
                        break;
                    } else {
                        regexpSubstr = regexpReplace(sessionLocal, string2, value2.getString(), value4.getString(), 1, 0, value5 != null ? value5.getString() : null);
                        break;
                    }
                }
                break;
            case 2:
                regexpSubstr = regexpSubstr(value, value2, length >= 3 ? this.args[2].getValue(sessionLocal) : null, length >= 4 ? this.args[3].getValue(sessionLocal) : null, length >= 5 ? this.args[4].getValue(sessionLocal) : null, length >= 6 ? this.args[5].getValue(sessionLocal) : null, sessionLocal);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return regexpSubstr;
    }

    private static Value regexpReplace(SessionLocal sessionLocal, String str, String str2, String str3, int i, int i2, String str4) {
        Mode mode = sessionLocal.getMode();
        if (mode.regexpReplaceBackslashReferences && (str3.indexOf(92) >= 0 || str3.indexOf(36) >= 0)) {
            StringBuilder sb = new StringBuilder();
            int i3 = 0;
            while (i3 < str3.length()) {
                char charAt = str3.charAt(i3);
                if (charAt == '$') {
                    sb.append('\\');
                } else if (charAt == '\\') {
                    i3++;
                    if (i3 < str3.length()) {
                        charAt = str3.charAt(i3);
                        sb.append((charAt < '0' || charAt > '9') ? '\\' : '$');
                    }
                }
                sb.append(charAt);
                i3++;
            }
            str3 = sb.toString();
        }
        boolean z = mode.getEnum() == Mode.ModeEnum.PostgreSQL;
        int makeRegexpFlags = makeRegexpFlags(str4, z);
        if (z && (str4 == null || str4.isEmpty() || !str4.contains("g"))) {
            i2 = 1;
        }
        try {
            try {
                Matcher region = Pattern.compile(str2, makeRegexpFlags).matcher(str).region(i - 1, str.length());
                if (i2 == 0) {
                    return ValueVarchar.get(region.replaceAll(str3), sessionLocal);
                }
                StringBuffer stringBuffer = new StringBuffer();
                int i4 = 1;
                while (true) {
                    if (!region.find()) {
                        break;
                    }
                    if (i4 == i2) {
                        region.appendReplacement(stringBuffer, str3);
                        break;
                    }
                    i4++;
                }
                region.appendTail(stringBuffer);
                return ValueVarchar.get(stringBuffer.toString(), sessionLocal);
            } catch (PatternSyntaxException e) {
                throw DbException.get(ErrorCode.LIKE_ESCAPE_ERROR_1, e, str2);
            }
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e2) {
            throw DbException.get(ErrorCode.LIKE_ESCAPE_ERROR_1, e2, str3);
        }
    }

    private static Value regexpSubstr(Value value, Value value2, Value value3, Value value4, Value value5, Value value6, SessionLocal sessionLocal) {
        if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE || value4 == ValueNull.INSTANCE || value6 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        String string = value2.getString();
        int i = value3 != null ? value3.getInt() - 1 : 0;
        int i2 = value4 != null ? value4.getInt() : 1;
        String string2 = value5 != null ? value5.getString() : null;
        int i3 = value6 != null ? value6.getInt() : 0;
        try {
            Matcher matcher = Pattern.compile(string, makeRegexpFlags(string2, false)).matcher(value.getString());
            boolean find = matcher.find(i);
            for (int i4 = 1; i4 < i2 && find; i4++) {
                find = matcher.find();
            }
            if (!find) {
                return ValueNull.INSTANCE;
            }
            return ValueVarchar.get(matcher.group(i3), sessionLocal);
        } catch (IndexOutOfBoundsException e) {
            return ValueNull.INSTANCE;
        } catch (PatternSyntaxException e2) {
            throw DbException.get(ErrorCode.LIKE_ESCAPE_ERROR_1, e2, string);
        }
    }

    private static int makeRegexpFlags(String str, boolean z) {
        int i = 64;
        if (str != null) {
            for (int i2 = 0; i2 < str.length(); i2++) {
                switch (str.charAt(i2)) {
                    case 'c':
                        i &= -3;
                        break;
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'h':
                    case 'j':
                    case Opcodes.DMUL /* 107 */:
                    case 'l':
                    default:
                        throw DbException.get(ErrorCode.INVALID_VALUE_2, str);
                    case 'g':
                        if (!z) {
                            throw DbException.get(ErrorCode.INVALID_VALUE_2, str);
                        }
                        break;
                    case 'i':
                        i |= 2;
                        break;
                    case Opcodes.LDIV /* 109 */:
                        i |= 8;
                        break;
                    case Opcodes.FDIV /* 110 */:
                        i |= 32;
                        break;
                }
            }
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        int i;
        int i2;
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        switch (this.function) {
            case 0:
                i = 2;
                i2 = 3;
                this.type = TypeInfo.TYPE_BOOLEAN;
                break;
            case 1:
                i = 3;
                i2 = 6;
                this.type = TypeInfo.TYPE_VARCHAR;
                break;
            case 2:
                i = 2;
                i2 = 6;
                this.type = TypeInfo.TYPE_VARCHAR;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        int length = this.args.length;
        if (length < i || length > i2) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), i + ".." + i2);
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
