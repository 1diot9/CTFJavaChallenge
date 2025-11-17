package org.h2.expression.function;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/StringFunction1.class */
public final class StringFunction1 extends Function1 {
    public static final int UPPER = 0;
    public static final int LOWER = 1;
    public static final int ASCII = 2;
    public static final int CHAR = 3;
    public static final int STRINGENCODE = 4;
    public static final int STRINGDECODE = 5;
    public static final int STRINGTOUTF8 = 6;
    public static final int UTF8TOSTRING = 7;
    public static final int HEXTORAW = 8;
    public static final int RAWTOHEX = 9;
    public static final int SPACE = 10;
    public static final int QUOTE_IDENT = 11;
    private static final String[] NAMES = {"UPPER", "LOWER", "ASCII", "CHAR", "STRINGENCODE", "STRINGDECODE", "STRINGTOUTF8", "UTF8TOSTRING", "HEXTORAW", "RAWTOHEX", "SPACE", "QUOTE_IDENT"};
    private final int function;

    public StringFunction1(Expression expression, int i) {
        super(expression);
        this.function = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value;
        Value value2 = this.arg.getValue(sessionLocal);
        if (value2 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        switch (this.function) {
            case 0:
                value = ValueVarchar.get(value2.getString().toUpperCase(), sessionLocal);
                break;
            case 1:
                value = ValueVarchar.get(value2.getString().toLowerCase(), sessionLocal);
                break;
            case 2:
                String string = value2.getString();
                value = string.isEmpty() ? ValueNull.INSTANCE : ValueInteger.get(string.charAt(0));
                break;
            case 3:
                value = ValueVarchar.get(String.valueOf((char) value2.getInt()), sessionLocal);
                break;
            case 4:
                value = ValueVarchar.get(StringUtils.javaEncode(value2.getString()), sessionLocal);
                break;
            case 5:
                value = ValueVarchar.get(StringUtils.javaDecode(value2.getString()), sessionLocal);
                break;
            case 6:
                value = ValueVarbinary.getNoCopy(value2.getString().getBytes(StandardCharsets.UTF_8));
                break;
            case 7:
                value = ValueVarchar.get(new String(value2.getBytesNoCopy(), StandardCharsets.UTF_8), sessionLocal);
                break;
            case 8:
                value = hexToRaw(value2.getString(), sessionLocal);
                break;
            case 9:
                value = ValueVarchar.get(rawToHex(value2, sessionLocal.getMode()), sessionLocal);
                break;
            case 10:
                byte[] bArr = new byte[Math.max(0, value2.getInt())];
                Arrays.fill(bArr, (byte) 32);
                value = ValueVarchar.get(new String(bArr, StandardCharsets.ISO_8859_1), sessionLocal);
                break;
            case 11:
                value = ValueVarchar.get(StringUtils.quoteIdentifier(value2.getString()), sessionLocal);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return value;
    }

    private static Value hexToRaw(String str, SessionLocal sessionLocal) {
        if (sessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle) {
            return ValueVarbinary.get(StringUtils.convertHexToBytes(str));
        }
        int length = str.length();
        if (length % 4 != 0) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, str);
        }
        StringBuilder sb = new StringBuilder(length / 4);
        for (int i = 0; i < length; i += 4) {
            try {
                sb.append((char) Integer.parseInt(str.substring(i, i + 4), 16));
            } catch (NumberFormatException e) {
                throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, str);
            }
        }
        return ValueVarchar.get(sb.toString(), sessionLocal);
    }

    private static String rawToHex(Value value, Mode mode) {
        if (DataType.isBinaryStringOrSpecialBinaryType(value.getValueType())) {
            return StringUtils.convertBytesToHex(value.getBytesNoCopy());
        }
        String string = value.getString();
        if (mode.getEnum() == Mode.ModeEnum.Oracle) {
            return StringUtils.convertBytesToHex(string.getBytes(StandardCharsets.UTF_8));
        }
        int length = string.length();
        StringBuilder sb = new StringBuilder(4 * length);
        for (int i = 0; i < length; i++) {
            String hexString = Integer.toHexString(string.charAt(i) & 65535);
            for (int length2 = hexString.length(); length2 < 4; length2++) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        int i;
        this.arg = this.arg.optimize(sessionLocal);
        switch (this.function) {
            case 0:
            case 1:
            case 4:
            case 10:
            case 11:
                this.type = TypeInfo.TYPE_VARCHAR;
                break;
            case 2:
                this.type = TypeInfo.TYPE_INTEGER;
                break;
            case 3:
                this.type = TypeInfo.getTypeInfo(2, 1L, 0, null);
                break;
            case 5:
                TypeInfo type = this.arg.getType();
                this.type = DataType.isCharacterStringType(type.getValueType()) ? TypeInfo.getTypeInfo(2, type.getPrecision(), 0, null) : TypeInfo.TYPE_VARCHAR;
                break;
            case 6:
                this.type = TypeInfo.TYPE_VARBINARY;
                break;
            case 7:
                TypeInfo type2 = this.arg.getType();
                this.type = DataType.isBinaryStringType(type2.getValueType()) ? TypeInfo.getTypeInfo(2, type2.getPrecision(), 0, null) : TypeInfo.TYPE_VARCHAR;
                break;
            case 8:
                TypeInfo type3 = this.arg.getType();
                if (sessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle) {
                    if (DataType.isCharacterStringType(type3.getValueType())) {
                        this.type = TypeInfo.getTypeInfo(6, type3.getPrecision() / 2, 0, null);
                        break;
                    } else {
                        this.type = TypeInfo.TYPE_VARBINARY;
                        break;
                    }
                } else if (DataType.isCharacterStringType(type3.getValueType())) {
                    this.type = TypeInfo.getTypeInfo(2, type3.getPrecision() / 4, 0, null);
                    break;
                } else {
                    this.type = TypeInfo.TYPE_VARCHAR;
                    break;
                }
            case 9:
                TypeInfo type4 = this.arg.getType();
                long precision = type4.getPrecision();
                if (DataType.isBinaryStringOrSpecialBinaryType(type4.getValueType())) {
                    i = 2;
                } else {
                    i = sessionLocal.getMode().getEnum() == Mode.ModeEnum.Oracle ? 6 : 4;
                }
                int i2 = i;
                this.type = TypeInfo.getTypeInfo(2, precision <= Long.MAX_VALUE / ((long) i2) ? precision * i2 : Long.MAX_VALUE, 0, null);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        if (this.arg.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
